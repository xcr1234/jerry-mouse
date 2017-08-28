package com.jerry.mouse.core;

import com.jerry.mouse.api.*;
import com.jerry.mouse.util.HeaderMap;
import com.jerry.mouse.util.StringUtil;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import org.apache.commons.io.output.ByteArrayOutputStream;
import java.io.*;

import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ResponseImpl implements Response {


    private Map<String, Object> map = new ConcurrentHashMap<String, Object>();
    private List<Cookie> cookieList = new Vector<Cookie>();
    private HeaderMap headers = new HeaderMap();
    private final ByteArrayOutputStream out;
    private final ServletContext servletContext;
    private final Application application;
    private final Request request;
    private PrintWriter writer;
    private String encoding;
    private String contentType;
    private Integer status;


    public ResponseImpl(Request request, Application application) {
        this.servletContext = application.getServletContext();
        this.application = application;
        this.request = request;
        this.out = new ByteArrayOutputStream();
    }

    protected ResponseImpl(ResponseImpl response){
        this.out = response.out;
        this.servletContext = response.servletContext;
        this.application = response.application;
        this.request = response.request;
    }

    @Override
    public Object getAttr(String key) {
        return map.get(key);
    }

    @Override
    public Object putAttr(String key, Object value) {
        return map.put(key, value);
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
    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    @Override
    public void addDateHeader(String name, Date date) {
        headers.put(name, StringUtil.getGMTDate(date));
    }

    @Override
    public synchronized PrintWriter getWriter() throws IOException {
        String encoding = this.encoding == null ? application.getEncoding() : this.encoding;
        if (writer == null) {
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(out,encoding)), true);
        }
        return writer;
    }

    @Override
    public void setStatus(int status) throws IOException {
        this.status = status;
    }

    @Override
    public OutputStream getOutputStream() {
        return out;
    }

    @Override
    public void setCharacterEncoding(String encoding) {
        this.encoding = encoding;
    }

    @Override
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public void addCookie(Cookie cookie) {
        cookieList.add(cookie);
    }

    @Override
    public void sendRedirect(String target) throws IOException {
        setStatus(302);
        addHeader("Location", target);
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }


    void finish() throws IOException{

        if (encoding != null && contentType == null) {
            addHeader("Content-Type", "text/html;charset=" + encoding);
        } else if (encoding != null) {
            addHeader("Content-Type", contentType + ";charset=" + encoding);
        } else if (contentType != null) {
            addHeader("Content-Type", contentType + ";charset=" + application.getEncoding());
        }
        Session session = request.getSession(false);
        if (session != null) {
            String sessionId = session.getId();
            Cookie c = new Cookie(application.getSessionCookieName(), sessionId);
            c.setMaxAge(application.getSessionMaxAge());//session过期时间
            c.setHttpOnly(true);
            cookieList.add(c);
        }
        if (!cookieList.isEmpty()) {
            headers.put("Set-Cookie", Cookie.toResponse(cookieList));
        }
        addHeader("Server", "jerry-mouse");
        addHeader("Connection", "keep-alive");
        addHeader("Keep-Alive", "timeout=20");
        addHeader("Date", StringUtil.getGMTDate());

    }

    void writeTo(HttpExchange exchange) throws IOException {


        Headers headers = exchange.getResponseHeaders();
        for (Map.Entry<String, List<String>> entry : this.headers.entrySet()) {
            headers.put(entry.getKey(), entry.getValue());
        }


        if (status != null) {
            exchange.sendResponseHeaders(status, 0);
        } else {
            exchange.sendResponseHeaders(200, 0);
        }

        if (out.size() > 0) {
            exchange.getResponseBody().write(out.toByteArray());
        }


    }
}
