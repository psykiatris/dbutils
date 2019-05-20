package org.palczewski.proposed;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import com.mysql.cj.jdbc.MysqlPooledConnection;


import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.PooledConnection;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.*;
import java.util.logging.Logger;

/*
Proposed class to managed pooled connections.

Based on a class developed by Jakob Jenkov.


 */
public class PoolManager extends MysqlConnectionPoolDataSource implements
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

    private Connection wrapConnectionAndMarkAsInUse(MysqlPooledConnection pooledConnection) throws SQLException {

        pooledConnection = assureValidConnection(pooledConnection);

        Connection conn = pooledConnection.getConnection();

        if(doResetAutoCommit) {
            conn.setAutoCommit(isAutoCommit);

        }

        if(doResetReadOnly) {
            conn.setReadOnly(isReadOnlye);
        }

        if(doResetTrasactionIsolation) {
            conn.setTransactionIsolation(transactionIsolation);
        }

        if(doResetCatalog) {
            conn.setCatalog(catalog);
        }

        if(validationQuery != null) {

            java.sql.ResultSet rs = null;

            try {

                rs = conn.createStatement().executeQuery(validationQuery);

                if(!rs.next()) {
                    throw new SQLException(" 0 rows returned");
                }
            } catch (SQLException e) {
                closePhysically(pooledConnection, "Closing " +
                        "non0validateing pooled connection");
                throw new SQLException("Validation query failed. " + e.getMessage());
            } finally {
                if(rs != null) {
                    rs.close();
                }
            }
        }
        this.connectionInUse.add(pooledConnection);

        SessionConnectionWrapper sessionWrapper =
                new SessionConnectionWrapper(pooledConnection.getConnection());

        this.sessionConnectionWrappers.put(pooledConnection,
                sessionWrapper);

        return sessionWrapper;


    }

    private MysqlPooledConnection assureValidConnection(MysqlPooledConnection pooledConnection) throws SQLException {

        if(isInvalid(pooledConnection)) {
            closePhysically(pooledConnection, "Closing invalid pooled " +
                    "connection");

            return this.connectionPoolDatasource.getConnection();
        }
        return pooledConnection;
    }

    private boolean isInvalid(MysqlPooledConnection pooledConnection) {
        try {
            pooledConnection.getConnection().isValid() || pooledConnection.getConnection().isClosed();
        } catch (SQLException e) {
            logInfo("Error calling pooledConnection. Connection will be " +
                    "removed from pool.", e);
        }
        return false;
    }

    private boolean isSessionTimedOut(long now,
                                      ConnectionSessionWrapper sessionWrapper, long sessionTimeoutMills) {
        return now - sessionWrapper.getLatestActivityTime() >= sessionTimeoutMills;
    }

    private boolean poolHasSpaceForNewConnections() {
        return this.maxPoolSize > size();1
    }

    public synchronized void connectionClosed(ConnectionEvent event) {

        MysqlPooledConnection connection =
                (MysqlPooledConnection) event.getSource();

        this.connectionInUse.remove(connection);
        this.sessionConnectionWrappers.remove(connection);

        if(!isPoolClosed) {
            enqueue(connection);
            logInfo("Connection returned to pool.");

        } else {
            closePhysically(connection, "closing returned connection");
            logInfo("Connection returned to pool was closed because pool" +
                    " is closed.");
            this.notifyAll();
        }
    }

    public synchronized void connectionErrorOccurred(ConnectionEvent event) {

        MysqlPooledConnection connection =
                (MysqlPooledConnection) event.getSource();
        connection.removeStatementEventListener(this);
        this.connectionInUse.remove(connection);
        this.sessionConnectionWrappers.remove(connection);
        logInfo("Fatal exception occurred on pooled connection. " +
                "Connection removed from pool.");
        logInfo(event.getSQLException());
        closePhysically(connection, "closing invalid, removed connection" +
                ".");
        this.notifyAll();
    }

    public synchronized void close() {

        this.isPoolClosed = true;

        while(this.connectionsInactive.size() > 0) {
            MysqlPooledConnection connection = dequeueFirstIfAny();

            if(connection != null) {
                closePhysically(connection, "closing inactive connection" +
                        " when connection pool was closed.");
            }
        }
    }

    public synchronized void closeAndWait() throws InterruptedException {

        close();

        while(size() > 0) {
            this.wait();
        }
    }

    public synchronized void closeimmediately() {

        close();

        Iterator iterator = connectionInUse.iterator();

        while(iterator.hasNext()) {
            MysqlPooledConnection connection =
                    (MysqlPooledConnection) iterator.next();
            SessionConnectionWrapper sessionWrapper =
                    (SessionConnectionWrapper) this.sessionConnectionWrappers.get(connection);
            closeSessionWrapper(sessionWrapper, "Error closing session " +
                    "wrapper. Connection pool was shutdown immediately");
        }
    }

    private void closePhysically(MysqlPooledConnection source,
                                 String logText) {
        try {
            source.close();

        } catch (SQLException e) {
            logInfo("Error " + logText, e);
        }
    }

    private void logInfo(String message) {

        connectionPoolDatasource.logInfo(message);
    }

    private void logInfo(Throwable t) {

        connectionPoolDatasource.logInfo(t);
    }

    private void logInfo(String message, Throwable t) {

        connectionPoolDatasource.logInfo(message, t);
    }

    public void setDefaultAutoCommnit(boolean defaultAutoCommit) {
        isAutoCommit = defaultAutoCommit;
        doResetAutoCommit = true;
    }

    public void setDefaultReadOnly(boolean defaultReadOnly) {

        isReadOnlye = defaultReadOnly;
        doResetReadOnly = true;
    }

    public void setDefaultTransactionIsolation(int defaultTransactionIsolation) {

        transactionIsolation = defaultTransactionIsolation;
        doResetTransactionIsolation = true;
    }

    public void setDefaultCatalog(String defaultCatalog) {

        catalog = defaultCatalog;
        doResetCatalog = true;
    }

    public boolean getDefaultAutoCommit() {
        doResetAutoCommit = true;

        return isAutoCommit;
    }

    public String getDefaultCatalog() {
        doResetCatalog = true;

        return catalog;
    }

    public int getDefaultTransactionIsolatio() {
        doResetTransactionIsolation = true;
        return transactionIsolation;
    }

    public boolean getDefaultReadOnly() {
        doResetReadOnly = true;
        return isReadOnlye;
    }

    public void setDriverClassName(String driverClassName) {
        if(driverClassName.equals(MysqlDataSource.mysqlDriver) {
            return;
        }

        throw new RuntimeException("This class only supports " + MysqlDataSource.mysqlDriver);
    }

    public String getDriverClassName() {
        return MysqlDataSource.mysqlDriver.toString();
    }

    public void setInitialSize(int initialSize) {
        this.initialSize = initialSize;
    }

    public int getinitialPoolSize() {
        return getInitialSize();
    }

    public void setInitialPoolSize(int initialSize) {

        setInitialSize(initialSize);

    }

    public int getInitialSize() {
        return initialSize;
    }

    public int getnumActive() {
        return connectionInUse.size();
    }

    public void setUsername(String username) {
        setUser(username);
    }

    public String getusername() {
        return getUser();
    }

    public void setMaxActive(int maxActive) {
        setMaxPoolSize(maxActive);
    }

    public int getMaxActive() {
        return getMaxPoolSize();
    }

    public void setValidationQuery(String validationQuery) {

        this.validationQuery = validationQuery;

    }

    public String getValidationQuery() {
        return validationQuery;
    }

    public void addConnectionProperty(String name, String value) {
        this.connectionPoolDatasource.setConnectionProperty(name, value);


    }

    public void removeConnectionProperty(String name) {

        this.connectionPoolDatasource.removeConnectionProperty(name);
    }

    public String toString() throws RuntimeException {

        int timeout = 0;

        try {
            timeout = getLoginTimeout();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve the Login " +
                    "Timeout value.")
        }

        StringBuffer sb =
                new StringBuffer(PoolManager.class.getName() + " " +
                        "instance\n   User: " + getUsername()
                + "\n   Url: " + getUrl()
                + "\n   Login Timeout: " + timeout
                + "\n   Num. ACTIVE: " + getnumActive()
                + "\n   Num. IDLE: " + getNumIdle());



    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }



}
