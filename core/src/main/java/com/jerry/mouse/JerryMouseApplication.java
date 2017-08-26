package com.jerry.mouse;


import com.jerry.mouse.core.Application;
import com.jerry.mouse.core.LifecyleException;
import com.jerry.mouse.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class JerryMouseApplication {

	private static Log log = LogFactory.getLog(JerryMouseApplication.class);
	
	public static Application run(Class<?> c ,Properties properties) {

	    if(c.getPackage() == null){
	        throw new RuntimeException(c + " shouldn't be placed in the default package!");
        }

	    long time = System.currentTimeMillis();

        Application application = new Application(c,properties);
        try {
            application.init(properties);
        } catch (LifecyleException e) {
            log.error("failed to init application",e);
            return null;
        }

        try {
            application.start();
        } catch (LifecyleException e) {
            log.error("failed to start application",e);
            return null;
        }


        log.info("application started successfully in " + (System.currentTimeMillis() - time) + " ms.");
        return application;

    }

	public static Application run(Class<?> c ,java.util.Properties properties){
		return run(c,new Properties(properties));
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
