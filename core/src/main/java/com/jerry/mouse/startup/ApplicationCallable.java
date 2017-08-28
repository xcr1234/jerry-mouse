package com.jerry.mouse.startup;

import com.jerry.mouse.core.Application;
import com.jerry.mouse.core.LifecyleException;
import com.jerry.mouse.util.Properties;

import java.util.concurrent.Callable;

public class ApplicationCallable implements Callable<Application>{


    private Class c;
    private Properties properties;
    private Application application;

    public ApplicationCallable(Class c, Properties properties) {
        this.c = c;
        this.properties = properties;
    }

    @Override
    public Application call() throws Exception {
        application = new Application(c);
        application.init(properties);
        application.start();
        return application;
    }

    public void cancel(){
        if(application != null){
            try {
                application.destroy();
            } catch (Exception e) {

            }
        }
    }
}
