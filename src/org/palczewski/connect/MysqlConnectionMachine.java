package org.palczewski.connect;

import com.mysql.cj.MysqlConnection;
import com.mysql.cj.Session;
import com.mysql.cj.conf.PropertySet;
import com.mysql.cj.exceptions.ExceptionInterceptor;

import java.util.Properties;

public class MysqlConnectionMachine implements MysqlConnection {
    @Override
    public PropertySet getPropertySet() {
        return null;
    }

    @Override
    public void createNewIO(boolean b) {

    }

    @Override
    public long getId() {
        return 0;
    }

    @Override
    public Properties getProperties() {
        return null;
    }

    @Override
    public Object getConnectionMutex() {
        return null;
    }

    @Override
    public Session getSession() {
        return null;
    }

    @Override
    public String getURL() {
        return null;
    }

    @Override
    public String getUser() {
        return null;
    }

    @Override
    public ExceptionInterceptor getExceptionInterceptor() {
        return null;
    }

    @Override
    public void checkClosed() {

    }

    @Override
    public void normalClose() {

    }

    @Override
    public void cleanup(Throwable throwable) {

    }
}
