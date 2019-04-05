package org.palczewski.core;
/*
Methods for user management (creation, deletion, etc.). Root access is
needed.
 */

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;

public class UserMachine {
    Connection conn;
    Statement stmt;

    public UserMachine() {
        // Tell user to pass connection
        System.out.println("Only an instance is created. No connection " +
                "made.");
        System.out.println("Usage: UserMachine(<your connection>)");
    }

    public UserMachine(Connection connection) {
        conn = connection;
    }

    public final void createUser(String name, String pw) {
        // Will create user
        if(conn != null) {
            try {
                stmt = conn.createStatement();
                String qry = "CREATE USER IF NOT EXISTS " + name +
            " IDENTIFIED BY \"" + pw + "\" WITH " +
                        "MAX_USER_CONNECTIONS 1 " +
                        "PASSWORD EXPIRE INTERVAL 90 DAY";
                stmt.executeUpdate(qry);
                stmt.close();

            } catch (SQLException e) {
                System.out.println("SQL error in createUser(): " + e.getMessage());
            }
        } else {
            System.out.println("No connection.");
        }
        System.out.println(MessageFormat.format("Successfully created a mySQL account for {0}.", name));

    }

    public void grantUser(String name) {
        // Will grant named users with privileges.
    }

    public void setPassword(String name, String newPW) {
        // Allows user to change password
    }
}
