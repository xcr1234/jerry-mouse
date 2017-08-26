package com.jerry.mouse.startup;

import com.jerry.mouse.core.Application;

public interface ApplicationStartListener {
    void onError(Throwable t);

    void onComplete(Application application);
}
