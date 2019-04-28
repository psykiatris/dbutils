package org.palczewski.connect;

import com.mysql.cj.DataStoreMetadata;

public class DataStoreMetadataMachine implements DataStoreMetadata {
    @Override
    public boolean schemaExists(String s) {
        return false;
    }

    @Override
    public boolean tableExists(String s, String s1) {
        return false;
    }

    @Override
    public long getTableRowCount(String s, String s1) {
        return 0;
    }
}
