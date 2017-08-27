package com.jerry.mouse.startup;

import com.jerry.mouse.core.Application;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DefaultApplicationStartListener implements ApplicationStartListener {
    private final long time = System.currentTimeMillis();
    private static Log log = LogFactory.getLog(JerryMouseApplication.class);
    @Override
    public void onError(Throwable t) {
        log.error(t.getLocalizedMessage(),t);
    }

    @Override
    public void onComplete(Application application) {
        log.info("application start up successfully in " + (System.currentTimeMillis() - time) + " ms.");
    }
}
