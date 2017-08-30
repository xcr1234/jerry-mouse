package com.jerry.mouse.server;

import com.jerry.mouse.core.LifecyleException;
import com.jerry.mouse.core.LifecyleSupport;
import com.jerry.mouse.util.Properties;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.spi.HttpServerProvider;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BaseServer extends LifecyleSupport implements Server {

    private InetSocketAddress address;
    private ExecutorService executor;
    private HttpServer httpserver;
    private String ip;
    private int iPort;

    private String encoding = Charset.defaultCharset().displayName();

    private List<Application> applicationList = new ArrayList<Application>();


    @Override
    protected void doInit(Properties properties) throws LifecyleException {
        ip = properties.get(IP);
        String port = properties.get(PORT);

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

        String enc = properties.get(ENCODING);
        if(enc != null && !enc.isEmpty()){
            this.encoding = enc;
        }

        try{
            this.executor = Executors.newCachedThreadPool();
            address = (ip == null || ip.isEmpty()) ? new InetSocketAddress(iPort) : new InetSocketAddress(ip,iPort);
            HttpServerProvider provider = HttpServerProvider.provider();
            httpserver = provider.createHttpServer(address,100);
            httpserver.setExecutor(this.executor);
            httpserver.createContext("/",new DefaultServerHandler(this));

        }catch (Exception e){
            newException(e);
        }
    }

    @Override
    protected void doStart() throws LifecyleException {
        try{
            httpserver.start();
            logger.info("server started at :" + address);
        }catch (Exception e){
            newException(e);
        }
    }

    @Override
    protected void doDestroy() throws LifecyleException {
        httpserver.stop(0);
    }

    @Override
    public Collection<Application> getApplications() {
        return applicationList;
    }

    @Override
    public InetSocketAddress getAddress() {
        return address;
    }

    @Override
    public String getPath() {
        return "http://" + (ip == null ? "127.0.0.1" : ip) + ":" + iPort ;
    }

    @Override
    public void addApplication(Application application) {
        applicationList.add(application);
        try{
            httpserver.removeContext(application.getContext());
        }catch (IllegalArgumentException e){}
        httpserver.createContext(application.getContext(),new ApplicationServerHandler(application,this));
    }

    @Override
    public String getEncoding() {
        return this.encoding;
    }
}
