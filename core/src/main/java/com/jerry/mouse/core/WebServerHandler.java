package com.jerry.mouse.core;


import com.jerry.mouse.api.*;

import com.jerry.mouse.util.upload.FileItem;
import com.jerry.mouse.util.upload.FileUploadException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

class WebServerHandler implements HttpHandler {

    private Application application;


    private Log log = LogFactory.getLog(WebServerHandler.class);


    public WebServerHandler(Application application) {
        this.application = application;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        RequestImpl request;
        ResponseImpl response;

        try{
            request = new RequestImpl(exchange,application,this);
        }catch (Exception e){
            throw new IOException("failed to create request object.",e);
        }
        try{
            response = new ResponseImpl(request,application);
        }catch (Exception e){
            throw new IOException("failed to create response object.",e);
        }
        String path = exchange.getRequestURI().getPath();
        if(application.isLogConn()){
            log.debug(exchange.getRequestMethod() + " " + path);
        }

        this.handle(path,request,response,exchange);

    }

    void handle(String path,RequestImpl request,ResponseImpl response,HttpExchange exchange) throws IOException {
        handle(path,request,response,exchange,false);
    }

    void handle(String path,RequestImpl request,ResponseImpl response,HttpExchange exchange,boolean dispatch) throws IOException {
        try{
            Servlet servlet = application.getServlet(path);
            if(servlet == null){    //如果找不到，则重定向到默认servlet
                Servlet defaultServlet = application.getDefaultServlet();
                if(defaultServlet == null){
                    throw new IOException("can't find [/default] mapping!");
                }
                handle(defaultServlet,request,response,exchange,dispatch);
                return;
            }
            //执行servlet
            handle(servlet,request,response,exchange,dispatch);
        }catch (Exception e){
            throw new IOException(e);
        }
    }

    private void handle(Servlet servlet, RequestImpl request,ResponseImpl response,HttpExchange exchange) throws IOException{
        handle(servlet,request,response,exchange,false);
    }


    private void handle(Servlet servlet, RequestImpl request,ResponseImpl response,HttpExchange exchange,boolean dispatch) throws IOException{
        try{
            servlet.service(request,response);
            response.finish();
            if(!dispatch){
                response.writeTo(exchange);
            }
        }catch (Exception e){
            Servlet errorServlet = application.getErrorServlet();
            if(servlet.equals(errorServlet)){
                throw new IOException("an error occurred in [/error] ErrorServlet",e);
            }
            request.setError(e);
            this.handle(errorServlet,request,response,exchange);
        }finally {
            if(!dispatch){
                exchange.close();
            }
        }

    }





}
