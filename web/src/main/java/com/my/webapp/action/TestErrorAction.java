package com.my.webapp.action;

import com.jerry.mouse.api.Request;
import com.jerry.mouse.api.Response;
import com.jerry.mouse.api.Servlet;
import com.jerry.mouse.api.WebServlet;

import java.io.IOException;

@WebServlet("/error.do")
public class TestErrorAction implements Servlet {
    @Override
    public void service(Request request, Response response) throws Exception {
        throw new RuntimeException("test error",new IOException("failed"));
    }

}
