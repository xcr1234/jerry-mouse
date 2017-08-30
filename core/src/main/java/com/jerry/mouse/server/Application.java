package com.jerry.mouse.server;

import com.jerry.mouse.Config;
import com.jerry.mouse.api.Servlet;
import com.jerry.mouse.api.ServletContext;
import com.jerry.mouse.core.SessionManager;
import com.jerry.mouse.core.StaticManager;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface Application extends Config.Application{

    ApplicationServerHandler getHandler();

    String getContext();

    String getEncoding();

    Class getMainClass();

    List<String> welcomeFiles();

    Servlet getServlet(String path);

    Servlet getErrorServlet();

    Servlet getNotFoundServlet();

    Servlet getDefaultServlet();

    Collection<Servlet> getServlets();

    void addServlet(String path,Servlet servlet);

    Servlet removeServlet(String path);

    ServletContext getServletContext();

    StaticManager getStaticManager();

    boolean viewDir();

    boolean isLogConn();

    Set<String> cacheExtensions();

    String getSessionCookieName();

    int getSessionMaxAge();

    SessionManager getSessionManager();

    String getBasePath();




}
