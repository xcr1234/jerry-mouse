package com.jerry.mouse.api;

import com.jerry.mouse.util.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

/**
 * 标准Http Cookie
 */
public class Cookie implements Serializable{


    private static final long serialVersionUID = 1342204034404928345L;
    private String name;
    private String value;
    private String comment;
    private String path;
    private String domain;
    private Integer maxAge;
    private Boolean secure;
    private Boolean httpOnly;


    public Cookie(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
    }


    public Boolean getSecure() {
        return secure;
    }

    public void setSecure(Boolean secure) {
        this.secure = secure;
    }

    public boolean isHttpOnly() {
        return httpOnly;
    }

    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append('=').append(value);
        if(path != null){
            sb.append("; path=").append(path);
        }
        if(comment != null){
            sb.append("; comment=").append(comment);
        }
        if(domain != null){
            sb.append("; domain=").append(domain);
        }
        if(maxAge != null){
            sb.append("; expires=").append(StringUtil.getGMTDate(new Date(System.currentTimeMillis() + maxAge * 1000)));
        }
        if(secure != null && secure){
            sb.append("; secure");
        }
        if(httpOnly != null && httpOnly){
            sb.append("; HttpOnly");
        }
        return sb.toString();
    }

    public static List<Cookie> parse(String requestHeader){
        List<Cookie> list = new ArrayList<Cookie>();
        StringTokenizer tokenizer = new StringTokenizer(requestHeader, ";");
        while (tokenizer.hasMoreTokens()){
            String namevaluePair = tokenizer.nextToken();
            int index = namevaluePair.indexOf('=');
            String name, value;
            if (index != -1) {
                name = namevaluePair.substring(0, index).trim();
                value = namevaluePair.substring(index + 1).trim();
            } else {
                name = namevaluePair.trim();
                value = null;
            }
            list.add(new Cookie(name,value));
        }
        return list;
    }

    public static List<String> toResponse(List<Cookie> cookies){
        List<String> list = new ArrayList<String>();
        for(Cookie cookie :cookies){
            list.add(cookie.toString());
        }
        return list;
    }

}
