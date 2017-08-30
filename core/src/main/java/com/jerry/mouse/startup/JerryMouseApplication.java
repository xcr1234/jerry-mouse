package com.jerry.mouse.startup;

import com.jerry.mouse.core.LifecyleException;
import com.jerry.mouse.server.Application;
import com.jerry.mouse.server.BaseApplication;
import com.jerry.mouse.server.BaseServer;
import com.jerry.mouse.server.Server;
import com.jerry.mouse.util.Properties;

import java.util.concurrent.FutureTask;

public class JerryMouseApplication {
    

    public static FutureTask<Server> run(Class<?> c, Properties properties){
        JerryMouseCallAble callAble = new SingleApplicationCallAble(c,properties);
        JerryMouseFutureTask futureTask = new JerryMouseFutureTask(callAble,new DefaultApplicationStartListener());
        futureTask.run();
        return futureTask;
    }
    
}
