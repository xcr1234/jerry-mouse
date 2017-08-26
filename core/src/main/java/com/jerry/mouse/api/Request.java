package com.jerry.mouse.api;
import com.jerry.mouse.util.upload.FileItem;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 相当于HttpServletRequest，代表客户端的请求。
 */
public interface Request {


    String getMethod();

    String getContentType();

    InputStream getInputStream();


    Object getAttr(String key);

    Object putAttr(String key, Object value);

    Object removeAttr(String key);

    String getCharacterEncoding();

    String getParameter(String key);

    Set<Map.Entry<String,String>> parameterSet();

    Set<Map.Entry<String,Object>> attrSet();

    Session getSession();

    Session getSession(boolean create);

    Collection<Cookie> getCookies();

    Cookie getCookie(String name);

    String getFirstHeader(String name);

    List<String> getHeaders(String name);

    Set<Map.Entry<String,List<String>>> headerSet();

    /**
     * 获取error servlet中的异常
     * @return 只有在映射为/error的servlet中会返回值，其他都返回null.
     */
    Exception getError();

    int getContentLength();

    ServletContext getServletContext();

    URI getRequestURI();

    String getBasePath();

    /**
     * 在上传文件时，返回文件列表，表单必须是multipart/form-data，否则返回null。
     * 例子：
     * List<FileItem> items = request.getFileItems():
     * for(Item item:items){
     *     if(item.isFormField()){
     *         //表单数据
     *         String value = item.getString();
     *     }   else{
     *         //文件
     *         byte[] bytes = item.get();
     *     }
     * }
     * @return 文件列表，非上传文件时返回null。
     */
    List<FileItem> getFileItems();

    Map<String,Object> getDataModel();

}
