package com.jerry.mouse.core;

import com.jerry.mouse.api.*;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public class RequestDispatcherImpl implements RequestDispatcher{

    private String path;
    private RequestImpl request;

    private HttpExchange exchange;
    private WebServerHandler handler;

    public RequestDispatcherImpl(String path, RequestImpl request,  HttpExchange exchange, WebServerHandler handler) {
        this.path = path;
        this.request = request;
        this.exchange = exchange;
        this.handler = handler;
    }

    @Override
    public void forward(Response response) throws IOException {
        handler.handle(path,request, (ResponseImpl) response,exchange,true);
    }

    @Override
    public void include(Response response) throws IOException {
        handler.handle(path,request,new IncludeResponseImpl((ResponseImpl) response),exchange,true);
    }

    private static class IncludeResponseImpl extends ResponseImpl implements Response{


        private ResponseImpl response;

        public IncludeResponseImpl(ResponseImpl response) {
            super(response);
        }

        @Override
        public Object getAttr(String key) {
            return response.getAttr(key);
        }

        @Override
        public Object putAttr(String key, Object value) {
            return response.putAttr(key,value);
        }

        @Override
        public Object removeAttr(String key) {
            return response.removeAttr(key);
        }

        @Override
        public Set<Map.Entry<String, Object>> attrSet() {
            return response.attrSet();
        }

        @Override
        public void addHeader(String name, String value) {

        }

        @Override
        public void addDateHeader(String name, Date date) {

        }

        @Override
        public PrintWriter getWriter() throws IOException {
            return response.getWriter();
        }

        @Override
        public void setStatus(int code) throws IOException {

        }


        @Override
        public void setCharacterEncoding(String encoding) {

        }

        @Override
        public void setContentType(String contentType) {

        }

        @Override
        public void addCookie(Cookie cookie) {

        }

        @Override
        public void sendRedirect(String target) throws IOException {

        }


    }
}
