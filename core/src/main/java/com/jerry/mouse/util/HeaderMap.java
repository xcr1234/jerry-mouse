package com.jerry.mouse.util;


import java.util.LinkedList;
import java.util.List;

public class HeaderMap extends CaseInsensitiveMap<List<String>> {


    public void put(String key,String value){
        List<String> list = get(key);
        if(list == null){
            list = new LinkedList<String>();
            super.put(key,list);
        }
        list.add(value);
    }

    public String getFirst(String key){
        List<String> list = super.get(key);
        if(list == null){
            return null;
        }
        if(list instanceof LinkedList){
            return ((LinkedList<String>) list).peekFirst();
        }
        return list.get(0);
    }



}
