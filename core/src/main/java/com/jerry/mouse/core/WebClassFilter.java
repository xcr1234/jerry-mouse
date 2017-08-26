package com.jerry.mouse.core;



import com.jerry.mouse.api.WebServlet;
import com.jerry.mouse.util.ClassFilter;

class WebClassFilter implements ClassFilter {
    @Override
    public boolean accept(Class clazz) {
        return clazz.isAnnotationPresent(WebServlet.class);
    }
}
