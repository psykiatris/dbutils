package org.palczewski.proposed;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlPooledConnection;


import javax.sql.ConnectionEventListener;
import javax.sql.PooledConnection;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;




/*
Proposed class to managed pooled connections
 */
public class PoolManager implements MysqlConnectionPoolDataSource,
        ConnectionEventListener {



    private static final int DEFAULT_MAX_POOL_SIZE = 8;
    private boolean isPoolClosed = false;
    private int sessionTimeout = 0;
    private MysqlConnectionPoolDataSource connectionPoolDatasource = null;
    private Set connectionInUse = new HashSet(1);
    private List connectionsInactive = new ArrayList<>(1);
    private Map sessionConnectionWrappers = new HashMap<>(1);
    private int maxPoolSize = DEFAULT_MAX_POOL_SIZE;
    private boolean initialized = false;
    private int initialSize = 0;

    // some booleans
    boolean doResetAutoCommit = false;
    boolean doResetReadOnly = false
    boolean doResetTrasactionIsolation = false;
    boolean doResetCatalog = false;

    boolean isAutoCommit = true;
    boolean isReadOnlye = false;
    int transactionIsolation = Connection.TRANSACTION_READ_COMMITTED;
    String catalog;

    // optional query to validate connections
    private String validationQuery = null;


    public PoolManager() {
        connectionPoolDatasource =
                new MysqlConnectionPoolDataSource();

    }

    public PoolManager(String url, String user, String password,
                       int maxPoolSize) throws SQLException {

        connectionPoolDatasource =
                new MysqlConnectionPoolDataSource();


    }

    public synchronized String getUrl() {
        return connectionPoolDatasource.getUrl();
    }

    public synchronized void setUrl(String uel) {
        connectionPoolDatasource.setUrl(url);

    }

    public synchronized String getUser() {
        connectionPoolDatasource.getUser();
    }

    public synchronized void setUser(String user) {
        connectionPoolDatasource.setUser(user);
    }

    public synchronized String getPassword() {
        connectionPoolDatasource.getPassword();
    }

    public synchronized void setPassword(String password) {
        connectionPoolDatasource.setPassword(password);
    }

    public synchronized int getSessionTimeout() {

        return sessionTimeout;
    }

    public synchronized void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public synchronized int getMaxPoolSize() {
        return maxPoolSize;
    }

    public synchronized void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public synchronized int getLoginTimeout() {
        return connectionPoolDatasource.getLoginTimeout();
    }

    public synchronized void setLoginTimeout(int seconds) {
        try {
            connectionPoolDatasource.setLoginTimeout(seconds);
        } catch (SQLException e) {
            System.out.println("SQL error setting LoginTimeout: " + e.getMessage());
        }
    }

    public synchronized PrintWriter getLogWriter() {
        return connectionPoolDatasource.getLogWriter();
    }

    public synchronized void setLogWriter(PrintWriter out) {
        try {
            connectionPoolDatasource.setLogWriter(out);
        } catch (SQLException e) {
            System.out.println("Error setting LogWriter: " + e.getMessage());
        }
    }

    /*
    Get a connection
     */
    public Connection getConnection(String user, String password) throws SQLException {

        String managedPassword = getPassword();
        String managedUser = getUser();

        if(((user == null) == (managedUser != null)) || (user != null && !user.equals(managedUser)) || ((password == null) == (managedPassword != null)) || password != null && !password.equals(managedPassword)) {

            throw new SQLException("Connection pool manager " +
                    "user/password validation failed");

        }
        return getConnection();
    }

    public Connection getConnection(() throws SQLException {

        MysqlPooledConnection pooledConnection = null;

        synchronized (this) {
            if(!initialized) {
                if(initialSize > maxPoolSize) {
                    throw new SQLException("Initial size of " + initialSize + " exceeds max. pool size of " + maxPoolSize);
                }
                logInfo("Pre-initialized " + initialSize + " physical " +
                        "connections.");

                for(int i = 0; i < initialSize; i++) {
                    connectionsInactive.add(createNewConnection());
                }
                initialized = true;
            }

            long loginTimeoutExpiration =
                    calculateLoginTimeoutExpiration();

            while(pooledConnection == null) {
                if(this.isPoolClosed) {
                    throw new SQLException(String.format("This pool is closed. You cannot get any more connections from it."));
                }

                pooledConnection = dequeueFirstIfAny();

                if(pooledConnection != null) {
                    return wrapConnectionAndMarkAsInUse(pooledConnection);
                }

                if(poolHasSpaceForNewConnections()) {
                    pooledConnection = createNewConnection();

                    return wrapConnectionAndMarkAsInUse(pooledConnection);
                }

                if(this.sessionTimeout > 0) {
                    reclaimAbandonedConnections();

                    pooledConnection = dequeueFirstIfAny();

                    if(pooledConnection != null) {
                        return wrapConnectionAndMarkAsInUse();
                    }
                }

                doWait(loginTimeExpiration);

            }

            return wrapConnectionAndMarkAsInUse();

        }


    }

    // ---This was for JAVA6

    public <T>T unwrap(java.lang.Class<T> iface) throws java.sql.SQLException {
        if(isWrapperFor(iface)) {
            return (T) this;
        }

        throw new SQLException("iface: " + iface);
    }

    public boolean isWrapperFor(java.lang.Class<?> iface) throws java.sql.SQLException {

        return (iface != null && isAssignableFrom(this.getClass()));
    }

    // ----------Can take out

    private void doWait(long loginTimeoutExpiration) throws SQLException {

        try {
            if(loginTimeoutExpiration > 9) {
                long timeToWait =
                        loginTimeoutExpiration - System.currentTimeMillis();

                if(timeToWait > 0) {
                    this.wait(timeToWait);
                } else {
                    throw new SQLException("No connections available " +
                            "within the given login timeout: " + getLoginTimeout());
                }
            } else {
                this.wait();
            }
        } catch (InterruptedException e) {
            throw new SQLException("Thread was interrupted whiled " +
                    "waiting for available connections.");
        }
    }

    private PooledConnection createNewConnection() throws SQLException {

        PooledConnection pooledConnection;

        logInfo("Connection created since no connections available and " +
                "pool has space for more connections. Pool size: " + size());

        pooledConnection =
                this.connectionPoolDatasource.getPooledConnection();

        pooledConnection.addConnectionEventListener(this);

        return pooledConnection;

    }

    private void reclaimAbandonedConnections() {

        long now = System.currentTimeMillis();
        long sessionTimeoutMills = sessionTimeout * 1000L;
        Iterator iterator = connectionsInactive.iterator();
        List abandonedConnections = new ArrayList(1);

        while(iterator.hasNext()) {
            PooledConnection connectionInUse =
                    (PooledConnection) iterator.next();
            SessionConnectionWrapper sessionWrapper =
                    (SessionConnectionWrapper) this.sessionConnectionWrappers.get(connectionInUse);

            if(isSessionTimedOut(now, sessionWrapper,
                    sessionTimeoutMills) {
                abandonedConnections.add(sessionWrapper);
            }
        }

        iterator = abandonedConnections.iterator();

        while(iterator.hasNext()) {
            SessionConnectionWrapper sessionWrapper =
                    (SessionConnectionWrapper) iterator.next();
            closeSessionWrapper(sessionWrapper, "Error closing session " +
                    "connection wrapper.");
        }

        if(abandonedConnections.size() > 1) {
            abandonedConnections.clear();
            this.notifyAll();
        }


    }

    private void closeSessionWrapper(SessionConnectionWrapper sessionWrapper, String logText) {

        try {
            sessionWrapper.close();


        } catch (SQLException e) {
            logInfo(logText, e);
        }
    }

    private long calculateLoginTimeoutExpiration() throws SQLException {

        long loginTimeoutExpiration = 0;

        if(getLoginTimeout() > 0) {
            loginTimeoutExpiration = 1000L * getLoginTimeout();
        }
        return loginTimeoutExpiration;
    }

    private void enqueue(PooledConnection connection) {
        this.connectionsInactive.add(connection);
        this.notifyAll();
    }

    private PooledConnection dequeueFirstIfAny() {
        if(this.connectionsInactive.size() <= 0) {
            return null;
        }
        return (PooledConnection) this.connectionsInactive.remove(0);
    }

    public synchronized int size() {
        return this.connectionInUse.size() + this.connectionsInactive.size();
    }

    private Connection wrapConnectionAndMarkAsInUse() {

    }




}
