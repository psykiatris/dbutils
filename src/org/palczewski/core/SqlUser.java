package org.palczewski.core;
/*
Create a SQL user type
 */
public class SqlUser {

    private final String user;
    private final String password;
    private final String defaultDatabase;

    public SqlUser(String user, String password, String defaultDatabase) {

        this.user = user;
        this.password = password;
        this.defaultDatabase = defaultDatabase;

    }
}
