package org.palczewski.test;

import org.palczewski.connect.MyDataSourceFactory;

import java.sql.Connection;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DataSourceTest {

    public static void main(String[] args) {

        testDataSource("mysql");
        System.out.println("*************");

    }

    private static void testDataSource(String dbType) {

        DataSource ds = null;
        if("mysql".equals(dbType)) {
            ds = MyDataSourceFactory.getMySQLDataSource();
        } else {
            System.out.println("No dbType known");
        }

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = ds.getConnection();
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
