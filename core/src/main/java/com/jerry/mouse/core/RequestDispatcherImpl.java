package com.jerry.mouse.core;

import com.jerry.mouse.api.Cookie;
import com.jerry.mouse.api.Request;
import com.jerry.mouse.api.RequestDispatcher;
import com.jerry.mouse.api.Response;
import com.jerry.mouse.server.Application;
import com.jerry.mouse.server.ApplicationServerHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Date;

public class RequestDispatcherImpl implements RequestDispatcher {



    private String path;
    private ApplicationServerHandler handler;


    public RequestDispatcherImpl(String path, Application application) {
        this.path = path;
        this.handler = application.getHandler();
    }

    @Override
    public void forward(Request request, Response response) throws IOException {
        this.handler.handlePath(path, (RequestImpl) request,new DispatchResponse((ResponseImpl) response),request.getExchange());
    }

    @Override
    public void include(Request request, Response response) throws IOException {
        this.handler.handlePath(path, (RequestImpl) request,new IncludeResponse((ResponseImpl) response),request.getExchange());
    }


    private static class DispatchResponse extends ResponseImpl{
        DispatchResponse(ResponseImpl response) {
            super(response);
        }

        @Override
        public void writeTo(HttpExchange exchange) throws IOException {

        }

        @Override
        public void close() {

        }
    }

    private static class IncludeResponse extends DispatchResponse{

        IncludeResponse(ResponseImpl response) {
            super(response);
        }

        @Override
        public void addHeader(String name, String value) {

        }

        @Override
        public void addDateHeader(String name, Date date) {

        }

        @Override
        public void setStatus(int status) throws IOException {

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
