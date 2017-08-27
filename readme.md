# Java实现自己的Servlet服务器

仓库地址：https://github.com/xcr1234/jerry-mouse

jar包：[jar包下载](https://coding.net/u/xcr_abcd/p/jerry-mouse/git/archive/master)
（依赖于[commons-logging](http://commons.apache.org/proper/commons-logging/)和[commons-io](http://commons.apache.org/proper/commons-io/)、[freemarker](http://freemarker.org/)三个包）



# 启动

完整例子见仓库里的web目录

```java
package com.my;

import com.jerry.mouse.util.Properties;
import com.jerry.mouse.startup.JerryMouseApplication;
import com.jerry.mouse.Config;

public class MyWebApp {

	public static void main(String[] args) throws Exception{
	
		Properties properties = new Properties();
	    properties.put(Config.Server.PORT,8080);
        properties.put(Config.Application.CONTEXT,"/app");
        properties.put(Config.Application.ENCODING,"UTF-8");     
                                   // 在应用级别设置编码格式，可有效防止中文乱码
        JerryMouseApplication.run(App.class,properties);    // 启动Application
   }      
   
}
```

启动成功后，在浏览器输入localhost:8080/app应该能正常看到404页面：

![404页面](http://img.blog.csdn.net/20170826223010220?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQveGNyNTMwNTUxNDI2/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)


# 静态资源访问

在MyWebApp类上加上`@StaticResource`注解即可支持静态资源访问，例如`@StaticResource(prefix = "/",target = "static")`，以后每次访问时，都会从classpath的static目录下寻找静态资源。

这时在class根目录下创建static文件夹，放入index.html，访问localhost:8080/app/index.html应该能正常访问

![这里写图片描述](http://img.blog.csdn.net/20170826225822593?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQveGNyNTMwNTUxNDI2/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

![这里写图片描述](http://img.blog.csdn.net/20170826225849630?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQveGNyNTMwNTUxNDI2/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

# 欢迎页面设置

在MyWebApp类上加上`@WelComeFiles({"index.htm","index.html"})`，当用户在浏览器中输入的URL不包含某个servlet名或页面时，welcome-file-list元素可指定显示的默认文件。例如访问localhost:8080/app/，则可以自动访问到localhost:8080/app/index.html

# 定义Servlet

在MyWebApp的子包下创建Servlet，框架会自动扫描到，方式是实现`Servlet`接口，然后加上`@WebSerlvet`注解

```java

@WebServlet("/hello")
public class HelloWorldAction implements Servlet {
    @Override
    public void service(Request request, Response response) throws Exception {        
        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
        out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
        out.println("<head>");
        out.println("<title>Hello,world</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>你好，It Works!</h1>");
        out.println("</body>");
        out.println("</html>");
    }
}
```

比java原生servlet方便的是，原生servlet只能抛出`IOException`和ServletException`，`这里可以抛出任何异常，统一处理


# FreeMarker支持

FreeMarker是一款模板引擎： 即一种基于模板和要改变的数据，   并用来生成输出文本（HTML网页、电子邮件、配置文件、源代码等）的通用工具。       它不是面向最终用户的，而是一个Java类库，是一款程序员可以嵌入他们所开发产品的组件。建议采用freemarker作模板引擎，参考http://freemarker.org，不推荐jsp！

1。在MyWebApp类上加上`@FreeMarkerSupport`注解，例如`@FreeMarkerSupport(mapping = "/freemarker",base = "template")`，`/freemarker`是将来用到的映射，`base = "template"`是模板文件在classpath下的相对路径。

![这里写图片描述](http://img.blog.csdn.net/20170826224505833?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQveGNyNTMwNTUxNDI2/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

2。

在要访问的那个Servlet中：
```java
request.putAttr("template","模板文件文件名");
response.dispatch("/freemarker");
```

重定向到`@FreeMarkerSupport`里面的mapping，会有`FreemarkerServlet`自动处理。

## 支持的属性

template			：	模板文件名
encoding		： 	文件
contentType	：	输出的文件类型

## freemarker取值

1. 可直接取到request.getParameter和request.getAttr中的值

2. session的值从sessionScope中取，如${sessionScope.user}

3. request.getParameter还可以从parameterScope中取，如${parameterScope.user}

4. request.getAttr还可以从attrScope中取

5. servletContext的取值${servletContext.user}

6. ${basePath}直接拿到basePath（jsp开发中经常用到）


## 异常处理

Servlet执行过程中的异常，统一重定向到[/error]去处理，可以通过request.getError()方法获得。

