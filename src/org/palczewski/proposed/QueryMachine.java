package org.palczewski.proposed;
/*
Proposed class to manage server-side PreparedQuerys and anything of a
query
nature.

 */

import com.mysql.cj.AbstractPreparedQuery;
import com.mysql.cj.NativeSession;
import com.mysql.cj.conf.DefaultPropertySet;


public class QueryMachine extends AbstractPreparedQuery {

    DefaultPropertySet ps = new DefaultPropertySet();
    NativeSession ns;


    public QueryMachine(NativeSession sess) {
        super(sess);
        ns = sess;

    }


    @Override
    protected long[] computeMaxParameterSetSizeAndBatchSize(int i) {
        return new long[0];
    }
}
