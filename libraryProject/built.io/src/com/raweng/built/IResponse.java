package com.raweng.built;

import java.io.InputStream;

import org.apache.http.Header;

/**
 *
 * @author raw engineering, Inc
 */
public interface IResponse {

    public void parseData(boolean treatDuplicateKeysAsArrayItems) throws Exception;

    public int getStatusCode();

    public void setStatusCode(int statusCode);

    public InputStream getInputStream();

    public void setInputStream(InputStream inputStream);

    public String getContentType();

    public void setContentType(String contentType);

    public Object getRootObject();

    public void setRootObject(Object rootObject);
    
    public void setResponseHeader(Header[] responseHeader);

    public Header[] getResponseHeader();
    
    public String getStringInputStream();

    public void setStringInputStream(String inputStream);
}

