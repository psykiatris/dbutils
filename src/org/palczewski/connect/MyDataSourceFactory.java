package org.palczewski.connect;
/*
A DataSource Factory class to handle connections
 */

import com.mysql.cj.jdbc.MysqlDataSource;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

public class MyDataSourceFactory {

    /*
    This method will be used when a new user creates an account, as the
    properties fild contains the user required to create other users.
     */

    public static final String TZ = "America/Los_Angeles";
    public static DataSource getMySQLDataSource() {

        Properties props = new Properties();
        FileInputStream fis = null;
        MysqlDataSource mysqlDS = null;
        try {
            fis = new FileInputStream("db.properties");
            props.load(fis);
            mysqlDS = new MysqlDataSource();
            mysqlDS.setServerTimezone(TZ);
            mysqlDS.setURL(props.getProperty("MYSQL_DB_URL"));
            mysqlDS.setUser(props.getProperty("MYSQL_DB_USERNAME"));
            mysqlDS.setPassword(props.getProperty("MYSQL_DB_PASSWORD"));

        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO exception in MyDataSourceFactory(): " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("SQL error in getMysqlDataSource(): " + e.getMessage());
        }
        return mysqlDS;
    }

    public static DataSource getMySQLDataSource(String userN,
                                                String userpass) {

        MysqlDataSource mySqlDS = new MysqlDataSource();

        mySqlDS.setURL("jdbc:mysql://localhost:3306/diabetes?verifyServerCertificate=false&useSSL=true");
        try {
            mySqlDS.setServerTimezone(TZ);
        } catch (SQLException e) {
            System.out.println("SQL error in factory method: " + e.getMessage());
        }
        mySqlDS.setUser(userN);
        mySqlDS.setPassword(userpass);

            return mySqlDS;

    }
}
