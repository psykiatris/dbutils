package org.palczewski.connect;

import com.mysql.jdbc.Connection;

import javax.sql.DataSource;

/*
This class will use DataSource objects to manage connections.
 */
public class DSConnectionMgr  {

    DataSource ds;

    public DSConnectionMgr() {

        Connection conn = ds.getConnection();

    }

}
