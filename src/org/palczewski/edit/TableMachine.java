package org.palczewski.edit;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;

/*
This class contains methods to be used with tables (creation, deletion,
viewing, inserting and deleting records).

Methods will be built with predefined use (as general as possible), but
can be overridden by programs utilizing this library.
 */
public class TableMachine {
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
                        MessageFormat.format("CREATE TABLE IF NOT EXISTS {0} (date DATE PRIMARY KEY, lname CHAR(15), fname CHAR(15));", tName);
                stmt.executeUpdate(table);
                stmt.close();

            } catch (SQLException e) {
                System.out.println("SQL error in createTable(): " + e.getMessage());
            }
        } else {
            System.out.println(DatabaseMachine.NO_CONNECTION);
        }
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
                            System.out.println("Nothing to show");
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


    @Override
    public String toString() {
        return MessageFormat.format("This instance maintains the methods for use with tables\n Connection: {0}", conn);
    }
}
