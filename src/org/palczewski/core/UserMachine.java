package org.palczewski.core;
/*
Methods for user management (creation, deletion, etc.). Root access is
needed.
 */

import org.palczewski.edit.DatabaseMachine;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;

public class UserMachine {
    Connection conn;


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
            try (Statement stmt = conn.createStatement()){

                String qry = MessageFormat.format("CREATE USER IF NOT EXISTS {0} IDENTIFIED BY \"{1}\" WITH MAX_USER_CONNECTIONS 1 PASSWORD EXPIRE INTERVAL 90 DAY", name, pw);
                stmt.executeUpdate(qry);
                System.out.println(MessageFormat.format("Successfully created a mySQL account for {0}.", name));


            } catch (SQLException e) {
                System.out.println(MessageFormat.format("SQL error in createUser(): {0}", e.getMessage()));
            }
        } else {
            System.out.println(DatabaseMachine.NO_CONNECTION);
        }
    }

    public void grantUser(String dbName, String name) {
        // Will grant named users with privileges.
        // MUST be root!!
        if(conn != null) {
            try (Statement stmt = conn.createStatement()) {

                String grant = MessageFormat.format("GRANT SELECT, INSERT, UPDATE, DELETE, REFERENCES ON {0}.* TO {1}", dbName, name);
                stmt.executeUpdate(grant);

                // Show grants
                String qry = MessageFormat.format("SHOW GRANTS FOR {0}", name);
                /*
                Must create another Statement instance once the first
                one closes. Cannot reopen!
                 */
                try(Statement stmt2 = conn.createStatement();ResultSet rs =
                        stmt2.executeQuery(qry)) {
                    while(rs.next()) {
                        System.out.println(rs.getString(1));
                    }
                }

            } catch (SQLException e) {
                System.out.println(MessageFormat.format("SQL error in grantUser(): {0}", e.getMessage()));
            }

        } else {
            System.out.println(DatabaseMachine.NO_CONNECTION);
        }
    }

    public void setPassword(String name, String newPW) {
        // Allows user to change password
    }
}
