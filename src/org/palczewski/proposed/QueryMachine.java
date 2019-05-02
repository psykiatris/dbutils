package org.palczewski.proposed;
/*
Proposed class to manage server-side PreparedQuerys and anything of a
query
nature.

 */

import com.mysql.cj.NativeSession;
import com.mysql.cj.conf.DefaultPropertySet;
import com.mysql.cj.conf.HostInfo;

public class QueryMachine {

    DefaultPropertySet ps = new DefaultPropertySet();
    NativeSession ns = new NativeSession(new HostInfo(), ps);

    public QueryMachine() {


    }







}
