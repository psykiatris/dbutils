package org.palczewski.edit;

import com.mysql.cj.ClientPreparedQuery;
import com.mysql.cj.NativeSession;

public class ClientPreparedQueryMachine extends ClientPreparedQuery {

    public ClientPreparedQueryMachine(NativeSession sess) {
        super(sess);
    }
}
