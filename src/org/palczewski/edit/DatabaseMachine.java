package org.palczewski.edit;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/*
This class will contain all of the methods to work with databases, such
as creation, deletion, viewing etc.

This will assume connection is open, and will check via the boolean
isOpen().

Will be used by application at initialization.
 */
public class DatabaseMachine {
    String dbName; // Name of database
    Connection conn;
    Statement stmt;

    public DatabaseMachine(Connection conn) {
        this.conn = conn;

    }

    public DatabaseMachine() {
        System.out.println("Instance created, but no connection made.");
        System.out.println("Call constructor with connection param");
        System.out.println("Excmple: DatabaseMachine dm = new " +
                "DatabaseMachine(connection)");
    }

    public void createDB(String dbName) {
        this.dbName = dbName;

        if(conn != null) {
            System.out.println("Creating database " + dbName + ".");
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
            System.out.println("no connection to server");
        }

    }

    public void removeDatabase(String dbName) {
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
            System.out.println("No connection to server.");
        }
    }

    public void switchDatabase(String dbName) {
        if(conn != null) {

        } else {
            System.out.println("No connection to server.");
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
            System.out.println("Not connected to mySQL");
        }
    }

    @Override
    public String toString() {
        return "Maintains methods to use with databases. Connection: " + conn;
    }
}
