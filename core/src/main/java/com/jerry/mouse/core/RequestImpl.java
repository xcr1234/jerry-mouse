package com.jerry.mouse.core;

import com.jerry.mouse.api.*;
import com.jerry.mouse.util.HeaderMap;
import com.jerry.mouse.util.StreamReuse;
import com.jerry.mouse.util.upload.FileItem;
import com.jerry.mouse.util.upload.FileUploadBase;
import com.jerry.mouse.util.upload.FileUploadException;
import com.jerry.mouse.util.upload.RequestContext;
import com.jerry.mouse.util.upload.disk.DiskFileItemFactory;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URI;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RequestImpl implements Request {

    private HttpExchange exchange;
    private InputStream in;
    private Map<String,Object> map = new ConcurrentHashMap<String, Object>();
    private Map<String,Cookie> cookieMap = new HashMap<String, Cookie>();
    private Map<String,String> parameterMap = new HashMap<String, String>();
    private HeaderMap headers = new HeaderMap();
    private final Application application;
    private List<FileItem> fileItems;


    public RequestImpl(HttpExchange exchange,Application application) throws IOException, FileUploadException {
        this.exchange = exchange;
        this.servletContext = application.getServletContext();
        this.application = application;
        Headers requestHeader = exchange.getRequestHeaders();

        String cookie = requestHeader.getFirst("Cookie");
        if(cookie != null && cookie.isEmpty()){
            List<Cookie> cookieList = Cookie.parse(cookie);
            for(Cookie c : cookieList){
                cookieMap.put(c.getName(),c);
            }
        }

        for(Map.Entry<String,List<String>> entry : requestHeader.entrySet()){
            headers.put(entry.getKey(),entry.getValue());
        }

        String encoding = getCharacterEncoding();
        int contentLength = getContentLength();

        String query = getRequestURI().getRawQuery();
        if(query != null && !query.isEmpty()){
            String[] querys = query.split("&");
            for(String q : querys){
                int i = q.indexOf('=');
                if(i > 0){
                    parameterMap.put(URLDecoder.decode(q.substring(0,i),encoding),URLDecoder.decode(q.substring(i+1),encoding));
                }else{
                    parameterMap.put(URLDecoder.decode(q,encoding),"");
                }
            }
        }

        RequestContext requestContext = new RequestUploadContext(this);
        if(FileUploadBase.isMultipartContent(requestContext)){
            this.in = exchange.getRequestBody();
            ServerFileUpload fileUpload = new ServerFileUpload(requestContext);
            fileUpload.setFileItemFactory(new DiskFileItemFactory());
            List<FileItem> fileItems = fileUpload.parseRequest();
            this.fileItems = fileItems;
            for(FileItem fileItem : fileItems){
                if(fileItem.isFormField()){
                    parameterMap.put(fileItem.getFieldName(),fileItem.getString(encoding));
                }
            }
        }else{
            this.in = new StreamReuse(exchange.getRequestBody());
            if(contentLength > 0){
                this.in.mark(contentLength);
            }else{
                this.in.mark(Integer.MAX_VALUE);
            }
            String res = IOUtils.toString(this.in,encoding);
            if(res != null && !res.isEmpty()){
                String[] querys = res.split("&");
                for(String q : querys){
                    int i = q.indexOf('=');
                    if(i > 0){
                        parameterMap.put(URLDecoder.decode(q.substring(0,i),encoding),URLDecoder.decode(q.substring(i+1),encoding));
                    }else{
                        parameterMap.put(URLDecoder.decode(q,encoding),"");
                    }
                }
            }
            this.in.reset();
        }


    }

    @Override
    public URI getRequestURI() {
        return exchange.getRequestURI();
    }

    @Override
    public String getMethod() {
        return exchange.getRequestMethod();
    }

    @Override
    public InputStream getInputStream() {
        return this.in;
    }

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
    public String getCharacterEncoding() {
        String header = getFirstHeader("Content-Type");
        if(header == null || header.isEmpty()){
            return application.getEncoding();
        }
        int i = header.indexOf("charset=");
        if(i < 0 || i + "charset=".length() >= header.length()){
            return application.getEncoding();
        }

        int j = header.indexOf(";",i + "charset=".length());
        if(j < 0){
            return header.substring(i + "charset=".length());
        }
        return header.substring(i + "charset=".length(),j);
    }


    @Override
    public String getParameter(String key) {
        return parameterMap.get(key);
    }

    @Override
    public Set<Map.Entry<String, String>> parameterSet() {
        return parameterMap.entrySet();
    }

    @Override
    public Set<Map.Entry<String, Object>> attrSet() {
        return map.entrySet();
    }

    @Override
    public Session getSession() {
       return getSession(true);
    }

    @Override
    public Session getSession(boolean create) {
        Cookie cookie  = getCookie(application.getSessionCookieName());
        if(cookie == null){
            if(create){
                String setSessionId = UUID.randomUUID().toString();
                return application.getSessionManager().getSession(setSessionId);
            }
            return null;
        }else{
            return application.getSessionManager().getSession(cookie.getValue());
        }
    }

    @Override
    public Collection<Cookie> getCookies() {
        return Collections.unmodifiableCollection(cookieMap.values());
    }

    @Override
    public Cookie getCookie(String name) {
        return cookieMap.get(name);
    }

    @Override
    public String getFirstHeader(String name) {
        return headers.getFirst(name);
    }

    @Override
    public List<String> getHeaders(String name) {
        return headers.get(name);
    }

    @Override
    public Set<Map.Entry<String,List<String>>> headerSet() {
        return headers.entrySet();
    }

    private Exception error;


    @Override
    public Exception getError() {
        return error;
    }

    public void setError(Exception error) {
        this.error = error;
    }

    @Override
    public int getContentLength() {
        String strLength = getFirstHeader("Content-length");
        if(strLength == null || strLength.isEmpty()){
            return 0;
        }
        return Integer.parseInt(strLength);
    }

    public String getContentType(){
        return getFirstHeader("Content-type");
    }



    private final ServletContext servletContext;

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }


    @Override
    public String getBasePath() {
        return application.getBasePath();
    }

    @Override
    public String getContextPath() {
        String context =  application.getContext();
        if(context.startsWith("/")){
            return context;
        }
        return  "/" + context;
    }

    @Override
    public List<FileItem> getFileItems() {
        return fileItems;
    }

    @Override
    public Map<String, Object> getDataModel() {
        Map<String,Object> map = new HashMap<String,Object>();
        map.putAll(parameterMap);
        map.putAll(this.map);
        map.put("basePath",getBasePath());
        map.put("contextPath",getContextPath());
        map.put("parameterScope",Collections.unmodifiableMap(parameterMap));
        map.put("attrScope",Collections.unmodifiableMap(this.map));
        map.put("request",this);
        map.put("servletContext",servletContext);
        Session session = getSession(false);
        if(session != null){
            map.put("sessionScope",session.getDataModel());
        }else{
            map.put("sessionScope",Collections.emptyMap());
        }
        return Collections.unmodifiableMap(map);
    }
}
