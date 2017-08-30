package com.jerry.mouse.startup;

import com.jerry.mouse.core.LifecyleException;
import com.jerry.mouse.server.Application;
import com.jerry.mouse.server.BaseApplication;
import com.jerry.mouse.server.BaseServer;
import com.jerry.mouse.server.Server;
import com.jerry.mouse.util.Properties;

import java.util.concurrent.Callable;

/**
 * 整个server中只有一个application，创建完毕后返回Application
 */
public class SingleApplicationCallAble implements JerryMouseCallAble {

    private Class c;
    private Properties properties;
    private BaseServer server;

    public SingleApplicationCallAble(Class c, Properties properties) {
        this.c = c;
        this.properties = properties;
    }

    @Override
    public Server call() throws Exception {
        server = new BaseServer();
        server.init(properties);
        BaseApplication application = new BaseApplication(c,server);
        application.init(properties);
        application.start();
        server.addApplication(application);
        server.start();
        return server;
    }

    @Override
    public void cancel() {
        if(server != null){
            try {
                server.destroy();
            } catch (LifecyleException e) {

            }
        }
    }
}
