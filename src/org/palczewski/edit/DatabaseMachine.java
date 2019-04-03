package org.palczewski.edit;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;

/*
This class will contain all of the methods to work with databases, such
as creation, deletion, viewing etc.

This will assume connection is open, and will check via the boolean
isOpen().

Will be used by application at initialization.
 */
public class DatabaseMachine {
    public static final String NO_CONNECTION = "No connection to server.";
    String dbName;
    private Connection conn;
    private Statement stmt;

    public DatabaseMachine(Connection connection) {
        conn = connection;

    }

    public DatabaseMachine() {
        System.out.println("No connection made");
        System.out.println("Use: new DatabaseMachin(<your connection>)");
    }

    public final void createDB(String name) {
        dbName = name;

        if(conn != null) {
            System.out.println(MessageFormat.format("Creating database {0}.", dbName));
            try {
                stmt = conn.createStatement();
                String sql = "create database " + dbName;
                stmt.executeUpdate(sql);
            } catch (SQLException e) {
                System.out.println("Error creating database " + e.getMessage());
            }
            System.out.println("Database " + dbName + " ready for input.");
        } else {
            /*
        If no connection, do nothing.
         */
            System.out.println(NO_CONNECTION);
        }

    }

    public final void removeDatabase(String name) {
        dbName = name;

        if(conn != null) {
            try {
                stmt = conn.createStatement();
                String drop = "drop database " + dbName;
                stmt.executeUpdate(drop);
                System.out.println("Removed " + dbName + " from mySQL " +
                        "server.");

            } catch (SQLException e) {
                System.out.println("Error dropping database " + dbName + ": " + e.getMessage());
            }

        } else {
            System.out.println(NO_CONNECTION);
        }
    }

    public final void switchDatabase(String name) {
        dbName = name;

        if(conn != null) {
            try {
                stmt = conn.createStatement();
                String sql = "SELECT database()";
                try(ResultSet rs = stmt.executeQuery(sql)) {
                    if(rs != null) {
                        while(rs.next()) {
                            if(rs.getString(1) == dbName) {
                                System.out.println("Currently in " + dbName);
                            } else {
                                stmt = conn.createStatement();
                                String change = "use " + dbName;
                                stmt.executeUpdate(change);
                                System.out.println("Changed to " + dbName);
                            }
                        }
                    }
                }

            } catch (SQLException e) {
                System.out.println("Error processing SQL statement: " + e.getMessage());
            }


        } else {
            System.out.println(NO_CONNECTION);
        }
    }

    public void viewDatabase() {
        if(conn != null) {
            try {
                stmt = conn.createStatement();
                String list = "show databases";
                try (ResultSet rs = stmt.executeQuery(list)) {
                    System.out.println("List of databases on mySQL:");
                    int i = 1;
                    while (rs.next()) {
                        System.out.println(i + ": " + rs.getString(1));
                        i++;
                    }
                }


            } catch (SQLException e) {
                System.out.println("Error processing statement" + e.getMessage());
            }

        } else {
            System.out.println(NO_CONNECTION);
        }
    }

    @Override
    public String toString() {
        return "Maintains methods to use with databases. Connection: " + conn;
    }
}
