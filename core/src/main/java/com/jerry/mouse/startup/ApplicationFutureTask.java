package com.jerry.mouse.startup;

import com.jerry.mouse.core.Application;
import com.jerry.mouse.util.Properties;


import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class ApplicationFutureTask extends FutureTask<Application> {

    private ApplicationStartListener listener;

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
    protected void done() {
        if( listener != null){
            try {
                Application application = get();
                listener.onComplete(application);
            } catch (InterruptedException e) {
                listener.onError(e);
            } catch (ExecutionException e) {
                listener.onError(e.getCause());
            }
        }
    }
}
