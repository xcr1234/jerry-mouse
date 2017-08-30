package com.jerry.mouse.server;

import com.jerry.mouse.api.Servlet;
import com.jerry.mouse.core.RequestImpl;
import com.jerry.mouse.core.ResponseImpl;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

public class ApplicationServerHandler implements HttpHandler {

    private Log log = LogFactory.getLog(ApplicationServerHandler.class);

    private Application application;
    private Server server;

    public ApplicationServerHandler(Application application, Server server) {
        this.application = application;
        this.server = server;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        RequestImpl request;
        ResponseImpl response;
        try{
            response = new ResponseImpl(application,exchange);
        }catch (Exception e){
            throw new IOException("failed to create response object.",e);
        }
        try{
            request = new RequestImpl(exchange,application);
        }catch (Exception e){
            throw new IOException("failed to create request object.",e);
        }
        response.setRequest(request);
        String path = exchange.getRequestURI().getPath();
        if(application.isLogConn()){
            log.debug(exchange.getRequestMethod() + " " + path);
        }
        this.handlePath(path,request,response,exchange);
    }

    public void handlePath(String path,RequestImpl request,ResponseImpl response,HttpExchange exchange){
        try {
            Servlet servlet = application.getServlet(path);
            if(servlet == null){
                Servlet defaultServlet = application.getDefaultServlet();
                if(defaultServlet == null){
                    throw new IOException("can't find [/default] mapping!");
                }
                handleServlet(defaultServlet,request,response,exchange);
                return;
            }
            handleServlet(servlet,request,response,exchange);
        }catch (Exception e){
            log.error("an unexpected error occurred.",e);
        }
    }

    private void handleServlet(Servlet servlet, RequestImpl request,ResponseImpl response,HttpExchange exchange) throws IOException{
        try{
            servlet.service(request,response);
            response.flush();
            response.writeTo(exchange);
        }catch (Exception e){
            Servlet errorServlet = application.getErrorServlet();
            if(servlet.equals(errorServlet)){
                log.error("an error occurred in [/error] ErrorServlet",e);
            }
            request.setErrorServlet(servlet);
            request.setError(e);
            try{
                this.handleServlet(errorServlet,request,response,exchange);
            }catch (Exception ex){
                log.error("an error occurred in [/error] ErrorServlet",ex);
            }
        }finally {
            response.close();
        }
    }


}
