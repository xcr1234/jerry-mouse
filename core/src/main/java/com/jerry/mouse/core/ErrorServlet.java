package com.jerry.mouse.core;

import com.jerry.mouse.api.Request;
import com.jerry.mouse.api.Response;
import com.jerry.mouse.api.Servlet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

class ErrorServlet implements Servlet {

    private static Log log = LogFactory.getLog(ErrorServlet.class);

    @Override
    public void service(Request request, Response response) throws IOException {
        try{
            response.setContentType("text/html");
            response.setStatus(500);

            Exception throwable = request.getError();
            if(request.getError() == null){
                throwable = new Exception("hello,world!");
            }else{
                Servlet servlet = request.getErrorServlet();
                if(servlet == null){
                    log.error("[/error] : an error occurred in servlet action.",throwable);
                }else{
                    log.error("[/error] : an error occurred in servlet :" + servlet,throwable);
                }

            }


            Throwable rootCause = throwable;
            while (rootCause.getCause()!=null){
                rootCause = rootCause.getCause();
            }

            PrintWriter out = response.getWriter();
            out.print("<!DOCTYPE html><html><head><title>Jerry Mouse WebServer - Error report</title><style type=\"text/css\">H1 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:22px;} H2 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:16px;} H3 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:14px;} BODY {font-family:Tahoma,Arial,sans-serif;color:black;background-color:white;} B {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;} P {font-family:Tahoma,Arial,sans-serif;background:white;color:black;font-size:12px;}A {color : black;}A.name {color : black;}.line {height: 1px; background-color: #525D76; border: none;}</style> </head><body><h1>HTTP Status 500 - ");
            out.print(throwable.toString());
            out.print("</h1><div class=\"line\"></div><p><b>type</b> Exception report</p><p><b>message</b> <u>");
            out.print(throwable.toString());
            out.print("</u></p><p><b>description</b> <u>");
            out.print(throwable.getLocalizedMessage());
            out.print(".</u></p><p><b>exception</b></p><pre>");
            throwable.printStackTrace(out);
            out.print("</pre><p><b>root cause</b></p><pre>");
            rootCause.printStackTrace(out);
            out.print("</pre><hr class=\"line\"><h3>Servlet Exception - ");
            out.print(date());
            out.print("</h3></body></html>");
        }catch (Exception e){
            log.error("an error occurred in [/error] ErrorServlet",e);
        }
    }


    private String date(){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }
}
