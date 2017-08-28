package com.jerry.mouse.startup;

import com.jerry.mouse.core.Application;
import com.jerry.mouse.util.Properties;


import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class ApplicationFutureTask  {

    private ApplicationStartListener listener;
    private ApplicationCallable applicationCallable;
    private FutureTask<Application> futureTask;

    public ApplicationStartListener getListener() {
        return listener;
    }

    public void setListener(ApplicationStartListener listener) {
        this.listener = listener;
    }

    public ApplicationFutureTask(Class c, Properties properties){
        applicationCallable = new ApplicationCallable(c,properties);
        futureTask = new FutureTask<Application>(applicationCallable){
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

            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                if(applicationCallable != null){
                    applicationCallable.cancel();
                }
                return true;
            }
        };
    }


    public void run(){
        this.futureTask.run();
    }




    public FutureTask<Application> getFutureTask() {
        return futureTask;
    }
}
