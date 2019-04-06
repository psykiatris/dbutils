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
        System.out.println("Usage: DatabaseMachin(<your connection>)");
    }

    public final void createDB(String name) {
        dbName = name;

        if(conn != null) {
            System.out.println(MessageFormat.format("Creating database {0}.", dbName));
            try {
                stmt = conn.createStatement();
                String sql =
                        MessageFormat.format("CREATE DATABASE IF NOT EXISTS {0} CHARACTER SET = utf8", dbName);
                stmt.executeUpdate(sql);
                stmt.close();
                System.out.println(MessageFormat.format("Database {0} ready for input.", dbName));
            } catch (SQLException e) {
                System.out.println("Error creating database " + e.getMessage());
            }

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
                String drop = MessageFormat.format("DROP DATABASE IF EXISTS {0}", dbName);
                stmt.executeUpdate(drop);
                System.out.println(MessageFormat.format("Removed {0} from mySQL.", dbName));

            } catch (SQLException e) {
                System.out.println(MessageFormat.format("Error dropping database {0}: {1}", dbName, e.getMessage()));
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
                    System.out.println(MessageFormat.format("SQL error in inDatabase(): {0}", e.getMessage()));
                }
                stmt.close();
            } catch (SQLException e) {
                System.out.println(MessageFormat.format("SQL error in creating statement: {0}", e.getMessage()));
            }

        } else {
            System.out.println(NO_CONNECTION);

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
                    String change = MessageFormat.format("USE {0}", name);
                    stmt.executeUpdate(change);
                    dbName = name;
                    System.out.println(MessageFormat.format("Switched to {0}", dbName));
                    stmt.close();

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
                String list = "SHOW DATABASES";
                try (ResultSet rs = stmt.executeQuery(list)) {
                    System.out.println("List of databases on mySQL:");
                    int i = 1;
                    while (rs.next()) {
                        System.out.println(MessageFormat.format("{0}: {1}", i, rs.getString(1)));
                        i++;
                    }
                }
                stmt.close();

            } catch (SQLException e) {
                System.out.println(MessageFormat.format("Error processing statement{0}", e.getMessage()));
            }

        } else {
            System.out.println(NO_CONNECTION);
        }
    }

    @Override
    public final String toString() {
        return MessageFormat.format("Maintains methods to use with databases. Connection: {0}", conn);
    }
}
