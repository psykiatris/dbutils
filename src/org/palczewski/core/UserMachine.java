package org.palczewski.core;

import org.palczewski.edit.DatabaseMachine;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;

/**
 * User management (creation, deletion, etc.). Root access is
 * required.
 */
public class UserMachine {
    Connection conn;

    /**
     * Notifies user that a connection must be made
     */
    public UserMachine() {
        // Tell user to pass connection
        System.out.println("Only an instance is created. No connection made.");
        System.out.println("Usage: UserMachine(<your connection>)");
    }

    /**
     * Creates UserMachine instance with connection
     * @param connection SQL connection
     */
    public UserMachine(Connection connection) {
        conn = connection;
    }

    /**
     * Creates user within mySQL
     * @param name Username
     * @param pw Password
     */
    public final void createUser(String name, String pw) {
        // Will create user
        if(conn != null) {
            try (Statement stmt = conn.createStatement()){

                String qry = MessageFormat.format("CREATE USER IF NOT EXISTS {0}@'localhost' IDENTIFIED BY \"{1}\" WITH alterMAX_USER_CONNECTIONS 1 PASSWORD EXPIRE INTERVAL 90 DAY", name, pw);
                stmt.executeUpdate(qry);
                System.out.println(MessageFormat.format("Successfully created a mySQL account for {0}.", name));


            } catch (SQLException e) {
                System.out.println(MessageFormat.format("SQL error in createUser(): {0}", e.getMessage()));
            }
        } else {
            System.out.println(DatabaseMachine.NO_CONNECTION);
        }
    }

    /**
     * Sets user privileges within mySQL
     *
     * @param dbName Database name
     * @param name Username
     */
    public final void grantUser(String dbName, String name) {

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

    /**
     * Sets user's password within mySQL
     * @param name Username
     * @param newPW New password
     */
    public void setPassword(String name, String newPW) {
        // Allows user to change password
    }
}
