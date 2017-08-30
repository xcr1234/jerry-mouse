package com.jerry.mouse.api;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public interface Response {

    Object getAttr(String key);

    Object putAttr(String key, Object value);

    Object removeAttr(String key);

    Set<Map.Entry<String,Object>> attrSet();

    void addHeader(String name, String value);

    void addDateHeader(String name, Date date);

    PrintWriter getWriter() throws IOException;

    void setStatus(int code) throws IOException;

    OutputStream getOutputStream();

    void setCharacterEncoding(String encoding);

    void setContentType(String contentType);

    void addCookie(Cookie cookie);

    void sendRedirect(String target) throws IOException;

    ServletContext getServletContext();

}
