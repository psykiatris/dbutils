package org.palczewski.proposed;

import com.mysql.cj.AppendingBatchVisitor;

public class MyBatchVisitor extends AppendingBatchVisitor {

    byte[] bytes = null;

    public MyBatchVisitor(byte[] bytes) {

        this.bytes = bytes;

    }

    public void appendTo(byte[] bytes) {
        append(bytes);
    }

    public void incrementBatch() {
        increment();
        System.out.println("Batch was incremented");
    }
}
