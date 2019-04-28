package org.palczewski.connect;

import com.mysql.cj.DataStoreMetadataImpl;
import com.mysql.cj.Session;

public class DataStoreMetadataImplMachine extends DataStoreMetadataImpl {

    public DataStoreMetadataImplMachine(Session sess) {
        super(sess);
    }
}
