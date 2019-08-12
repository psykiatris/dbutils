package org.palczewski.edit;

import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.text.MessageFormat;
import java.util.Scanner;

/*
This class contains methods to be used with tables (creation, deletion,
viewing, inserting and deleting records).

Methods will be built with predefined use (as general as possible), but
can be overridden by programs utilizing this library.
 */
public class TableMachine {

    public static final String NOTHING_TO_SHOW = "Nothing to show";
    /*
        Other apps can pass their connection objects to use methods in this
        class. Same idea as DatabaseMachine.
         */
    Connection conn;
    ResultSetMetaData rsmd;


    public TableMachine(Connection conn) {
        this.conn = conn;
    }

    public TableMachine() {
        System.out.println("No connection established");
        System.out.println("Usage: TableMachine(<your connection>");
    }

    public void createTable() {
        // Allow classses extending this class to customize their own
        // table.

    }

    public void createTable(String tName) {
        /*
        Defines default table wit columns (date, lname, fname).
        Your application should override this method with its own table
        definition.

         */
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

    public void viewTables() {
        /*
        Displays a list of tables in a database
         */
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
    /*
    In the method below, it should open a new Scanner and read input.
    However, when run, it throws an exception saying "line not found."
    as if the System.in is closed, though I opened it explicitly.
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

    public void viewRecords(String tName) {
        /*
        Will display records from table
         */
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
