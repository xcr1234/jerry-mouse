package com.jerry.mouse.server;

import com.jerry.mouse.util.StringUtil;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.commons.io.IOUtils;

import java.io.IOException;

public class DefaultServerHandler implements HttpHandler {

    private Server server;


    public DefaultServerHandler(Server server) {
        this.server = server;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try{
            String path = httpExchange.getRequestURI().getPath();

            Headers headers = httpExchange.getResponseHeaders();
            headers.set("Content-Type","text/html;charset=" + server.getEncoding());
            headers.set("Server","jerry-mouse");
            headers.set("Connection","keep-alive");
            headers.set("Keep-Alive","timeout=20");
            headers.set("Date", StringUtil.getGMTDate());
            if("/".equals(path)){
                httpExchange.sendResponseHeaders(200,0);
                StringBuilder sb = new StringBuilder("<h1> Available Contexts: </h1>");
                for(Application application : server.getApplications()){
                    sb.append(String.format("<a href=\"%s\">%s - %s</a>",application.getContext(),application.getContext(),application.getMainClass().getName()));
                }
                IOUtils.write(sb.toString(),httpExchange.getResponseBody(),server.getEncoding());
            }else{
                httpExchange.sendResponseHeaders(404,0);
                IOUtils.write("<h1>404 Not Found</h1>No context found for request",httpExchange.getResponseBody(),server.getEncoding());
            }
        }finally {
            httpExchange.close();
        }

    }
}
