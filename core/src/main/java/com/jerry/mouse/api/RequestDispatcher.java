package com.jerry.mouse.api;

import java.io.IOException;

public interface RequestDispatcher {
    void forward(Response response) throws IOException;

    void include(Response response) throws IOException;
}
