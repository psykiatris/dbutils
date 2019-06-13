package org.palczewski.connect;
/*
Factory method that returns a Connection object based on the method called.
 */

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

public enum MyDataSourceFactory {
    ;

    /*
    This method will be used when a new user creates an account, as the
    properties file contains the user required to create other users.
     */

    public static MysqlConnectionPoolDataSource defaultUser() {

        Properties props = new Properties();
        FileInputStream fis = null;
        MysqlConnectionPoolDataSource mysqlDS = null;
        try {
            fis = new FileInputStream("db.properties");
            props.load(fis);
            mysqlDS = new MysqlConnectionPoolDataSource();

            mysqlDS.setURL(props.getProperty("MYSQL_DB_URL"));
            mysqlDS.setUser(props.getProperty("MYSQL_DB_USERNAME"));
            mysqlDS.setPassword(props.getProperty("MYSQL_DB_PASSWORD"));
            mysqlDS.setVerifyServerCertificate(false);
            mysqlDS.setUseSSL(true);



        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO exception in MyDataSourceFactory(): " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("SQL error in getMysqlDataSource(): " + e.getMessage());
        }
        return mysqlDS;
    }

    /*
    This method will be used for users with their own
    credientials.
     */

    static MysqlConnectionPoolDataSource withUser(String userN,
                                              String userpass,
                                              String dbName) {

        MysqlConnectionPoolDataSource mySqlDS =
                new MysqlConnectionPoolDataSource();

        mySqlDS.setURL("jdbc:mysql://localhost:3306/");
        try {
            // set config

            mySqlDS.setVerifyServerCertificate(false);
            mySqlDS.setUseSSL(true);

        } catch (SQLException e) {
            System.out.println("SQL error in factory method: " + e.getMessage());
        }
        mySqlDS.setUser(userN);
        mySqlDS.setPassword(userpass);


            return mySqlDS;

    }
}
