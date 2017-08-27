package com.jerry.mouse.core;

import com.jerry.mouse.api.Session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultSessionManager implements SessionManager{


    private Map<String,Session> sessionMap = new ConcurrentHashMap<String, Session>();

    @Override
    public synchronized Session getSession(String id) {
        Session session = sessionMap.get(id);
        if(session == null){
            session = new SessionImpl(id);
            sessionMap.put(id,session);
        }
        return session;
    }
}
