package com.jerry.mouse;

public interface Config {

    interface Application{
        String CONTEXT = "application.context";
        String LOG_CONN = "application.logger.conn";
        String DIR_VIEW = "application.dirView";
        String ENCODING = "application.encoding";
    }

    interface Server{
        String IP = "server.ip";
        String PORT = "server.port";
        String MAX_CONN = "server.connect";

    }

}
