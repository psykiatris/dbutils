package org.palczewski.connect;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;

import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.PooledConnection;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;


/*
Proposed class to managed pooled connections. Attempting to utilize the
 mysql Connector/J's PoolDataSource   class.

Based on a class developed by Christian d'Heureuse, Inventec Informatik AG, Zurich, Switzerland.

When the program is run, a DataSource object is created and passed
to PoolManager, then a connection is established using Poolmanager's
getConnection() method.
 */
public class PoolManager {


    private final String DISPOSED = "Connection pool has been disposed.";
    private final MysqlConnectionPoolDataSource dataSource;
    private final int maxConnections;
    private final long timeoutMs;
    private final PrintWriter logWriter;
    private final Semaphore semaphore;
    private final PoolManager.PoolConnectionEventListener poolConnectionEventListener;

    // The following variables must only be accessed within synchronized blocks.
// @GuardedBy("this") could be used in the future.
    private final LinkedList<PooledConnection> recycledConnections;          // list of inactive PooledConnections
    private int activeConnections;            // number of active (open) connections of this pool
    private boolean isDisposed;                   // true if this connection pool has been disposed
    private boolean doPurgeConnection;            // flag to purge the connection currently beeing closed instead of recycling it
    private PooledConnection connectionInTransition;       // a PooledConnection which is currently within a PooledConnection.getConnection() call, or null

    public static class TimeoutException extends RuntimeException {

        private static final long serialVersionUID = 1;

        public TimeoutException() {
            super("Timeout while waiting for a free database connection.");
        }

        public TimeoutException(String msg) {
            super(msg);
        }
    }

    public PoolManager(MysqlConnectionPoolDataSource dataSource,
                  int maxConnections) {
        this(dataSource, maxConnections, 60);
    }

    public PoolManager(MysqlConnectionPoolDataSource dataSource,
                    int maxConnections,
                                     int timeout) {
        this.dataSource = dataSource;
        this.maxConnections = maxConnections;
        timeoutMs = timeout * 1000L;
        logWriter = dataSource.getLogWriter();
        if (maxConnections < 1) {
            throw new IllegalArgumentException("Invalid maxConnections value.");
        }
        semaphore = new Semaphore(maxConnections, true);
        recycledConnections = new LinkedList<>();
        poolConnectionEventListener = new PoolManager.PoolConnectionEventListener();
    }

    /**
     * Closes all unused pooled connections.
     */
    public synchronized void dispose() throws SQLException {
        if (isDisposed) {
            return;
        }
        isDisposed = true;
        SQLException e = null;
        while (!recycledConnections.isEmpty()) {
            PooledConnection pconn = recycledConnections.remove();
            try {
                pconn.close();
            } catch (SQLException e2) {
                if (e == null) {
                    e = e2;
                }
            }
        }
        if (e != null) {
            throw e;
        }
    }

    /**
     * Retrieves a connection from the connection pool.
     *
     * <p>If <code>maxConnections</code> connections are already in use, the method
     * waits until a connection becomes available or <code>timeout</code> seconds elapsed.
     * When the application is finished using the connection, it must close it
     * in order to return it to the pool.
     *
     * @return a new <code>Connection</code> object.
     * @throws PoolManager.TimeoutException when no connection becomes available within <code>timeout</code>
     * seconds.
     */
    public Connection getConnection() throws SQLException {
        return getConnection2(timeoutMs);
    }

    private Connection getConnection2(long timeoutMs) throws SQLException {
        // This routine is unsynchronized, because semaphore.tryAcquire() may block.
        synchronized (this) {
            if (isDisposed) {
                throw new IllegalStateException(DISPOSED);
            }
        }
        try {
            if (!semaphore.tryAcquire(timeoutMs, TimeUnit.MILLISECONDS)) {
                throw new PoolManager.TimeoutException("Error with setting semaphore");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while waiting for a database connection.", e);
        }
        boolean ok = false;
        try {
            Connection conn = getConnection3();
            ok = true;
            return conn;
        } finally {
            if (!ok) {
                semaphore.release();
            }
        }
    }

    private synchronized Connection getConnection3() throws SQLException {
        if (isDisposed) {                                       // test again within synchronized lock
            throw new IllegalStateException(DISPOSED);
        }
        PooledConnection pconn;
        if (!recycledConnections.isEmpty()) {
            pconn = recycledConnections.remove();
        } else {
            pconn = dataSource.getPooledConnection();
            pconn.addConnectionEventListener(poolConnectionEventListener);
        }
        Connection conn;
        try {
            // The JDBC driver may call ConnectionEventListener.connectionErrorOccurred()
            // from within PooledConnection.getConnection(). To detect this within
            // disposeConnection(), we temporarily set connectionInTransition.
            connectionInTransition = pconn;
            conn = pconn.getConnection();
        } finally {
            connectionInTransition = null;
        }
        activeConnections++;
        assertInnerState();
        return conn;
    }

    /**
     * Retrieves a connection from the connection pool and ensures that it is valid
     * by calling {@link Connection#isValid(int)}.
     *
     */
    public Connection getValidConnection() {
        long time = System.currentTimeMillis();
        long timeoutTime = time + timeoutMs;
        int triesWithoutDelay = getInactiveConnections() + 1;
        while (true) {
            Connection conn = getValidConnection2(time, timeoutTime);
            if (conn != null) {
                return conn;
            }
            triesWithoutDelay--;
            if (triesWithoutDelay <= 0) {
                triesWithoutDelay = 0;
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    throw new RuntimeException(
                            "Interrupted while waiting for a valid database connection.", e);
                }
            }
            time = System.currentTimeMillis();
            if (time >= timeoutTime) {
                throw new PoolManager.TimeoutException(
                        "Timeout while waiting for a valid database connection.");
            }
        }
    }

    private Connection getValidConnection2(long time, long timeoutTime) {
        long rtime = Math.max(1, timeoutTime - time);
        Connection conn;
        try {
            conn = getConnection2(rtime);
        } catch (SQLException e) {
            return null;
        }
        rtime = timeoutTime - System.currentTimeMillis();
        int rtimeSecs = Math.max(1, (int) ((rtime + 999) / 1000));
        try {
            if (conn.isValid(rtimeSecs)) {
                return conn;
            }
        } catch (SQLException e) {
        }
        // This Exception should never occur. If it nevertheless occurs, it's because of an error in the
        // JDBC driver which we ignore and assume that the connection is not valid.
        // When isValid() returns false, the JDBC driver should have already called connectionErrorOccurred()
        // and the PooledConnection has been removed from the pool, i.e. the PooledConnection will
        // not be added to recycledConnections when Connection.close() is called.
        // But to be sure that this works even with a faulty JDBC driver, we call purgeConnection().
        purgeConnection(conn);
        return null;
    }

    // Purges the PooledConnection associated with the passed Connection from the connection pool.
    private synchronized void purgeConnection(Connection conn) {
        try {
            doPurgeConnection = true;
            // (A potential problem of this program logic is that setting the doPurgeConnection flag
            // has an effect only if the JDBC driver calls connectionClosed() synchronously within
            // Connection.close().)
            conn.close();
        } catch (SQLException e) {
        }
        // ignore exception from close()
        finally {
            doPurgeConnection = false;
        }
    }

    private synchronized void recycleConnection(PooledConnection pconn) {
        if (isDisposed || doPurgeConnection) {
            disposeConnection(pconn);
            return;
        }
        if (pconn == connectionInTransition) {
            // This happens when a faulty JDBC driver calls ConnectionEventListener.connectionClosed()
            // a second time within PooledConnection.getConnection().
            return;
        }
        if (activeConnections <= 0) {
            throw new AssertionError();
        }
        activeConnections--;
        semaphore.release();
        recycledConnections.add(pconn);
        assertInnerState();
    }

    private synchronized void disposeConnection(PooledConnection pconn) {
        pconn.removeConnectionEventListener(poolConnectionEventListener);
        if (!recycledConnections.remove(pconn) && pconn != connectionInTransition) {
            // If the PooledConnection is not in the recycledConnections list
            // and is not currently within a PooledConnection.getConnection() call,
            // we assume that the connection was active.
            if (activeConnections <= 0) {
                throw new AssertionError();
            }
            activeConnections--;
            semaphore.release();
        }
        closeConnectionAndIgnoreException(pconn);
        assertInnerState();
    }

    private void closeConnectionAndIgnoreException(PooledConnection pconn) {
        try {
            pconn.close();
        } catch (SQLException e) {
            log("Error while closing database connection: " + e);
        }
    }

    private void log(String msg) {
        String s = "PoolManager: " + msg;
        try {
            if (logWriter == null) {
                System.err.println(s);
            } else {
                logWriter.println(s);
            }
        } catch (Exception e) {
        }
    }

    private synchronized void assertInnerState() {
        if (activeConnections < 0) {
            throw new AssertionError();
        }
        if (activeConnections + recycledConnections.size() > maxConnections) {
            throw new AssertionError();
        }
        if (activeConnections + semaphore.availablePermits() > maxConnections) {
            throw new AssertionError();
        }
    }

    private class PoolConnectionEventListener implements ConnectionEventListener {

        public void connectionClosed(ConnectionEvent event) {
            PooledConnection pconn = (PooledConnection) event.getSource();
            recycleConnection(pconn);
        }

        public void connectionErrorOccurred(ConnectionEvent event) {
            PooledConnection pconn = (PooledConnection) event.getSource();
            disposeConnection(pconn);
        }
    }

    /**
     * Returns the number of active (open) connections of this pool.
     *
     **/
    public synchronized int getActiveConnections() {
        return activeConnections;
    }

    /**
     * Returns the number of inactive (unused) connections in this pool.
     *
     **/
    public synchronized int getInactiveConnections() {
        return recycledConnections.size();
    }



}
