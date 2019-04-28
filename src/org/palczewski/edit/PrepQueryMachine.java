package org.palczewski.edit;

import com.mysql.cj.ClientPreparedQuery;
import com.mysql.cj.NativeSession;

public class PrepQueryMachine extends ClientPreparedQuery {

    NativeSession sess;

    public PrepQueryMachine(NativeSession sess) {
        super(sess);
        this.sess = sess;
        System.out.println("Connected at: " + sess.getConnectionCreationTimeMillis());;
    }


}
