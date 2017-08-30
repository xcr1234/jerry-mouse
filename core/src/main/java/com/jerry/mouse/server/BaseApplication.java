package com.jerry.mouse.server;

import com.jerry.mouse.api.*;
import com.jerry.mouse.core.*;
import com.jerry.mouse.util.ClassUtils;
import com.jerry.mouse.util.Properties;

import java.nio.charset.Charset;
import java.util.*;

public class BaseApplication extends LifecyleSupport implements Application{

    private Class c;
    private Server server;

    public BaseApplication(Class c,Server server) {
        this.c = c;
        this.server = server;
    }
    private StaticManager staticManager = new DefaultStaticManager();
    private String context;
    private final ApplicationServerHandler handler = new ApplicationServerHandler(this,server);
    private final ServletContext servletContext = new ServletContextImpl(this);
    private final Map<String ,Servlet> servletMap = new HashMap<String, Servlet>();
    private final List<String> welComeFiles = new ArrayList<String>();
    private boolean dirView = false;
    private boolean logConn = false;
    private int sessionMaxAge = 1800;
    private String sessionCookieName = "MouseSessionId";
    private String encoding = Charset.defaultCharset().displayName();

    private final List<ServletContextListener> listeners = new ArrayList<ServletContextListener>();
    private final Set<String> extensionsSet = new HashSet<String>();
    private final SessionManager sessionManager = new DefaultSessionManager();

    @Override
    protected void doInit(Properties properties) throws LifecyleException {
        if(c.isAnnotationPresent(WebContext.class)){
            WebContext webContext = (WebContext) c.getAnnotation(WebContext.class);
            context = webContext.value();
        }
        if(context == null || context.isEmpty()){
            context = c.getSimpleName();
        }
        if(!context.startsWith("/")){
            context =  "/"  + context;
        }
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
            this.staticManager = new StaticManager(staticResource.target(),staticResource.prefix());
            this.staticManager.setExpires(staticResource.expires());
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
    }


    protected Servlet createServlet(Class clazz) throws LifecyleException {
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

    @Override
    protected void doStart() throws LifecyleException {

    }

    @Override
    protected void doDestroy() throws LifecyleException {
        for(ServletContextListener listener :listeners){
            try {
                listener.onDestroy(servletContext);
            } catch (Exception e) {
                logger.error("an error occurred while application was destroying",e);
            }
        }
    }

    @Override
    public String getContext() {
        return context;
    }

    @Override
    public String getEncoding() {
        return encoding;
    }

    @Override
    public Class getMainClass() {
        return c;
    }

    @Override
    public List<String> welcomeFiles() {
        return welComeFiles;
    }

    @Override
    public Servlet getServlet(String path) {
        if(!"/".equals(context) &&  path.startsWith(context)){
            int i = path.indexOf(context);
            path = path.substring(i + context.length());
        }
        return servletMap.get(path);
    }

    @Override
    public Servlet getErrorServlet() {
        return servletMap.get("/error");
    }

    @Override
    public Servlet getNotFoundServlet() {
        return servletMap.get("/404");
    }

    @Override
    public Servlet getDefaultServlet() {
        return servletMap.get("/default");
    }

    public boolean isLogConn() {
        return logConn;
    }

    @Override
    public Collection<Servlet> getServlets() {
        return servletMap.values();
    }

    @Override
    public void addServlet(String path, Servlet servlet) {
        servletMap.put(path,servlet);
    }

    @Override
    public Servlet removeServlet(String path) {
        return servletMap.remove(path);
    }


    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public StaticManager getStaticManager() {
        return staticManager;
    }

    @Override
    public boolean viewDir() {
        return dirView;
    }

    @Override
    public Set<String> cacheExtensions() {
        return extensionsSet;
    }

    @Override
    public String getSessionCookieName() {
        return sessionCookieName;
    }

    @Override
    public int getSessionMaxAge() {
        return sessionMaxAge;
    }

    @Override
    public SessionManager getSessionManager() {
        return sessionManager;
    }

    @Override
    public String getBasePath() {
        return server.getPath() + context + "/";
    }

    @Override
    public ApplicationServerHandler getHandler() {
        return handler;
    }
}
