package com.jerry.mouse.core;

import com.jerry.mouse.api.Request;
import com.jerry.mouse.util.upload.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ServerFileUpload extends FileUpload {


    private RequestContext context;


    public ServerFileUpload(RequestContext context){
        super();
        this.context = context;
    }

    public List<FileItem> parseRequest() throws FileUploadException {
        return parseRequest(this.context);
    }

    public Map<String, List<FileItem>> parseParameterMap()
            throws FileUploadException{
        return parseParameterMap(this.context);
    }

    public FileItemIterator getItemIterator()  throws FileUploadException, IOException {
        return super.getItemIterator(this.context);
    }


}
