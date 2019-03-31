package org.palczewski.test;

import org.palczewski.connect.MainConnect;

public class MainConnectTest {
    MainConnect mc = new MainConnect();

    @org.testng.annotations.Test
    public void testDoConnect() {
        mc.doConnect("root", "Pe99er@1");

    }
}