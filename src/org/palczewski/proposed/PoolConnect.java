package org.palczewski.proposed;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlPooledConnection;

/*
Proposed class to managed pooled connections
 */
public class PoolConnect {

    MysqlConnectionPoolDataSource pds;
    MysqlPooledConnection pconn;

    PoolConnect() {

    }
}
