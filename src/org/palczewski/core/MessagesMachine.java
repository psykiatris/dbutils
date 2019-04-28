package org.palczewski.core;

import com.mysql.cj.protocol.Message;

public class MessagesMachine implements Message {
    @Override
    public byte[] getByteBuffer() {
        return new byte[0];
    }

    @Override
    public int getPosition() {
        return 0;
    }
}
