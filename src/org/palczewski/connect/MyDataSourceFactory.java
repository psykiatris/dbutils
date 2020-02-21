package org.palczewski.connect;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

/**
 * DataSource factory
 */
public enum MyDataSourceFactory {
    ;

    /**
     * Returns a pooled datasource using properties file
     * @return Pooled datasource
     */
    public static MysqlConnectionPoolDataSource defaultUser() {

        /*
        Reads from properties file
         */
        Properties props = new Properties();

        MysqlConnectionPoolDataSource mysqlDS = null;
        try (FileInputStream fis = new FileInputStream("db.properties")) {
            props.load(fis);
            mysqlDS = new MysqlConnectionPoolDataSource();

            mysqlDS.setURL(props.getProperty("MYSQL_DB_URL"));
            mysqlDS.setUser(props.getProperty("MYSQL_DB_USERNAME"));
            mysqlDS.setPassword(props.getProperty("MYSQL_DB_PASSWORD"));
            mysqlDS.setVerifyServerCertificate(false);
            mysqlDS.setUseSSL(true);



        } catch (FileNotFoundException e) {
            System.out.printf("File not found: %s%n", e.getMessage());
        } catch (IOException e) {
            System.out.println("IO exception in MyDataSourceFactory(): " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("SQL error in getMysqlDataSource(): " + e.getMessage());
        }
        return mysqlDS;
    }

    /**
     * Returns datasource with credentials
     * @param userN Username
     * @param userP Password
     * @param dbName Database Name
     * @return Datasource
     */
    public static MysqlConnectionPoolDataSource withUser(String userN,
                                              String userP,
                                              String dbName) {

        MysqlConnectionPoolDataSource mySqlDS =
                new MysqlConnectionPoolDataSource();

        mySqlDS.setURL("jdbc:mysql://localhost:3306/?allowPublicKeyRetrieval=true");
        // mySqlDS is not autocloseable
        try {
            // set config

            mySqlDS.setVerifyServerCertificate(false);
            mySqlDS.setUseSSL(true);

        } catch (SQLException e) {
            System.out.println("SQL error in factory method: " + e.getMessage());
        }
        mySqlDS.setUser(userN);
        mySqlDS.setPassword(userP);


            return mySqlDS;


    }
}
