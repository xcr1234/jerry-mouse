package com.jerry.mouse.core;

import com.jerry.mouse.api.RequestDispatcher;
import com.jerry.mouse.api.Servlet;
import com.jerry.mouse.api.ServletContext;
import com.jerry.mouse.server.Application;

import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ServletContextImpl implements ServletContext {


    private Application application;


    public ServletContextImpl(Application application) {
        this.application = application;
    }

    @Override
    public Servlet getServlet(String path){
        if(path == null){
            throw new IllegalArgumentException("null path!");
        }
        return application.getServlet(path);
    }
    @Override
    public Collection<Servlet> getServlets(){
        return application.getServlets();
    }
    @Override
    public void addServlet(String path,Servlet servlet){
        if(path == null){
            throw new IllegalArgumentException("null path!");
        }
        if(servlet == null){
            throw new IllegalArgumentException("null servlet!");
        }
        application.addServlet(path,servlet);
    }


    @Override
    public Servlet removeServlet(String path) {
        if(path == null){
            throw new IllegalArgumentException("null path!");
        }
        return application.removeServlet(path);
    }


    @Override
    public URL getResource(String name) {
        return getClass().getClassLoader().getResource(name);
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        return getClass().getClassLoader().getResourceAsStream(name);
    }
    private Map<String,Object> map = new ConcurrentHashMap<String, Object>();

    @Override
    public Object getAttr(String key) {
        return map.get(key);
    }

    @Override
    public Object putAttr(String key, Object value) {
        return map.put(key,value);
    }

    @Override
    public Object removeAttr(String key) {
        return map.remove(key);
    }

    @Override
    public Set<Map.Entry<String, Object>> attrSet() {
        return map.entrySet();
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        return new RequestDispatcherImpl(path,application);
    }
}
