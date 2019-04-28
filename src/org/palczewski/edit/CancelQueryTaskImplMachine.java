package org.palczewski.edit;

import com.mysql.cj.CancelQueryTaskImpl;
import com.mysql.cj.Query;

public class CancelQueryTaskImplMachine extends CancelQueryTaskImpl {

    public CancelQueryTaskImplMachine(Query cancellee) {
        super(cancellee);
    }
}
