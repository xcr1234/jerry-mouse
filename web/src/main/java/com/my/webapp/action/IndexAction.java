package com.my.webapp.action;

import com.jerry.mouse.api.Request;
import com.jerry.mouse.api.Response;
import com.jerry.mouse.api.Servlet;
import com.jerry.mouse.api.WebServlet;

@WebServlet("/index.html")
public class IndexAction implements Servlet {

    public IndexAction(){

    }
    
    @Override
    public void service(Request request, Response response) throws Exception {
        request.putAttr("template","index.ftl");
        request.getRequestDispatcher("/freemarker").forward(request,response);
    }
}
