package org.palczewski.connect;

import javax.sql.DataSource;
import java.sql.*;
import java.text.MessageFormat;

/*
This class will provide the main connection to mySQL. Can be used by
other applications.
 */
public class MainConnect {

    DataSource ds;
    private static final String DRIVER = "com.mysql.jdbc.Driver";
    private static final String CONNECT = "Connected to server.";
    static Connection conn;
    private static Statement stmt;
    private String user;
    private String pw;
    private String dbName;



    public MainConnect() {
        // Creates an instance

    }

    public Connection doConnect(String user, String pw, String dbName) {
        String url = "jdbc:mysql://localhost:3306/" + dbName +
                "?verifyServerCertificate=false&useSSL=true";
        /*
        Creates and returns a connection object to the calling app.
         */
        try {
            Class.forName(DRIVER);
            conn = DriverManager.getConnection(url, user, pw);

            System.out.println(CONNECT);

        } catch (ClassNotFoundException e) {
            System.out.println(MessageFormat.format("Class was not Found: {0}", e.getMessage()));
        } catch (SQLException e) {
            System.out.println(MessageFormat.format("SQL error connecting to mySQL server: {0}", e.getMessage()));
        }
        return conn;

    }

    // Overload connection for no database
    // For creation of users
    public Connection doConnect(String user, String pw) {
        String url = "jdbc:mysql://localhost:3306/?verifyServerCertificate=false&useSSL=true";
        /*
        Creates and returns a connection object to the calling app.
         */
        try {
            Class.forName(DRIVER);
            /*
            Per inspection, DriverManager has been superseded by javax
            .sql.Datasoure. (Need to update)
             */
            conn = DriverManager.getConnection(url, user, pw);
            System.out.println(CONNECT);

        } catch (ClassNotFoundException e) {
            System.out.println(MessageFormat.format("Class Not Found: {0}", e.getMessage()));
        } catch (SQLException e) {
            System.out.println(MessageFormat.format("Error connecting to mySQL server: {0}", e.getMessage()));
        }
        return conn;

    }

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

    final boolean isOpen() {
        return conn != null;
    }

    public final String toString() {
        return "Main connection to mySQL server.";
    }


}