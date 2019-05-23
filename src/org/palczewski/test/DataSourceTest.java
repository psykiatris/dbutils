package org.palczewski.test;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import org.palczewski.connect.MyDataSourceFactory;
import org.palczewski.connect.PoolManager;


import java.sql.Connection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;

public class DataSourceTest {

    public static void main(String[] args) {

        testDataSource("mysql");
        System.out.println("*************");


    }

    private static void testDataSource(String dbType) {

        MysqlConnectionPoolDataSource ds = null;
        PoolManager pm = null;
        if("mysql".equals(dbType)) {
            ds = MyDataSourceFactory.getMySQLDataSource();
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
            System.out.println(MessageFormat.format("Active connections: {0}\nLise of valid connections:\n\t{1}\nInactive connection:{2}",
                    pm.getActiveConnections(), pm.getValidConnection(), pm.getInactiveConnections()));



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
