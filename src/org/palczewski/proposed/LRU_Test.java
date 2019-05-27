package org.palczewski.proposed;

import com.mysql.cj.MysqlConnection;
import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;

import java.sql.SQLException;

public class LRU_Test {

    public static void main(String[] args) {

        PerConnLRUFactory lru = new PerConnLRUFactory();

        String url = "jdbc:mysql://localhost:3306/";
        MysqlDataSource ds = new MysqlDataSource();
        ds.setURL(url);
        ds.setUser("diabpro");
        ds.setPassword("diabpro");

        try {
            MysqlConnection conn = (MysqlConnection) ds.getConnection();
            lru.getInstance(conn, url, 128, 8);
            // proof lru works
            System.out.println(lru);



        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
}
