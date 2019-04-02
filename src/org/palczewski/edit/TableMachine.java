package org.palczewski.edit;

import java.sql.Connection;
import java.sql.Statement;

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
        System.out.println("Instance created, but no connection made");
        System.out.println("Call constructor with connection object");
        System.out.println("Example: TableMachine tm = new TableMachine" +
                "(sonnection)");
        return;
    }

    @Override
    public String toString() {
        return "This instance maintains the methods for use with " +
                "tables\n Connection: " + conn;
    }
}
