package org.palczewski.test;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import org.palczewski.connect.MyDataSourceFactory;
import org.palczewski.connect.PoolManager;

import java.sql.Connection;
import java.sql.SQLException;

public class PoolManagerTest {
    /*
    This test will create multiple datasource objects and put them in
    the pool.
     */
    public static void main(String[] args) {

        // Obtain datasource
        MysqlConnectionPoolDataSource ds = MyDataSourceFactory.getMySQLDataSource();
        // create pool with space for 10 connections
        PoolManager pm = new PoolManager(ds, 5);

        // Add connections
        try {
            Connection conn1 = pm.getConnection();
            Connection conn2 = pm.getConnection();
            Connection conn3 = pm.getConnection();
            Connection conn4 = pm.getConnection();
            /*
            Will hang if last connection is added.
            // TODO: 5/23/19 Build mechanism to report if pool is full and refuses new connections.
             */
            //Connection conn5 = pm.getConnection();


        } catch (SQLException e) {
            System.out.println("Error in connections");
        }

        System.out.println("Active connections: " + pm.getActiveConnections());
        System.out.println(Thread.currentThread());
        System.out.println("Valid connections:\n\t" + pm.getValidConnection());

    }
}
