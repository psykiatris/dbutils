package org.palczewski.edit;

import com.mysql.cj.AbstractQuery;
import com.mysql.cj.NativeSession;

public class QueryMachine extends AbstractQuery {

    public QueryMachine(NativeSession sess) {
        super(sess);
    }


}
