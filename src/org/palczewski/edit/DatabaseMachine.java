package org.palczewski.edit;

import java.sql.*;
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
    public static final String TIMEOUT = "Connection timed out.";

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

        try {
            if(conn.isValid(120)) {
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
        } catch (SQLException e) {
            System.out.println(TIMEOUT);
        }

    }

    public final void removeDatabase(String name) {
        dbName = name;

        try {
            if(conn.isValid(120)) {
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
        } catch (SQLException e) {
            System.out.println(TIMEOUT);
        }
    }


    public final void switchDatabase(String name) {

        try {
            if(conn.isValid(120)) {
                if(conn.getCatalog().equals(name)) {
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
        } catch (SQLException e) {
            System.out.println(TIMEOUT);
        }
    }

    public void viewDatabases() {
        /*
        Returns a list of databases available.
         */
        try {
            if(conn.isValid(120)) {
                try {
                    DatabaseMetaData dmd = conn.getMetaData();
                    try(ResultSet rs = dmd.getCatalogs()) {
                        while(rs.next()) {
                            System.out.println(rs.getString(1));
                        }
                    }


                } catch (SQLException e) {
                    System.out.println("SQL error in viewDatabases(): " + e.getMessage());
                }

            } else {
                System.out.println(NO_CONNECTION);
            }
        } catch (SQLException e) {
            System.out.println(TIMEOUT);
        }
    }

    public void getNameVersion() {
        /*
        This will return the vesion of mySQL being used.
         */
        try {
            if(conn.isValid(120)) {
                try {
                    DatabaseMetaData dmd = conn.getMetaData();
                    String product = dmd.getDatabaseProductName();
                    String version = dmd.getDatabaseProductVersion();
                    System.out.println(MessageFormat.format("Using {0} {1}", product, version));

                } catch (SQLException e) {
                    System.out.println("SQL error in getNameVersion(): " + e.getMessage());
                }
            } else {
                System.out.println(NO_CONNECTION);
            }
        } catch (SQLException e) {
            System.out.println(TIMEOUT);
        }
    }

    public void getDriverInfo() {
        try {
            if(conn.isValid(120)) {
                try {
                    DatabaseMetaData dmd = conn.getMetaData();
                    System.out.println(MessageFormat.format("Using driver: {0} Version: {1}.{2}", dmd.getDriverName(), dmd.getDriverMajorVersion(), dmd.getDriverMinorVersion()));
                    System.out.println(MessageFormat.format("JDBC Version: {0}.{1}", dmd.getJDBCMajorVersion(), dmd.getJDBCMinorVersion()));

                } catch (SQLException e) {
                    System.out.println("SQL error in getDriverInfo(): " + e.getMessage());
                }

            } else {
                System.out.println(NO_CONNECTION);
            }
        } catch (SQLException e) {
            System.out.println(TIMEOUT);
        }
    }

    public void getUserName() {
        try {
            if(conn.isValid(120)) {
                try {
                    DatabaseMetaData dmd = conn.getMetaData();
                    System.out.println("Logged in as: " + dmd.getUserName());



                } catch (SQLException e) {
                    System.out.println("SQL error in getUserName(): " + e.getMessage());
                }

            } else {
                System.out.println(NO_CONNECTION);
            }
        } catch (SQLException e) {
            System.out.println(TIMEOUT);
        }
    }

    @Override
    public final String toString() {
        return MessageFormat.format("Maintains methods to use with databases. Connection: {0}", conn);
    }
}
