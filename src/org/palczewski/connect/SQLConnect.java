package org.palczewski.connect;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;

import java.sql.*;
import java.text.MessageFormat;

/**
 * Provide the main connection to mySQL. Can be used by
 * other applications.
 */
public class SQLConnect {

    MysqlConnectionPoolDataSource ds = null;
    private static final String DRIVER = "com.mysql.jdbc.Driver";
    private static final String CONNECT = "Connected to server.";
    static Connection conn;
    private static Statement stmt;
    private String user;
    private String pw;
    private String dbName;



    public SQLConnect() {
        // Creates an instance

    }

    /**
     * Returns a Coonnection
     * @param user Username
     * @param pw User's password
     * @param dbName Database ma,e
     * @return Connection to mySQL
     */
    public final Connection doConnect(String user, String pw, String dbName) {
        this.user = user;
        this.pw = pw;
        this.dbName = dbName;
        /*
        Creates a pooled DataSource object and and returns a
        connection object to
        the calling app.


         */
        ds = MyDataSourceFactory.withUser(user, pw, dbName);

        try {
            conn = ds.getConnection();
            conn.setCatalog(dbName);

            System.out.println(CONNECT);

        } catch (SQLException e) {
            System.out.println(MessageFormat.format("SQL error connecting to mySQL server: {0}", e.getMessage()));
        }
        return conn;

    }

    // Overload connection for no database
    // For creation of users

    /**
     * For connections without database name
     * @return Connection to mySQL
     */
    public final Connection doConnect() {

        try {
            ds = MyDataSourceFactory.defaultUser();
            conn = ds.getConnection();



        } catch (SQLException e) {
            System.out.println(MessageFormat.format("Error connecting to mySQL server: {0}", e.getMessage()));
        }
        return conn;

    }

    /**
     * Closes connection
     */
    public final void doClose() {
        if(isOpen()) {
            // Shut down
            try {
                conn.close();
                System.out.println("SQL connection closed.");
            } catch (SQLException e) {
                System.out.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    /**
     * Returns status of connection
     * @return Status
     */
    private boolean isOpen() {
        return conn != null;
    }

    public final String toString() {
        return "Returns a connection objhect using a DataSource";
    }



}