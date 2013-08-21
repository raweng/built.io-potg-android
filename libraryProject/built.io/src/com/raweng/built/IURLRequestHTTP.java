package com.raweng.built;

import org.apache.http.Header;
import org.json.JSONObject;

import com.raweng.built.utilities.BuiltAppConstants;
/**
 * 
 * @author raw engineering, Inc
 *
 */
public interface IURLRequestHTTP {
	
	public void send();

    public void setHeaders(Header[] headers);

    public Header[] getHeaders();

    public void setRequestMethod(BuiltAppConstants.RequestMethod requestMethod);

    public BuiltAppConstants.RequestMethod getRequestMethod();

    public JSONObject getResponse();

   
    
    public void setInfo(String info);

    public String getInfo();
    
    public void setController(String controller);

    public String getController();
    
    public void setCallBackObject(ResultCallBack builtResultCallBackObject);

    public ResultCallBack getCallBackObject();

    public void setTreatDuplicateKeysAsArrayItems(boolean treatDuplicateKeysAsArrayItems);

    public boolean getTreatDuplicateKeysAsArrayItems();
    
    
}
