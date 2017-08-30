package com.jerry.mouse.startup;

import com.jerry.mouse.server.Server;

public interface ApplicationStartListener {
    void onError(Throwable t);

    void onComplete(Server application);
}
