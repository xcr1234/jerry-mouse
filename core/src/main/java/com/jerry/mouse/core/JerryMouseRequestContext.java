package com.jerry.mouse.core;

import com.jerry.mouse.api.Request;
import com.jerry.mouse.util.upload.RequestContext;


import java.io.InputStream;

public class JerryMouseRequestContext implements RequestContext {

    private Request request;

    public JerryMouseRequestContext(Request request) {
        this.request = request;
    }

    @Override
    public String getCharacterEncoding() {
        return request.getCharacterEncoding();
    }

    @Override
    public String getContentType() {
        return request.getContentType();
    }

    @Override
    public int getContentLength() {
        return request.getContentLength();
    }

    @Override
    public InputStream getInputStream(){
        return request.getInputStream();
    }
}
