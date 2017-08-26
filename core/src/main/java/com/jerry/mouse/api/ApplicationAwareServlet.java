package com.jerry.mouse.api;

import com.jerry.mouse.core.Application;

public interface ApplicationAwareServlet extends Servlet{
    void setApplication(Application application);
}
