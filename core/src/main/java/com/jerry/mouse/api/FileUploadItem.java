package com.jerry.mouse.api;

import java.io.InputStream;

public interface FileUploadItem {
    String getName();

    String getFileName();

    String getContentType();

    byte[] bytes();

    InputStream inputStream();

    int size();

    String getHeader(String name);
}
