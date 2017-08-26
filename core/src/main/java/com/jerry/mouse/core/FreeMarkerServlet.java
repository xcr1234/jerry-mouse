package com.jerry.mouse.core;

import com.jerry.mouse.api.*;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Map;

public class FreeMarkerServlet implements ApplicationAwareServlet {


    private Configuration configuration;
    private String base;
    private String encoding;

    @Override
    public void service(Request request, Response response) throws Exception {
        String template = (String) request.getAttr("template");
        String contentType = (String) request.getAttr("contentType");
        String enc = (String) request.getAttr("encoding");
        if(template == null || template.isEmpty()){
            response.dispatch("/404");
            return;
        }
        if(!template.toLowerCase().endsWith(".ftl")){
            template = template + ".ftl";
        }
        if(contentType != null && !contentType.isEmpty()){
            response.setContentType(contentType);
        }else{
            response.setContentType("text/html");
        }
        if(enc != null && !enc.isEmpty()){
            response.setCharacterEncoding(enc);
        }else{
            response.setCharacterEncoding(encoding);
        }
        Map<String, Object> model = request.getDataModel();
        Template t = configuration.getTemplate(template);
        PrintWriter writer = response.getWriter();
        t.process(model,writer);
    }

    @Override
    public void setApplication(Application application) {
        FreeMarkerSupport freeMarkerSupport = application.getFreeMarkerSupport();
        configuration = new Configuration(Configuration.VERSION_2_3_26);
        this.encoding = freeMarkerSupport.encoding().isEmpty() ? application.getEncoding() : freeMarkerSupport.encoding();
        configuration.setDefaultEncoding(this.encoding);
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER );
        configuration.setLogTemplateExceptions(false);
        this.base = freeMarkerSupport.base();
        configuration.setTemplateLoader(new ClassTemplateLoader(getClass().getClassLoader(),this.base));

    }
}
