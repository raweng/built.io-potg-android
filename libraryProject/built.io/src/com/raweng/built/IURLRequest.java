package com.raweng.built;

import java.util.List;

import org.apache.http.Header;
import org.apache.http.NameValuePair;


/**
 *
 * @author raw engineering, Inc
 */
public interface IURLRequest {

    public void send();

    public IResponse getResponse();

    public void setResponse(IResponse response);

    public void setHeaders(Header[] headers);

    public Header[] getHeaders();

    public void setRequestMethod(String requestMethod);

    public String getRequestMethod();

    public void setFormParams(List<NameValuePair> formParams);

    public List<NameValuePair> getFormParams();

    public void setInfo(String info);

    public String getInfo();
    
    public void setController(String controller);

    public String getController();
    
    public void setRapCallBackObject(BuiltResultCallBack builtResultCallBackObject);

    public BuiltResultCallBack getBuiltResultCallBack();

    public void setTreatDuplicateKeysAsArrayItems(boolean treatDuplicateKeysAsArrayItems);

    public boolean getTreatDuplicateKeysAsArrayItems();
    
    public void setPositionId(int position);
    
    public int getPositionId();
}
