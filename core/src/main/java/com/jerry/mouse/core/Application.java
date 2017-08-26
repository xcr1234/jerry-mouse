package com.jerry.mouse.core;

import com.jerry.mouse.Config;
import com.jerry.mouse.api.*;
import com.jerry.mouse.util.ClassUtils;
import com.jerry.mouse.util.Properties;


import java.nio.charset.Charset;
import java.util.*;

public class Application extends LifecyleSupport implements Config.Application{
    private Class c;
    private Properties properties;
    private StaticManage staticManage = new DefaultStaticManage();
    private WebServer server;
    private String context;

    private final ServletContext servletContext = new ServletContextImpl(this);

    private Map<String ,Servlet> servletMap = new HashMap<String, Servlet>();
    private List<String> welComeFiles = new ArrayList<String>();

    private boolean dirView = false;
    private boolean logConn = false;
    private int sessionMaxAge = 1800;
    private String sessionCookieName = "MouseSessionId";
    private String encoding = Charset.defaultCharset().displayName();

    private final List<ServletContextListener> listeners = new ArrayList<ServletContextListener>();
    private final Set<String> extensionsSet = new HashSet<String>();


    public Application(Class c, Properties properties) {
        this.c = c;
        this.properties = properties;
        this.server = new WebServer(this);
        context = properties.get(CONTEXT);

        if(context == null || context.isEmpty()){
            context = "/";
        }
        if(!context.startsWith("/")){
            context =  "/"  + context;
        }
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    int getSessionMaxAge() {
        return sessionMaxAge;
    }

    String getSessionCookieName() {
        return sessionCookieName;
    }

    String getContext(){
        return context;
    }

    StaticManage getStaticManage() {
        return staticManage;
    }

    public void setWelComeFiles(List<String> welComeFiles) {
        this.welComeFiles = welComeFiles;
    }

    public void setLogConn(boolean logConn) {
        this.logConn = logConn;
    }

    public void setSessionMaxAge(int sessionMaxAge) {
        this.sessionMaxAge = sessionMaxAge;
    }

    public void setSessionCookieName(String sessionCookieName) {
        this.sessionCookieName = sessionCookieName;
    }

    public void setStaticManage(StaticManage staticManage) {
        this.staticManage = staticManage;
    }

    @Override
    protected void doInit(Properties properties) throws LifecyleException {
        dirView = properties.getBool(DIR_VIEW);
        logConn = properties.getBool(LOG_CONN);
        String enc = properties.get(ENCODING);
        if(enc != null && !enc.isEmpty()){
            this.encoding = enc;
        }
        List<Class> classes = ClassUtils.scanPackage(c.getPackage().getName(),new WebClassFilter());
        for(Class clazz:classes){
            if(clazz.isAnnotationPresent(WebServlet.class)){
                Servlet servlet = createServlet(clazz);
                WebServlet webServlet = (WebServlet) clazz.getAnnotation(WebServlet.class);
                for(String mapping :webServlet.value()){
                    servletMap.put(mapping,servlet);
                }
                logger.info("mapped " + Arrays.toString(webServlet.value()) + " to " + clazz);
            }
        }
        if(!servletMap.containsKey("/default")){
            Servlet defaultServlet = createServlet(DefaultServlet.class);
            this.servletMap.put("/default",defaultServlet);
            logger.info("mapped [/default] to " + DefaultServlet.class);
        }
        if(!servletMap.containsKey("/error")){
            this.servletMap.put("/error",createServlet(ErrorServlet.class));
            logger.info("mapped [/error] to " + ErrorServlet.class);
        }
        if(!servletMap.containsKey("/404")){
            this.servletMap.put("/404",createServlet(NotFoundServlet.class));
            logger.info("mapped [/404] to " + NotFoundServlet.class);
        }
        if(c.isAnnotationPresent(FreeMarkerSupport.class)){
            FreeMarkerSupport freeMarkerSupport = (FreeMarkerSupport)c.getAnnotation(FreeMarkerSupport.class);
            this.freeMarkerSupport = freeMarkerSupport;
            Servlet servlet = createServlet(FreeMarkerServlet.class);
            String mapping = freeMarkerSupport.mapping();
            if(!mapping.startsWith("/")){
                mapping = "/" + mapping;
            }
            servletMap.put(mapping,servlet);
            logger.info("mapped ["+mapping+"] to " + FreeMarkerServlet.class);
        }
        if(c.isAnnotationPresent(StaticResource.class)){
            StaticResource staticResource = (StaticResource) c.getAnnotation(StaticResource.class);
            this.staticManage = new StaticManage(staticResource.target(),staticResource.prefix());
            this.staticManage.setExpires(staticResource.expires());
            logger.info("mapped static resources [" + staticResource.prefix() + "*] to classpath:/" + staticResource.target());
            Collections.addAll(extensionsSet,staticResource.cacheExtensions());
        }
        if(c.isAnnotationPresent(WelComeFiles.class)){
            WelComeFiles welComeFiles = (WelComeFiles) c.getAnnotation(WelComeFiles.class);
            this.welComeFiles.addAll(Arrays.asList(welComeFiles.value()));
            logger.info("mapped [/] to welcome files :" + this.welComeFiles);
        }
        if(c.isAnnotationPresent(SessionConfig.class)){
            SessionConfig sessionConfig = (SessionConfig) c.getAnnotation(SessionConfig.class);
            sessionMaxAge = sessionConfig.maxAge();
            sessionCookieName = sessionConfig.cookieName();
        }
        if(c.isAnnotationPresent(WebListener.class)){
            WebListener webListener = (WebListener) c.getAnnotation(WebListener.class);
            for(Class<? extends ServletContextListener> clz : webListener.listeners()){
                try {
                    listeners.add(clz.newInstance());
                } catch (Exception e) {
                    throw new LifecyleException("can't create listener:"+clz,e);
                }
            }
        }

        this.server.init(properties);
    }

    private FreeMarkerSupport freeMarkerSupport;

    public FreeMarkerSupport getFreeMarkerSupport() {
        return freeMarkerSupport;
    }

    public boolean isDirView() {
        return dirView;
    }

    boolean isLogConn() {
        return logConn;
    }

    List<String> getWelComeFiles() {
        return welComeFiles;
    }

    @Override
    protected void doStart() throws LifecyleException {
        this.server.start();
        for(ServletContextListener listener :listeners){
            try {
                listener.onInit(servletContext);
            } catch (Exception e) {
                throw new LifecyleException(e);
            }
        }
    }

    @Override
    protected void doDestroy() throws LifecyleException {
        try{
            this.server.destroy();
        }finally {
            for(ServletContextListener listener :listeners){
                try {
                    listener.onDestroy(servletContext);
                } catch (Exception e) {

                }
            }
        }

    }

    private Servlet createServlet(Class clazz) throws LifecyleException {
        try{
            Servlet servlet = (Servlet) clazz.newInstance();
            if(servlet instanceof ApplicationAwareServlet){
                ((ApplicationAwareServlet) servlet).setApplication(this);
            }
            return servlet;
        }catch (Exception e){
            throw new LifecyleException("can't create Servlet:" + clazz,e);
        }
    }

    Servlet getServlet(String path){
        if(path.startsWith(context)){
            int i = path.indexOf(context);
            path = path.substring(i + context.length());
        }
        return servletMap.get(path);
    }

    Servlet getDefaultServlet(){
        return servletMap.get("/default");
    }

    Servlet getErrorServlet(){
        return servletMap.get("/error");
    }

    Collection<Servlet> getServlets(){
        return servletMap.values();
    }

    String getBasePath(){
        return server.getPath() + context + "/";
    }


    void addServlet(String path,Servlet servlet){
        if(!path.startsWith("/")){
            path = "/" + path;
        }
        servletMap.put(path,servlet);
    }

    public Set<String> getExtensionsSet() {
        return extensionsSet;
    }

    public String getEncoding() {
        return encoding;
    }
}
