package org.palczewski.test;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import org.palczewski.connect.MyDataSourceFactory;
import org.palczewski.connect.PoolManager;


import java.sql.Connection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class DataSourceTest {

    public static void main(String[] args) {

        testDataSource("mysql");
        System.out.println("*************");


    }

    private static void testDataSource(String dbType) {

        MysqlConnectionPoolDataSource ds = null;
        PoolManager pm = null;
        if("mysql".equals(dbType)) {
            ds = MyDataSourceFactory.defaultUser();
            pm = new PoolManager(ds, 10);
        } else {
            System.out.println("No dbType known");
        }

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = pm.getConnection();
            conn.setCatalog("diabetes");
            stmt = conn.createStatement();
            rs = stmt.executeQuery("select empId, name from Employee");
            while(rs.next()) {
                System.out.println("Employee ID: " + rs.getInt("empId") + ", Name: " + rs.getString("name"));
            }




        } catch (SQLException e) {
            System.out.println("SQL error in testDatabase(): " + e.getMessage());
        } finally {
            try {
                if(rs !=  null) rs.close();
                if(stmt != null) stmt.close();
                if(conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("SQL error in closing block");
            }
        }

    }


}
