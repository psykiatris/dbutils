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
    private static final String NO_CONNECTION = "No connection to server.";
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
    private boolean inDatabase() {
        // Make sure currently in database
        if(conn != null) {
            //query
            try {
                stmt = conn.createStatement();
                String qry = "SELECT database()";
                try(ResultSet rs = stmt.executeQuery(qry)) {
                    while(rs.next()) {
                        if(rs.getString(1) == null) {
                            System.out.println("No database selected");

                        } else {
                            // Set dbName
                            dbName = rs.getString(1);
                            return true;
                        }
                    }


                } catch (SQLException e) {
                    System.out.println("SQL error in inDatabase(): " + e.getMessage());
                }
            } catch (SQLException e) {
                System.out.println("SQL error in creating statement: " + e.getMessage());
            } finally {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    System.out.println("Problem closing statement");
                }
            }
        }
        return false;
    }

    public final void switchDatabase(String name) {


        if(conn != null) {
            if(inDatabase() && dbName.equals(name)) {
                System.out.println("Currently in " + dbName);
            } else {
                // Otherwise switch to desired database
                try {
                    stmt = conn.createStatement();
                    dbName = name;
                    String change = "USE " + dbName;
                    stmt.executeUpdate(change);
                    System.out.println("Switched to " + dbName);

                } catch (SQLException e) {
                    System.out.println("SQL rror in switchDatabase(): " + e.getMessage());
                }
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
