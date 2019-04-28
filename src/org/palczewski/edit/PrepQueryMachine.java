package org.palczewski.edit;

import com.mysql.cj.AbstractPreparedQuery;
import com.mysql.cj.NativeSession;

public class PrepQueryMachine extends AbstractPreparedQuery {


    public PrepQueryMachine(NativeSession sess) {
        super(sess);
    }

    @Override
    protected long[] computeMaxParameterSetSizeAndBatchSize(int i) {
        return new long[0];
    }
}
