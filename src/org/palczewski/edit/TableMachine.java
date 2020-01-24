package org.palczewski.edit;

import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.text.MessageFormat;
import java.util.Scanner;

/**
 * Table management tools
 *
 *
 */
public class TableMachine {

    public static final String NOTHING_TO_SHOW = "Nothing to show";
    /*
        Pass cononection to use methods
         */
    Connection conn;
    ResultSetMetaData rsmd;


    /**
     * Creates TableMachine instance
     * @param conn Connection
     */
    public TableMachine(Connection conn) {
        this.conn = conn;
    }

    /**
     * When no connection is passed
     */
    public TableMachine() {
        System.out.println("No connection established");
        System.out.println("Usage: TableMachine(<your connection>");
    }

    /**
     * Creates empty table. Can be overridden. Use mySQL syntax to
     * create table with its name and params
     */
    public void createTable() {


    }

    /**
     * Creates a default table
     * Table fields: date, last name, first name
     * @param tName Table name
     */
    public void createTable(String tName) {

        if(conn != null) {
            try (Statement stmt = conn.createStatement()) {
                String table =
                        MessageFormat.format("CREATE TABLE IF NOT EXISTS {0} (date DATE KEY, first_name CHAR(15), last_name CHAR(15), timestamp TIMESTAMP)", tName);
                stmt.executeUpdate(table);
                // Verify that table is created
                String show = "SHOW TABLES";
                try(Statement stmt1 = conn.createStatement(); ResultSet rs = stmt1.executeQuery(show)) {
                    while(rs.next()) {
                        if(rs.getString(1) == null) {
                            System.out.println("No tables to list.");
                        } else {
                            if(rs.getString(1).equals(tName)) {
                                System.out.println(MessageFormat.format("{0} created successfully.", tName));
                            }
                        }
                    }
                }


            } catch (SQLException e) {
                System.out.println(MessageFormat.format("SQL error in createTable(): {0}", e.getMessage()));
            }
        } else {
            System.out.println(DatabaseMachine.NO_CONNECTION);
        }
    }

    /**
     * Displays columns of table
     * @param tName Table name
     */
    public void getColumns(String tName) {
        /*
        Return a list of column names for a table.
         */
        // Check connection
        if(conn != null) {
            try (Statement stmt = conn.createStatement()){
                String qry = "SELECT * FROM " + tName;
                try(ResultSet rs = stmt.executeQuery(qry)) {
                    rsmd = rs.getMetaData();
                    int i = 1;
                    while(i <= rsmd.getColumnCount()) {
                        String cName = rsmd.getColumnName(i);
                        String cType = rsmd.getColumnTypeName(i);
                        System.out.println(MessageFormat.format("Column: {0}\tType: {1}", cName, cType));
                        i++;
                    }
                    System.out.println("Number of columns: " + rsmd.getColumnCount());
                }


            } catch (SQLException e) {
                System.out.println("SQL error in getColumns(): " + e.getMessage());
            }
        } else {
            System.out.println(DatabaseMachine.NO_CONNECTION);
        }

    }

    /**
     * Display list of available tables for current user
     */
    public void viewTables() {

        if(conn != null) {
            try (Statement stmt = conn.createStatement()){


                String show = "SHOW TABLES";
                System.out.println("Table List:");
                try(ResultSet rs = stmt.executeQuery(show)) {
                    while(rs.next()) {
                        if(rs.getRow() == 0) {
                            System.out.println(NOTHING_TO_SHOW);
                        } else {
                            System.out.println(rs.getRow() + " " + rs.getString(1));
                        }
                    }

                }

            } catch (SQLException e) {
                System.out.println(MessageFormat.format("SQL error in viewTables(): {0}", e.getMessage()));
            }
        }
    }
    // TODO: 1/23/20 I can leave the Scanner open and pass to this instance to solve this problem
    /*
    In the method below, it should open a new Scanner and read input.
    However, when run, it throws an exception saying "line not found."
    as if the System.in is closed, though I opened it explicitly.
     */

    /**
     * Allows user to type input to table
     */
    public void insertRecord() {
        /*
        Must override with app's specific definitions, etc.
        This will input into the table created in createTable().
         */
        if(conn != null) {
            try(Scanner in = new Scanner(System.in, StandardCharsets.UTF_8)) {
                String data = "INSERT INTO history (date, first_name, last_name) VALUES (?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(data)){

                    // Get input
                    int count = 1;
                    while(count <= 5) {
                        System.out.println("====Enter details====");
                        System.out.print("Enter date: ");
                        String d = in.nextLine();
                        System.out.print("Enter first name: ");
                        String fname = in.nextLine();
                        System.out.print("Enter last name: ");
                        String lname = in.nextLine();
                        // process input
                        pstmt.setString(1, d.trim());
                        pstmt.setString(2, fname.trim());
                        pstmt.setString(3, lname.trim());
                        int i = pstmt.executeUpdate();
                        System.out.println(i + " input success");
                        count++;
                    }


                } catch (SQLException e) {
                    System.out.println("SQL error in insertRecord(): " + e.getMessage());
                }

            }

        } else {
            System.out.println(DatabaseMachine.NO_CONNECTION);
        }
    }

    /**
     * Displays records in a talbe
     * @param tName Table name
     */
    public void viewRecords(String tName) {

        try {
            if(conn.isValid(120)) {
                try (Statement stmt = conn.createStatement()){

                    String qry = MessageFormat.format("SELECT User, super_priv FROM " +
                            "{0}", tName);
                    try(ResultSet rs = stmt.executeQuery(qry)) {
                        rsmd = rs.getMetaData();
                        int i = 1;
                        while (i <= rsmd.getColumnCount()) {
                            System.out.print(rsmd.getColumnName(i) + "\t");
                            i++;

                        }
                        System.out.print("\n");
                    }
                    try(ResultSet rs = stmt.executeQuery(qry)) {
                        while(rs.next()) {
                            System.out.print(rs.getString(1) + "\t" + rs.getString(2) + "\n");
                        }
                    }


                } catch (SQLException e) {
                    System.out.println(MessageFormat.format("SQL error in viewRecords(): {0}", e.getMessage()));
                }
            } else {
                System.out.println(DatabaseMachine.NO_CONNECTION);
            }
        } catch (SQLException e) {
            System.out.println(DatabaseMachine.TIMEOUT);
        }
    }

    /**
     * Updates existing records
     * @param tName Table name
     */
    public void updateRecord(String tName) {
        /*
        This will update one row in the default template table. other
        apps should override this method for their own tables, etc.
         */
        if(conn != null) {
            try (Statement stmt = conn.createStatement()){

                String update = MessageFormat.format("UPDATE {0} date = TIMESTAMP", tName);

                stmt.executeUpdate(update);
                System.out.println("Updated record");

            } catch (SQLException e) {
                System.out.println(MessageFormat.format("SQL error in updateRecord(): {0}", e.getMessage()));
            }
        } else {
            System.out.println(DatabaseMachine.NO_CONNECTION);
        }
    }


    /**
     * // TODO: 1/23/20 Rename function as it displays a list of users
     * Displays a list of users
     */
    public void statement() {
        try (Statement stmt = conn.createStatement()){
            if(conn.isValid(120)) {

                String sql = "SELECT User FROM mysql.user";
                try(ResultSet rs = stmt.executeQuery(sql)) {
                    rsmd = rs.getMetaData();
                    System.out.println("Table: " + rsmd.getTableName(1));
                    while(rs.next()) {
                        System.out.println(rs.getString(1));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error with result set " + e.getMessage());
        }
    }


    @Override
    public String toString() {
        return MessageFormat.format("This instance maintains the methods for use with tables\n Connection: {0}", conn);
    }
}
