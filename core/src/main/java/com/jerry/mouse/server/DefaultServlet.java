package com.jerry.mouse.server;

import com.jerry.mouse.api.ApplicationAwareServlet;
import com.jerry.mouse.api.Request;
import com.jerry.mouse.api.Response;
import com.jerry.mouse.api.Servlet;
import com.jerry.mouse.util.ContentType;
import com.jerry.mouse.util.StringUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URL;
import java.util.Date;
import java.util.List;

/**
 * 默认的Servlet处理器，当一个url请求找不到对应的Servlet时，就会重定向到DefaultServlet，DefaultServlet的url-mapping始终是/default。
 * DefaultServlet必须支持的几个功能：
 * 处理欢迎页，{@link DefaultServlet#handleWelcomeFile(String, Request, Response)}，如果配置了{@link com.jerry.mouse.api.WelComeFiles}注解。
 * 处理目录访问，如果{@link com.jerry.mouse.Config.Application#DIR_VIEW}为true。
 * 处理静态资源，{@link DefaultServlet#handleStaticResource(String, Request, Response)}，如果配置了{@link com.jerry.mouse.api.StaticResource}注解
 * 处理404页面，{@link DefaultServlet#handleNotFound(Request, Response)}
 *
 * 用户可以创建子类，继承DefaultServlet类，覆盖上面几个方法，来实现自己的处理过程。记得将自定义的Servlet映射为/default
 * 例子：
 * @  WebServlet("/default")
 * class MyDefaultServlet extends DefaultServlet{
 *
 *
 * }
 */
public class DefaultServlet implements ApplicationAwareServlet {

    protected boolean handleWelcomeFile(String path,Request request, Response response) throws Exception {
        List<String> welComeFiles = application.welcomeFiles();
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
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Directory Listing For ["+ path +"]</title>");
        out.println("<STYLE><!--h1 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:22px;} h2 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:16px;} h3 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:14px;} body {font-family:Tahoma,Arial,sans-serif;color:black;background-color:white;} b {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;} p {font-family:Tahoma,Arial,sans-serif;background:white;color:black;font-size:12px;} a {color:black;} a.name {color:black;} .line {height:1px;background-color:#525D76;border:none;}--></STYLE> </head>");
        out.println("<body><h1>Directory Listing For ["+path+"]</h1><HR size=\"1\" noshade=\"noshade\"><table width=\"100%\" cellspacing=\"0\" cellpadding=\"5\" align=\"center\">");
        out.println("<tr>");
        out.println("<td align=\"left\"><font size=\"+1\"><strong>Filename</strong></font></td>");
        out.println("<td align=\"center\"><font size=\"+1\"><strong>Size</strong></font></td>");
        out.println("<td align=\"right\"><font size=\"+1\"><strong>Last Modified</strong></font></td>");
        out.println("</tr>");
        File files [] = file.listFiles();
        if(files != null){
            for(int i=0;i<files.length;i++){
                File f =files[i];
                out.println(i % 2 == 1 ? "<tr bgcolor=\"#eeeeee\">" :"<tr>");
                out.println("<td align=\"left\">&nbsp;&nbsp;");
                out.println("<a href=\"" + path(request,path,f) + "\"><tt>" + (f.isDirectory() ? f.getName() + "/" : f.getName()) + "</tt></a></td>");
                out.println("<td align=\"right\"><tt>" + (f.isDirectory() ? "&nbsp;" :StringUtil.formatSize(f.length())) + "</tt></td>");
                out.println("<td align=\"right\"><tt>" + StringUtil.getGMTDate(f.lastModified()) + "</tt></td>");
                out.println("</tr>");
            }
        }
        out.println("</table>");
        out.println("<HR size=\"1\" noshade=\"noshade\"><h3>Jerry Mouse Web Server</h3></body>");
        out.println("</html>");
        return true;
    }

    private static String path(Request request ,String path,File f){
        StringBuilder sb = new StringBuilder();
        sb.append(request.getContextPath());
        if (!path.startsWith("/")) {
            sb.append('/');
        }
        sb.append(path);
        if(!path.endsWith("/")){
            sb.append('/');
        }
        sb.append(f.getName());
        return sb.toString();
    }





    protected boolean handleStaticResource(String path,Request request,Response response) throws Exception {
        URL url = application.getStaticManager().getResource(path);
        if(url == null){
            return false;
        }

        File file = new File(url.getFile());
        String extension = FilenameUtils.getExtension(file.getName());
        String contentType = ContentType.findMimeByExtension(extension);
        if(!file.exists()){
            return false;
        }
        if(file.isDirectory()){
            if(application.viewDir()){
                return handleDirView(file,path,request,response);
            }
            if(handleWelcomeFile(path,request,response)){
                return true;
            }

        }
        response.setContentType(contentType);

        if(application.cacheExtensions().contains(extension)){
            long modify = file.lastModified();
            String header = request.getFirstHeader("If-Modified-Since");
            Date date = StringUtil.parseGMT(header);
            if(date != null && modify - date.getTime() < 1000){
                response.setStatus(304);
            }else{
                response.addDateHeader("Expires",new Date(System.currentTimeMillis() + application.getStaticManager().getExpires() * 1000));
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

    protected void handleNotFound(Request request,Response response) throws Exception {
        Servlet servlet = application.getNotFoundServlet();
        servlet.service(request,response);
    }


    @Override
    public final void service(Request request, Response response) throws Exception {

        String context = application.getContext();
        String path = request. getRequestURI().getPath();
        if(path.startsWith(context)){

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


    @Override
    public void setApplication(Application application) {
        this.application = application;
    }
}
