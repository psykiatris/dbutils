package org.palczewski.edit;

import com.mysql.cj.ClientPreparedQueryBindings;
import com.mysql.cj.Session;

public class ClientPreparedQueryBindingsMachine extends ClientPreparedQueryBindings {

    public ClientPreparedQueryBindingsMachine(int parameterCount, Session sess) {
        super(parameterCount, sess);
    }
}
