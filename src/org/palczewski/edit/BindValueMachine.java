package org.palczewski.edit;

import com.mysql.cj.BindValue;
import com.mysql.cj.MysqlType;

import java.io.InputStream;

public class BindValueMachine implements BindValue {
    @Override
    public BindValue clone() {
        return null;
    }

    @Override
    public void reset() {

    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public void setNull(boolean b) {

    }

    @Override
    public boolean isStream() {
        return false;
    }

    @Override
    public void setIsStream(boolean b) {

    }

    @Override
    public MysqlType getMysqlType() {
        return null;
    }

    @Override
    public void setMysqlType(MysqlType mysqlType) {

    }

    @Override
    public byte[] getByteValue() {
        return new byte[0];
    }

    @Override
    public void setByteValue(byte[] bytes) {

    }

    @Override
    public InputStream getStreamValue() {
        return null;
    }

    @Override
    public void setStreamValue(InputStream inputStream, int i) {

    }

    @Override
    public int getStreamLength() {
        return 0;
    }

    @Override
    public boolean isSet() {
        return false;
    }
}
