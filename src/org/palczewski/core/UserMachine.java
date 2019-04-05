package org.palczewski.core;
/*
Methods for user management (creation, deletion, etc.). Root access is
needed.
 */

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class UserMachine {
    Connection conn;
    Statement stmt;

    UserMachine() {
        // Tell user to pass connection
        System.out.println("Only an instance is created. No connection " +
                "made.");
        System.out.println("Usage: UserMachine(<your connection>)");
    }

    UserMachine(Connection connection) {
        conn = connection;
    }

    public void createUser(String name, String pw) {
        // Will create user
        if(conn != null) {
            try {
                stmt = conn.createStatement();
                String qry = "CREATE USER IF NOT EXISTS" + name +
            "IDENTIFIED BY \"" + pw + "\" WITH MAX_USER_CONNECTIONS 1 " +
                        "PASSWORD EXPIRE INTERVAL 90 DAY";
                stmt.executeUpdate(qry);
                stmt.close();

            } catch (SQLException e) {
                System.out.println("SQL error in createUser(): " + e.getMessage());
            }
        } else {
            System.out.println("No connection.");
        }

    }
}
