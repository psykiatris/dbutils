package org.palczewski.core;

/**
 * SQL User type
 */
public class SqlUser {

    private final String user;
    private final String password;
    private final String defaultDatabase;

    /**
     * Creates a SQL User instance
     * @param user Username
     * @param password Password
     * @param defaultDatabase Database Ma,e
     */
    public SqlUser(String user, String password, String defaultDatabase) {

        this.user = user;
        this.password = password;
        this.defaultDatabase = defaultDatabase;

    }
}
