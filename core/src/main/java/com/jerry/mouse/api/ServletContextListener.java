package com.jerry.mouse.api;

public interface ServletContextListener {

    void onInit(ServletContext servletContext) throws Exception;

    void onDestroy(ServletContext servletContext) throws Exception;

}
