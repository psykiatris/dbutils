package org.palczewski.edit;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
    Statement stmt;

    public TableMachine(Connection conn) {
        this.conn = conn;
    }
    public TableMachine() {
        System.out.println("No connection established");
        System.out.println("Usage: TableMachine(<your connection>");
    }

    public void createTable(String tName) {
        /*
        Defines default table wit columns (date, lname, fname).
        Your application should override this method with its own table
        definition.

         */
        if(conn != null) {
            try {
                stmt = conn.createStatement();
                String table =
                        MessageFormat.format("CREATE TABLE IF NOT EXISTS {0} (id INT KEY AUTO_INCREMENT, date DATE, first_name CHAR(15), last_name CHAR(15), timestamp TIMESTAMP) AUTO_INCREMENT = 1", tName);
                stmt.executeUpdate(table);
                stmt.close();
                // Verify that table is created
                stmt = conn.createStatement();
                String show = "SHOW TABLES";
                try(ResultSet rs = stmt.executeQuery(show)) {
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
                stmt.close();

            } catch (SQLException e) {
                System.out.println(MessageFormat.format("SQL error in createTable(): {0}", e.getMessage()));
            }
        } else {
            System.out.println(DatabaseMachine.NO_CONNECTION);
        }
    }

    public void getColumns() {
        /*
        Return a list of column names for a table.
         */
    }

    public void viewTables() {
        if(conn != null) {
            try {

                stmt = conn.createStatement();
                String show = "SHOW TABLES";
                System.out.println("Table List:");
                try(ResultSet rs = stmt.executeQuery(show)) {
                    while(rs.next()) {
                        if(rs.getString(1) == null) {
                            System.out.println(NOTHING_TO_SHOW);
                        } else {
                            System.out.println(rs.getString(1));
                        }
                    }

                }
                stmt.close();
            } catch (SQLException e) {
                System.out.println(MessageFormat.format("SQL error in viewTables(): {0}", e.getMessage()));
            }
        }
    }

    public void insertRecord() {
        /*
        Other apps MUST override this method. This will insert a default
         record into the table defined in the template createTable().
         */
        if(conn != null) {
            try {
                stmt = conn.createStatement();
                String data = MessageFormat.format(" INSERT INTO history (date, first_name, last_name) VALUES (\"{0}\", \"Damon\", \"Harris\")", LocalDate.now());
                stmt.executeUpdate(data);
                stmt.close();

            } catch (SQLException e) {
                System.out.println(MessageFormat.format("SQL error in insertRecord(): {0}", e.getMessage()));
            }
        } else {
            System.out.println(DatabaseMachine.NO_CONNECTION);
        }
    }

    public void viewRecords(String tName) {
        /*
        Will display records from table
         */
        if(conn != null) {
            try {
                stmt = conn.createStatement();
                String qry = MessageFormat.format("SELECT * FROM {0}", tName);
                try(ResultSet rs = stmt.executeQuery(qry)) {
                    System.out.println("Records Display:");
                    while(rs.next()) {
                        if(rs.getString(1) == null) {
                            System.out.println(NOTHING_TO_SHOW);
                        } else {
                            System.out.println(MessageFormat.format("{0" +
                                            "} {1}\t{2} {3} ",
                                    rs.getInt(1),
                                    rs.getDate(2), rs.getString(3),
                                    rs.getString(4), rs.getTimestamp(5)));
                        }
                    }
                }
            } catch (SQLException e) {
                System.out.println(MessageFormat.format("SQL error in viewRecords(): {0}", e.getMessage()));
            }
        } else {
            System.out.println(DatabaseMachine.NO_CONNECTION);
        }
    }

    public void updateRecord(String tName) {
        /*
        This will update one row in the default template table. other
        apps should override this method for their own tables, etc.
         */
        if(conn != null) {
            try {
                stmt = conn.createStatement();
                String update = MessageFormat.format("UPDATE {0} date = TIMESTAMP", tName);

                stmt.executeUpdate(update);
                stmt.close();
                System.out.println("Updated record");

            } catch (SQLException e) {
                System.out.println(MessageFormat.format("SQL error in updateRecord(): {0}", e.getMessage()));
            }
        } else {
            System.out.println(DatabaseMachine.NO_CONNECTION);
        }
    }


    @Override
    public String toString() {
        return MessageFormat.format("This instance maintains the methods for use with tables\n Connection: {0}", conn);
    }
}
