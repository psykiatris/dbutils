package org.palczewski.core;

import com.mysql.cj.CoreSession;

public class CoreSessionMachine extends CoreSession {

    @Override
    public void quit() {

    }

    @Override
    public String getProcessHost() {
        return null;
    }

    @Override
    public boolean isClosed() {
        return false;
    }
}
