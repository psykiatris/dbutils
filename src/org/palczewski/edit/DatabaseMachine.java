package org.palczewski.edit;

import java.sql.Connection;
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
    String dbName;  // Name of database
    Connection conn;
    Statement stmt;

    public void createDB(Connection conn, String dbName) {
        this.conn = conn;
        this.dbName = dbName;

        if(conn != null) {
            System.out.println("Creating database " + dbName + ".");
            try {
                stmt = conn.createStatement();
                String sql = "created database " + dbName;
                stmt.executeUpdate(sql);
            } catch (SQLException e) {
                System.out.println("Error creating database " + e.getMessage());
            }
            System.out.println("Database " + dbName + " ready for input.");
        }
        /*
        If no connection, do nothing.
         */
    }
}
