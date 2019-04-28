package org.palczewski.edit;

import com.mysql.cj.CancelQueryTask;
import com.mysql.cj.CancelQueryTaskImpl;
import com.mysql.cj.Query;

public class CancelQueryTaskMachine implements CancelQueryTask {
    @Override
    public boolean cancel() {
        return false;
    }

    @Override
    public Throwable getCaughtWhileCancelling() {
        return null;
    }

    @Override
    public void setCaughtWhileCancelling(Throwable throwable) {

    }

    @Override
    public Query getQueryToCancel() {
        return null;
    }

    @Override
    public void setQueryToCancel(Query query) {

    }
}
