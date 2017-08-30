package com.jerry.mouse.server;

import com.jerry.mouse.api.Request;
import com.jerry.mouse.api.Response;
import com.jerry.mouse.api.Servlet;

import java.io.IOException;
import java.io.PrintWriter;

public class NotFoundServlet implements Servlet {
    @Override
    public void service(Request request, Response response) throws IOException {
        response.setContentType("text/html");
        response.setStatus(404);
        PrintWriter out = response.getWriter();
        out.println("<!Doctype html>");
        out.println("<html xmlns=http://www.w3.org/1999/xhtml>");
        out.println("<head>");
        out.println("<title>Jerry Mouse Web Server</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>404 Not Found.</h1>");
        out.println("</body>");
        out.println("</html>");;
    }
}
