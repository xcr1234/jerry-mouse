package com.jerry.mouse.startup;


import com.jerry.mouse.core.Application;
import com.jerry.mouse.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;


public class JerryMouseApplication {

    public static FutureTask<Application> run(final Class<?> c , final Properties properties){
        return run(c, properties, new DefaultApplicationStartListener());
    }
	
	public static FutureTask<Application> run(final Class<?> c , final Properties properties, final ApplicationStartListener listener) {
        ApplicationFutureTask futureTask = new ApplicationFutureTask(c,properties);
        futureTask.setListener(listener);
        futureTask.run();
        return futureTask.getFutureTask();
    }
    public static FutureTask<Application> run(final Class<?> c , final java.util.Properties properties){
        return run(c, new Properties(properties), new DefaultApplicationStartListener());
    }

    public static FutureTask<Application> run(final Class<?> c , final java.util.Properties properties, final ApplicationStartListener listener) {
        ApplicationFutureTask futureTask = new ApplicationFutureTask(c,new Properties(properties));
        futureTask.setListener(listener);
        futureTask.run();
        return futureTask.getFutureTask();
    }



	public static void start(String u){
        try {
            // 创建一个URI实例
            java.net.URI uri = java.net.URI.create(u);
            // 获取当前系统桌面扩展
            java.awt.Desktop dp = java.awt.Desktop.getDesktop() ;
            // 判断系统桌面是否支持要执行的功能
            if (dp.isSupported(java.awt.Desktop.Action.BROWSE)) {
                // 获取系统默认浏览器打开链接
                dp.browse( uri ) ;
            }


        } catch (Exception e) {
            e.printStackTrace() ;
        }
    }
}
