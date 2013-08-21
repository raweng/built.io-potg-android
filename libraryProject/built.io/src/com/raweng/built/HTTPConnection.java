package com.raweng.built;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.json.JSONObject;

import com.raweng.built.utilities.BuiltAppConstants;
import com.raweng.built.utilities.BuiltAppConstants.RequestMethod;
import com.raweng.built.utilities.RawAppUtils;

final public class HTTPConnection implements IURLRequestHTTP{

	private BuiltAppConstants.RequestMethod  method 			     = null;
	private HashMap<String, java.lang.Object> formParams 	         = null;
	private SSLContext sslContext;
	private HttpURLConnection urlConnection;
	private org.apache.http.Header[] requestHeaders 		         = null;
	private int statusCode = 0;
	private BufferedInputStream inputStream;
	private JSONObject responseJSON;
	private boolean treatDuplicateKeysAsArrayItems;
	private String requestInfo;
	private String controller;
	private ResultCallBack callBackObject;
	private String urlPath;
	private IRequestModelHTTP request;
	JSONObject mainJsonObj;

	HTTPConnection(String urlPath, IRequestModelHTTP request) {
		this.urlPath = urlPath;
		this.request = request;
	}

	public String setFormParamsGET(HashMap<String, java.lang.Object> params){
		if(params != null && params.size() > 0){
			String urlParams = null;
			for (Entry<String, java.lang.Object> e : params.entrySet()) {
				if(urlParams == null){
					urlParams = "?" + e.getKey() + "=" + e.getValue();
				}else{
					urlParams += "&" + e.getKey() + "=" + e.getValue();
				}
			}
			return urlParams;	
		}
		return null;		
	}

	public void setFormParamsPOST(JSONObject jObj){
		mainJsonObj = null;
		mainJsonObj = jObj;
	}

	public void setFormParamsPOST(HashMap<String, java.lang.Object> params){
		try{
			if(params != null && params.size() > 0){
				mainJsonObj = new JSONObject();
				for (Entry<String, java.lang.Object> e : params.entrySet()) {
					mainJsonObj.put(e.getKey(), e.getValue());
				}
			}
		}catch (Exception e) {
			RawAppUtils.showLog("HTTPConnection","-setFormParamsPOST--catch|"+e);

		}
	}


	private HostnameVerifier hostNameVerifier = new HostnameVerifier() {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};


	private void trustAllHosts() {

		TrustManager tm = new X509TrustManager() {
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};
		try {
			sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, new TrustManager[]{tm}, null);
		} catch (Exception error) {
			RawAppUtils.showLog("HTTPConnection", "---------------trustAllHosts-catch|" + error);
		}
	}

	@Override
	public void send() {
		try{
			String url = null;
			if(method == BuiltAppConstants.RequestMethod.GET){
				if(setFormParamsGET(formParams) != null){
					url = urlPath + setFormParamsGET(formParams);
				}else{
					url = urlPath;
				}
			}else{
				url = urlPath;
			}

			url = url.replaceAll(" ", "%20");

			System.setProperty("http.keepAlive", "false");
			URL URLLink = new URL(url.toString());

			if(URLLink.getProtocol().contains("https")){
				trustAllHosts();
				urlConnection = (HttpsURLConnection) URLLink.openConnection();

				if(method != BuiltAppConstants.RequestMethod.GET){
					urlConnection.setDoOutput(true);
					urlConnection.setDoInput(true);
				}

				((HttpsURLConnection) urlConnection).setSSLSocketFactory(sslContext.getSocketFactory());
				((HttpsURLConnection) urlConnection).setHostnameVerifier(hostNameVerifier);
			}else{
				urlConnection = (HttpURLConnection) URLLink.openConnection();
			}

			int count = requestHeaders.length;
			for(int i = 0; i < count; i++){
				urlConnection.setRequestProperty(requestHeaders[i].getName(), requestHeaders[i].getValue());
			}
			urlConnection.setRequestProperty("Content-Type","application/json");   


			urlConnection.setRequestMethod(method.toString());

			if((method == BuiltAppConstants.RequestMethod.POST) && (mainJsonObj != null)){
				DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream ());
				wr.writeBytes(mainJsonObj.toString());
				wr.flush();
				wr.close();
			}

			if(BuiltAppConstants.cancelledCallController.contains(requestInfo)){

				// call cancelled 
				RawAppUtils.showLog("HTTPConnection", "-------------------call cancelled---requestInfo|"+requestInfo);
				BuiltAppConstants.cancelledCallController.remove(requestInfo);

			}else{
				urlConnection.connect();
				statusCode = urlConnection.getResponseCode();

				if((urlConnection.getResponseCode() == HttpURLConnection.HTTP_ACCEPTED) || (urlConnection.getResponseCode() == HttpURLConnection.HTTP_CREATED) || (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK)){

					inputStream = new BufferedInputStream(urlConnection.getInputStream());
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
					StringBuilder responseData = new StringBuilder();
					String line;
					while ((line = bufferedReader.readLine()) != null) {
						responseData.append(line);
					}
					if(responseData != null){
						responseJSON = new JSONObject(responseData.toString());
					}

					if(BuiltAppConstants.cancelledCallController.contains(requestInfo)){

						// call cancelled 
						RawAppUtils.showLog("HTTPConnection", "-------------------call cancelled---controller|"+controller);
						BuiltAppConstants.cancelledCallController.remove(requestInfo);
					}else{
						request.onRequestFinished(this);
					}
				}else{
					inputStream = new BufferedInputStream(urlConnection.getErrorStream());

					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
					StringBuilder responseData = new StringBuilder();
					String line;
					while ((line = bufferedReader.readLine()) != null) {
						responseData.append(line);
					}
					if(responseData != null){
						responseJSON = new JSONObject(responseData.toString());
					}

					if(BuiltAppConstants.cancelledCallController.contains(requestInfo)){
						// call cancelled 
						RawAppUtils.showLog("HTTPConnection", "-------------------call cancelled---requestInfo|"+requestInfo);
						BuiltAppConstants.cancelledCallController.remove(requestInfo);

					}
					request.onRequestFailed(responseJSON,statusCode, callBackObject);

				}
			}

		}catch (Exception error) {
			if(BuiltAppConstants.cancelledCallController.contains(requestInfo)){
				BuiltAppConstants.cancelledCallController.remove(requestInfo);
				RawAppUtils.showLog("HTTPConnection", "-------------------call cancelled---requestInfo|"+requestInfo);

			}else{

				try{
					responseJSON = new JSONObject();
					responseJSON.put("error_message", error);
				}catch (Exception e) {
				}
				request.onRequestFailed(responseJSON, statusCode, callBackObject);
			}

			RawAppUtils.showLog("HTTPConnection", "-------------------ERROR|" + error);
		}
	}

	public void setHeaders(Header[] requestHeaders) {
		this.requestHeaders = requestHeaders;
	}

	public Header[] getHeaders() {
		return this.requestHeaders;
	}


	public void setInfo(String requestInfo) {
		this.requestInfo = requestInfo;
	}

	public String getInfo() {
		return this.requestInfo;
	}

	public String getController() {
		return controller;
	}

	public void setController(String controller) {
		this.controller = controller;
	}

	@Override
	public void setCallBackObject(ResultCallBack callBackObject) {
		this.callBackObject = callBackObject;

	}

	@Override
	public ResultCallBack getCallBackObject() {
		return callBackObject;
	}

	public void setTreatDuplicateKeysAsArrayItems(boolean treatDuplicateKeysAsArrayItems) {
		this.treatDuplicateKeysAsArrayItems = treatDuplicateKeysAsArrayItems;
	}

	public boolean getTreatDuplicateKeysAsArrayItems() {
		return this.treatDuplicateKeysAsArrayItems;
	}

	@Override
	public void setRequestMethod(RequestMethod requestMethod) {
		method = requestMethod;
	}

	@Override
	public RequestMethod getRequestMethod() {
		return method;
	}

	@Override
	public JSONObject getResponse() {
		return responseJSON;
	}

	protected void cancelCall() {
		if(urlConnection != null){
			urlConnection.disconnect();
			urlConnection = null;
		}
	}

}