package com.jerry.mouse.api;


import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Session {

    String getId();

    Object getAttr(String key);

    Object putAttr(String key, Object value);

    Object removeAttr(String key);

    Set<Map.Entry<String,Object>> attrSet();

    Map<String,Object> getDataModel();


}
