package com.jerry.mouse.core;

import com.jerry.mouse.api.ApplicationAwareServlet;
import com.jerry.mouse.api.Request;
import com.jerry.mouse.api.Response;
import com.jerry.mouse.api.Servlet;
import com.jerry.mouse.util.ContentType;
import com.jerry.mouse.util.StringUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Date;
import java.util.List;

public class DefaultServlet implements ApplicationAwareServlet {

    protected boolean handleWelcomeFile(String path,Request request, Response response) throws Exception {
        List<String> welComeFiles = application.getWelComeFiles();
        for(String welComeFile : welComeFiles){
            Servlet servlet = application.getServlet(path + welComeFile);
            if(servlet != null){
                servlet.service(request,response);
                return true;
            }
            if(handleStaticResource(path + welComeFile,request,response)){
                return true;
            }
        }
        return false;

    }


    protected boolean handleDirView(File file,String path,Request request,Response response) throws IOException{
        return false;
    }



    protected boolean handleStaticResource(String path,Request request,Response response) throws Exception {
        URL url = application.getStaticManage().getResource(path);
        if(url == null){
            return false;
        }

        File file = new File(url.getFile());
        String extension = FilenameUtils.getExtension(file.getName());
        String contentType = ContentType.findMimeByExtension(extension);
        System.out.println(file);
        if(!file.exists()){
            return false;
        }
        if(file.isDirectory()){
            if(handleWelcomeFile(path,request,response)){
                return true;
            }
            if(application.isDirView()){
                return handleDirView(file,path,request,response);
            }
        }
        response.setContentType(contentType);

        if(application.getExtensionsSet().contains(extension)){
            long modify = file.lastModified();
            String header = request.getFirstHeader("If-Modified-Since");
            Date date = StringUtil.parseGMT(header);
            if(date != null && modify - date.getTime() < 1000){
                response.setStatus(304);
            }else{
                response.addDateHeader("Expires",new Date(System.currentTimeMillis() + application.getStaticManage().getExpires() * 1000));
                response.addDateHeader("Last-Modified",new Date(modify));
                InputStream in = url.openStream();
                try{
                    IOUtils.copy(in,response.getOutputStream());
                }finally {
                    IOUtils.closeQuietly(in);
                }
            }
        }else{
            InputStream in = url.openStream();
            try{
                IOUtils.copy(in,response.getOutputStream());
            }finally {
                IOUtils.closeQuietly(in);
            }
        }
        return true;
    }

    protected void handleNotFound(Request request,Response response) {
        response.dispatch("/404");
    }


    @Override
    public void service(Request request, Response response) throws Exception {

        String context = application.getContext();
        String path =request. getRequestURI().getPath();
        if( path.startsWith(context)){
            int i = path.indexOf(context);
            path = path.substring(i + context.length());
        }

        if("".equals(path) || "/".equals(path)){
            if(handleWelcomeFile("/",request,response)){
                return;
            }
        }else if(!"/".equals(path)){
            if(handleStaticResource(path,request,response)){
                return;
            }
        }

        handleNotFound(request,response);
    }




    protected Application application;

    public void setApplication(Application application){
        this.application = application;
    }
}
