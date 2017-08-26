package com.jerry.mouse.startup;

import com.jerry.mouse.core.Application;
import com.jerry.mouse.util.Properties;

import java.util.concurrent.Callable;

public class ApplicationCallable implements Callable<Application>{


    private Class c;
    private Properties properties;

    public ApplicationCallable(Class c, Properties properties) {
        this.c = c;
        this.properties = properties;
    }

    @Override
    public Application call() throws Exception {
        Application application = new Application(c);
        application.init(properties);
        application.start();
        return application;
    }
}
