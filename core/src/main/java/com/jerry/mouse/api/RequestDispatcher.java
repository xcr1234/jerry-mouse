package com.jerry.mouse.api;


import java.io.IOException;

public interface RequestDispatcher {
    void forward(Request request,Response response) throws IOException;

    void include(Request request,Response response) throws IOException;
}
