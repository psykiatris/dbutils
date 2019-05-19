package org.palczewski.proposed;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlPooledConnection;

import javax.sql.ConnectionEventListener;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;


/*
Proposed class to managed pooled connections
 */
public class PoolManager implements MysqlConnectionPoolDataSource,
        ConnectionEventListener {



    private static final int DEFAULT_MAX_POOL_SIZE = 8;
    private final boolean isPoolClosed = false;
    private int sessionTimeout;
    private MysqlConnectionPoolDataSource connectionPoolDatasource;
    private final Set connectionInUse = new HashSet(1);
    private final List connectionInactive = new ArrayList<>(1);
    private final Map sessionConnectionWrappers = new HashMap<>(1);
    private int maxPoolSize = DEFAULT_MAX_POOL_SIZE;
    private final boolean initialized;
    private int initialize;

    // some booleans
    boolean doResetAutoCommit;
    boolean doResetReadOnly;
    boolean doResetTrasactionIsolation;
    boolean doResetCatalog;

    boolean isAutoCommit = true;
    boolean isReadOnlye;
    int transactionIsolation = Connection.TRANSACTION_READ_COMMITTED;
    String catalog;

    // optional query to validate connections
    private final String validationQuery;


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

        MysqlPooledConnection poolConnection = null;


    }


}
