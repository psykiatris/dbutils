package org.palczewski.core;

import com.mysql.cj.MessageBuilder;
import com.mysql.cj.protocol.Message;

import java.util.List;

public class MessageBuilderMachine implements MessageBuilder {
    @Override
    public Message buildSqlStatement(String s) {
        return null;

    }

    @Override
    public Message buildClose() {
        return null;
    }

    @Override
    public Message buildSqlStatement(String s, List list) {
        return null;
    }
}
