package com.jerry.mouse.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpCookie;
import java.util.HashMap;
import java.util.Map;

/**
 * 配置文件类，用法跟java.util.Properties类似，但功能更强大
 * @author xiecr002
 *
 */
public class Properties extends HashMap<String, String>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1336545028588444285L;

	
	public Properties() {
		super();
	}
	
	public Properties(Map<? extends String,? extends String> another) {
		super(another);
	}
	
	public Properties(Properties properties) {
		super(properties);
	}

	public Properties(java.util.Properties properties){
        for(String name:properties.stringPropertyNames()) {
            put(name,properties.getProperty(name));
        }
    }

    public void put(String key,Object value){
		put(key,value.toString());
	}
	
	
	public void loadClassPath(String resource) throws IOException {
		if(resource == null) {
			throw new IllegalArgumentException();
		}
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
		if(in == null) {
			throw new FileNotFoundException("classpath:/" + resource);
		}
		try {
			java.util.Properties properties = new java.util.Properties();
			properties.load(in);
			for(String name:properties.stringPropertyNames()) {
				put(name,properties.getProperty(name));
			}
		}finally {
			try {
				in.close();
			}catch(IOException e) {}
		}
	}
	
	
	public Integer getInteger(String name) {
		return Integer.valueOf(get(name));
	}
	
	public Boolean getBool(String name) {
		return Boolean.valueOf(get(name));
	}
	

	
	
	
	

}
