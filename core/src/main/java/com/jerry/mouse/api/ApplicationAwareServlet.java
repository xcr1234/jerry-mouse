package com.jerry.mouse.api;

import com.jerry.mouse.core.Application;

/**
 * 在ApplicationAwareServlet中可以拿到全局的Application对象，setApplication方法可以用作Servlet的初始化（load-on-startup）
 */
public interface ApplicationAwareServlet extends Servlet{
    void setApplication(Application application);
}
