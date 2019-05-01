package org.palczewski.proposed;
/*
Proposed class to manage PreparedQuerys and anything of a query nature.

 */

import com.mysql.cj.conf.DatabaseUrlContainer;
import com.mysql.cj.conf.HostInfo;


public class QueryMachine {

    DatabaseUrlContainer duc = new DatabaseUrlContainer() {
        @Override
        public String getDatabaseUrl() {
            return "jdbc:mysql:";
        }
    };

    HostInfo hostinfo = new HostInfo(duc, "localhost",3306,"diabpro",
            "diabpro");






}
