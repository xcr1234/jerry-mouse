package com.jerry.mouse.startup;

import com.jerry.mouse.server.Server;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class JerryMouseFutureTask extends FutureTask<Server>{

    private JerryMouseCallAble callAble;
    private ApplicationStartListener listener;

    public JerryMouseFutureTask(JerryMouseCallAble callAble,ApplicationStartListener listener){
        super(callAble);
        if(listener == null){
            throw new IllegalArgumentException("null application listener!");
        }
        this.listener = listener;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        callAble.cancel();
        return true;
    }

    @Override
    protected void done() {
        try {
            Server server = get();
            listener.onComplete(server);
        } catch (InterruptedException e) {
            listener.onError(e);
        } catch (ExecutionException e) {
            listener.onError(e.getCause());
        }
    }
}
