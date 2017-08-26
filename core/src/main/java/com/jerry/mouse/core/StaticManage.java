package com.jerry.mouse.core;

import java.io.InputStream;
import java.net.URL;

public class StaticManage {

    private String target;
    private String prefix;
    private int expires;

    public StaticManage(String target, String prefix) {
        if(target.endsWith("/")){
            target = target.substring(0,target.length() - 1);
        }

        this.target = target;
        this.prefix = prefix;
    }

    public int getExpires() {
        return expires;
    }

    public void setExpires(int expires) {
        this.expires = expires;
    }

    public URL getResource(String path){
        if(!path.startsWith(prefix)){
            return null;
        }
        return getClass().getClassLoader().getResource(target + path);
    }


}
