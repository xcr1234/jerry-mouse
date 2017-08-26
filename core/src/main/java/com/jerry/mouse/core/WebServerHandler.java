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
        try{
            RequestImpl request = new RequestImpl(exchange,application.getServletContext(),application);
            ResponseImpl response = new ResponseImpl(application.getServletContext(),request,application);
            //找到servlet
            String path = exchange.getRequestURI().getPath();
            if(application.isLogConn()){
                log.debug(exchange.getRequestMethod() + " " + path);
            }
            handle(path,request,response,exchange);
        }catch (Exception e){
            if(e instanceof IOException){
                throw (IOException)e;
            }
            log.error(e);
        }
    }

    private void handle(String path,RequestImpl request,ResponseImpl response,HttpExchange exchange){
        //构造request和response
        try{
            Servlet servlet = application.getServlet(path);
            if(servlet == null){    //如果找不到，则重定向到默认servlet
                handle(application.getDefaultServlet(),request,response,exchange);
                return;
            }
            //执行servlet
            handle(servlet,request,response,exchange);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void handle(Servlet servlet, RequestImpl request,ResponseImpl response,HttpExchange exchange) throws IOException{
        try{
            servlet.service(request,response);
            if(response.getDispatch() != null){
                Servlet s = application.getServlet(response.getDispatch());
                if(s == null){
                    s = application.getServlet("/404");
                }
                if(s == null){
                    throw new IOException("can't find [/404] mapping!");
                }
                response.dispatch(null);
                handle(s,request,response,exchange);
                return;
            }
            response.writeTo(exchange);
        }catch (Exception e){
            if(servlet.equals(application.getErrorServlet())){
                throw new IOException(e);
            }
            request.setError(e);
            handle(application.getErrorServlet(),request,response,exchange);
        }finally {
            exchange.close();
        }

    }

}
