package com.my.webapp;


import com.jerry.mouse.Config;
import com.jerry.mouse.JerryMouseApplication;
import com.jerry.mouse.api.FreeMarkerSupport;
import com.jerry.mouse.api.StaticResource;
import com.jerry.mouse.api.WelComeFiles;
import com.jerry.mouse.util.Properties;

import java.nio.charset.Charset;

@WelComeFiles({"index.htm","index.html"})
@FreeMarkerSupport(mapping = "/freemarker",base = "template")
@StaticResource(prefix = "/",target = "static")
public class App {

    public static void main(String[] args) {



        Properties properties = new Properties();
        properties.put(Config.Server.PORT,8080);
        properties.put(Config.Application.CONTEXT,"/app");
        properties.put(Config.Application.ENCODING,"UTF-8");    //在应用级别设置系统编码格式，防止中文乱码

        JerryMouseApplication.run(App.class,properties);

        JerryMouseApplication.start("http://localhost:8080/app");
    }

}
