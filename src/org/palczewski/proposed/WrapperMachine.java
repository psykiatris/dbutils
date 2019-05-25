package org.palczewski.proposed;

import com.mysql.cj.jdbc.ConnectionWrapper;
import com.mysql.cj.jdbc.JdbcConnection;
import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlPooledConnection;

import java.sql.SQLException;
/*
Learned something about casting....

I noticed that the pool objects were returned as a ConnectionWrapper
object, so I was trying to create a Connection Wrapper object to "see
how it's done". ConnectionWrapper works internally, so no need to really
 use it.
 */
public class WrapperMachine {


    MysqlConnectionPoolDataSource poolDS;
    MysqlPooledConnection poolConn;
    JdbcConnection jdbcConn;
    ConnectionWrapper wrapper;

    public WrapperMachine() {

        poolDS = new MysqlConnectionPoolDataSource();
        poolDS.setUrl("jdbc:mysql://localhost:3306/");
        poolDS.setUser("diabpro");
        poolDS.setPassword("diabpro");

        try {
            poolConn = (MysqlPooledConnection) poolDS.getPooledConnection();
            jdbcConn = (JdbcConnection) poolConn.getConnection();
            wrapper = new ConnectionWrapper(poolConn, jdbcConn, false);
        } catch (SQLException e) {
            System.out.println("error getting pooled connection");
        }

        System.out.println(wrapper);

    }

    public static void main(String[] args) {

        WrapperMachine wm = new WrapperMachine();

    }


}
