package org.palczewski.connect;

import java.sql.*;

/*
This class will provide the main connection to mySQL. Can be used by
other applications.
 */
public class MainConnect {

    private static Connection conn;
    private static Statement stmt;
    private String user;
    private String pw;

    private static final String url =
            "jdbc:mysql://localhost:3306/4" +
                    "?verifyServerCertificate" +
                    "=false" +
                    "&useSSL=true";

    MainConnect() {
        // Creates an instance
    }

    public Connection doConnect(String user, String pw) {
        /*
        Creates and returns a connection object to the calling app.
         */
        try {
            Class.forName("com.mysql.jdbc.Driver");
            /*
            Per inspection, DriverManager has been superseded by javax
            .sql.Datasoure. (Need to update)
             */
            conn = DriverManager.getConnection(url, user, pw);
            System.out.println("Connected to server.");

        } catch (ClassNotFoundException e) {
            System.out.println("Class Not Found.");
        } catch (SQLException e) {
            System.out.println("Error connecting to database.");
        }
        return conn;
    }

    public final String toString() {
        return "This class manaages connecting to database server.";
    }
}