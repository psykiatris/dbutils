package org.palczewski;

import org.palczewski.connect.MainConnect;

public class ConnectDemo {

    public static void main(String[] args) {

        MainConnect mc = new MainConnect();
        System.out.println("Connecting to the mySQL Server...");
        mc.doConnect("diabpro", "diabpro");

    }
}
