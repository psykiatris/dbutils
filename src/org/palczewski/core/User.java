package org.palczewski.core;
/*
Class to create a User type for the util class
 */
public class User {

    private static String user;
    private static String pass;

    public static User getInstance() {
        return new User(String name, String pass);

    }

    private User(String name, String pass) {
        user = user;
        pass = pass;

    }

}
