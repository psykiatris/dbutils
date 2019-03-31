package org.palczewski.connect;

public class MainConnectTest {
    MainConnect mc = new MainConnect();

    @org.testng.annotations.Test
    public void testDoConnect() {
        mc.doConnect("root", "Pe99er@1");

    }
}