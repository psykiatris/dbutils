package org.palczewski.test;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import org.palczewski.connect.MyDataSourceFactory;
import org.palczewski.connect.PoolManager;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.sql.*;
import java.sql.Connection;
import java.sql.SQLException;

import static org.testng.Assert.*;

public class PoolManagerTest {

    MysqlConnectionPoolDataSource tds = null;
    PoolManager pm = null;

    @BeforeMethod
    public void setUp() {
        // Get normal connection tp put into pool.
         tds =
                MyDataSourceFactory.defaultUser();

        // Create pool
        pm = new PoolManager(tds, 5);
    }

    @Test
    public void testDispose() {
    }

    @Test
    public void testGetConnection() {
        // Get connection
        try (Connection conn = pm.getConnection()) {

            if (conn != null) {
                System.out.println("Successful connection");
            } else {
                System.out.println("Connection failed");
            }

        } catch (SQLException e) {
            System.out.println("Error connecting: " + e.getMessage());
        }

        System.out.println("\tTesting multiple connections:");
        try (Connection conn = pm.getConnection()) {

            // Second connection
            Connection conn2 = pm.getConnection();
            //Total connections
            System.out.println("Connections: " + pm.getActiveConnections());

        } catch (SQLException e) {
            System.out.println("Error making first connection.");
        }

    }

        @Test
    public void testGetValidConnection() {
    }

    @Test
    public void testGetActiveConnections() {
    }

    @Test
    public void testGetInactiveConnections() {
    }
}