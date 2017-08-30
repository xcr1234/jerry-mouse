package com.jerry.mouse.server;

import com.jerry.mouse.Config;

import java.net.InetSocketAddress;
import java.util.Collection;

public interface Server extends Config.Server{

    Collection<Application> getApplications();

    InetSocketAddress getAddress();

    String getPath();

    String getEncoding();

    void addApplication(Application application);

    void start() throws Exception;

}
