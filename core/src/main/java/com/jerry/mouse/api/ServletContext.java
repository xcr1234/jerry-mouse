package com.jerry.mouse.api;

import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ServletContext {

    URL getResource(String name);

    InputStream getResourceAsStream(String name);

    Object getAttr(String key);

    Object putAttr(String key, Object value);

    Object removeAttr(String key);

    Set<Map.Entry<String,Object>> attrSet();


    Servlet getServlet(String path);

    Collection<Servlet> getServlets();

    void addServlet(String path, Servlet servlet);

    Servlet removeServlet(String path);

    void setLogConn(boolean logConn);

    void setWelComeFiles(List<String> welComeFiles);

    void setSessionMaxAge(int sessionMaxAge);

    void setSessionCookieName(String sessionCookieName);

}
