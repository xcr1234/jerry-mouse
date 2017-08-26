package com.jerry.mouse.core;

import com.jerry.mouse.Config;
import com.jerry.mouse.util.Properties;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.spi.HttpServerProvider;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class WebServer extends LifecyleSupport implements Config.Server{

    private ExecutorService executor;
    private HttpServer httpserver;
    private InetSocketAddress address;
    private String context;
    private String ip;
    private int iPort;

    private final Application application;


    public WebServer(Application application) {
        this.application = application;
    }

    @Override
    protected void doInit(Properties properties) throws LifecyleException {
        ip = properties.get(IP);
        String port = properties.get(PORT);
        context = application.getContext();

        iPort = 0;
        if(port == null || port.isEmpty()){
            iPort = 8080;
        }else{
            try{
                iPort = Integer.parseInt(port);
            }catch (NumberFormatException e){
                newException("illegal server port:"+port);
            }
        }


        try {
            address = (ip == null || ip.isEmpty()) ? new InetSocketAddress(iPort) : new InetSocketAddress(ip,iPort);
            this.executor = Executors.newCachedThreadPool();
            HttpServerProvider provider = HttpServerProvider.provider();
            httpserver =provider.createHttpServer(address,100);
            httpserver.setExecutor(this.executor);
            httpserver.createContext(context,new WebServerHandler(application));
        } catch (Exception e) {
            newException(e);
        }

    }

    @Override
    protected void doStart() throws LifecyleException {
        try{
            httpserver.start();
            logger.info("server started at :" + address + context);
        }catch (Exception e){
            newException(e);
        }
    }

    @Override
    protected void doDestroy() throws LifecyleException {
        httpserver.stop(1);
        this.executor.shutdown();
    }

    String getPath(){
        return "http://" + (ip == null ? "127.0.0.1" : ip) + ":" + iPort ;
    }
}
