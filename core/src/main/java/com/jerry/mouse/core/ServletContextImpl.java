package com.jerry.mouse.core;

import com.jerry.mouse.api.Servlet;
import com.jerry.mouse.api.ServletContext;

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
        return application.getServlet(path);
    }
    @Override
    public Collection<Servlet> getServlets(){
        return application.getServlets();
    }
    @Override
    public void addServlet(String path,Servlet servlet){
        if(path == null || servlet == null){
            throw new IllegalArgumentException("null argument!");
        }
        application.addServlet(path,servlet);
    }
    @Override
    public void setLogConn(boolean logConn) {
        application.setLogConn(logConn);
    }
    @Override
    public void setWelComeFiles(List<String> welComeFiles) {
        application.setWelComeFiles(welComeFiles);
    }
    @Override
    public void setSessionMaxAge(int sessionMaxAge) {
        application.setSessionMaxAge(sessionMaxAge);
    }
    @Override
    public void setSessionCookieName(String sessionCookieName) {
        application.setSessionCookieName(sessionCookieName);
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
        return null;
    }
}
