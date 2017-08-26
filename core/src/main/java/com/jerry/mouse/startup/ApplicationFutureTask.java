package com.jerry.mouse.startup;

import com.jerry.mouse.core.Application;
import com.jerry.mouse.util.Properties;


import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class ApplicationFutureTask extends FutureTask<Application> {

    private ApplicationStartListener listener;
    private Throwable t;

    public ApplicationStartListener getListener() {
        return listener;
    }

    public void setListener(ApplicationStartListener listener) {
        this.listener = listener;
    }

    public ApplicationFutureTask(Class c, Properties properties){
        super(new ApplicationCallable(c,properties));
    }

    @Override
    protected void setException(Throwable t) {
        this.t = t;
        super.setException(t);
        if(listener != null){
            listener.onError(t);
        }
    }



    @Override
    protected void done() {
        if(t == null && listener != null){
            try {
                Application application = get();
                listener.onComplete(application);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}
