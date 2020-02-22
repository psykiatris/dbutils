package org.palczewski.edit;

import java.sql.*;
import java.text.MessageFormat;

/**
 * Database management
 *
 * This will assume connection is open, and will check via the boolean
 * isOpen().
 *
 *
 */
public class DatabaseMachine {
    public static final String NO_CONNECTION = "No connection to server.";
    static final String TIMEOUT = "Connection timed out.";

    String dbName;
    private Connection conn;

    /**
     * DatabaseMachine instance
     * @param connection Connection to mySQL
     */
    public DatabaseMachine(Connection connection) {
        conn = connection;

    }

    /**
     * Notify user that connection must be made.
     */
    public DatabaseMachine() {
        System.out.println("No connection made");
        System.out.println("Usage: DatabaseMachin(<your connection>)");
    }

    /**
     * Creates database
     * @param name Database name
     */
    public final void createDB(String name) {
        dbName = name;

        try {
            if(conn.isValid(120)) {
                System.out.println(MessageFormat.format("Creating database {0}.", dbName));
                try (Statement stmt = conn.createStatement()){

                    String sql =
                            MessageFormat.format("CREATE DATABASE IF NOT EXISTS {0} CHARACTER SET = utf8", dbName);
                    stmt.executeUpdate(sql);
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

    /**
     * Delete database
     * @param name Database name
     */
    public final void removeDatabase(String name) {
        dbName = name;

        try {
            if(conn.isValid(120)) {
                try (Statement stmt = conn.createStatement()){

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

    /**
     * Switches to another database
     * @param name Database name
     */
    public final void switchDatabase(String name) {

        try {
            if(conn.isValid(120)) {
                if(conn.getCatalog().equals(name)) {
                    System.out.println("Currently in " + conn.getCatalog());
                } else {
                    // Otherwise switch to desired database
                    try (Statement stmt = conn.createStatement()){

                        String change = MessageFormat.format("USE {0}", name);
                        stmt.executeUpdate(change);
                        dbName = name;
                        System.out.println(MessageFormat.format("Switched to {0}", dbName));


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

    /**
     * Displays list of available databases
     */
    public void viewDatabases() {

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

    /**
     * Displays mySQL version
     */
    public void getNameVersion() {

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

    /**
     * Displays mySQL driver information
     */
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

    /**
     * Displays current user name
     */
    public final void getUserName() {
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
