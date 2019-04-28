package org.palczewski.core;

import com.mysql.cj.CoreSession;
import com.mysql.cj.conf.HostInfo;
import com.mysql.cj.conf.PropertySet;

public class CoreSessionMachine extends CoreSession {

    public CoreSessionMachine(HostInfo hostInfo, PropertySet propSet) {
        super(hostInfo, propSet);
    }

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
