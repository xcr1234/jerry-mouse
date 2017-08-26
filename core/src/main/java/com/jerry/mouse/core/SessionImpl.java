package com.jerry.mouse.core;

import com.jerry.mouse.api.Session;


import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SessionImpl implements Session {

    private String id;
    private Map<String,Object> map = new ConcurrentHashMap<String, Object>();
    private static Map<String,Session> sessionMap = new ConcurrentHashMap<String, Session>();

    synchronized static Session getSession(String id){
        Session session = sessionMap.get(id);
        if(session == null){
            session = new SessionImpl(id);
            sessionMap.put(id,session);
        }
        return session;
    }

    private SessionImpl(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Object getAttr(String key) {
        return map.get(key);
    }

    @Override
    public Object putAttr(String key, Object value) {
        return map.put(key,value);
    }

    @Override
    public Object removeAttr(String key) {
        return map.remove(key);
    }

    @Override
    public Set<Map.Entry<String, Object>> attrSet() {
        return map.entrySet();
    }

    @Override
    public Map<String, Object> getDataModel() {
        return Collections.unmodifiableMap(this.map);
    }
}
