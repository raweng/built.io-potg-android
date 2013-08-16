package com.raweng.built;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;

import com.raweng.built.utilities.BuiltAppConstants;
import com.raweng.built.utilities.EasySSLSocketFactory;
import com.raweng.built.utilities.RawAppUtils;

/**
 * AsyncTask to upload media files on built.io server.
 * 
 * @author raw engineering, Inc
 *
 */
class BuiltFileUploadRequest extends AsyncTask<java.lang.Object, Integer, Void>{

	private HttpClient defaultHttpClient    = null;
	private HttpResponse httpResponse       = null;
	private ClientConnectionManager clientConnectionManager;
	private Header[] requestHeaders;
	private  String urlPath;
	private String requestMethod;
	boolean treatDuplicateKeysAsArrayItems;
	private List<NameValuePair> formParams;
	private MultipartEntity multiPartEntity;
	private boolean isFileAttached = false;
	private BuildFileResultCallback callBackObjectForUpload = null;
	private BuiltResultCallBack callBackObjectForUpdate 	= null;
	private String requestKey;
	private INotifyUploadDone notifyUploadDone;
	private HttpPost httpPost;
	private HttpPut httpPut;
	private FileObject fileObject;

	protected BuiltFileUploadRequest(BuiltFile BuiltFileInstance, IRequestModel request, String Key, FileObject value,  BuildFileResultCallback callBackObject) {
		this.requestKey        = Key;
		notifyUploadDone       = BuiltFileInstance;
		fileObject			   = value;	

	}

	private void send() {
		//prepare for the https connection
		//call this returnedInputStream the constructor of the class that does the connection if
		//it's used multiple times
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		// http scheme
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		// https scheme
		schemeRegistry.register(new Scheme("https", new EasySSLSocketFactory(), 443));

		HttpParams params = new BasicHttpParams();
		params.setParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 1);
		params.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, new ConnPerRouteBean(1));
		params.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, false);
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, "utf8");

		ConnManagerParams.setTimeout(params, BuiltAppConstants.CONNECTION_TIMEOUT);
		HttpConnectionParams.setConnectionTimeout(params, BuiltAppConstants.CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(params, BuiltAppConstants.CONNECTION_SO_TIMEOUT);
		HttpConnectionParams.setTcpNoDelay(params, true);
		HttpConnectionParams.getSocketBufferSize(params);
		HttpConnectionParams.setSocketBufferSize(params, 8192);

		// ignore that the ssl cert is self signed
		CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		//credentialsProvider.setCredentials(new AuthScope("yourServerHere.com", AuthScope.ANY_PORT), new UsernamePasswordCredentials("YourUserNameHere", "UserPasswordHere"));
		credentialsProvider.setCredentials(new AuthScope(urlPath, AuthScope.ANY_PORT), new UsernamePasswordCredentials("YourUserNameHere", "UserPasswordHere"));
		clientConnectionManager = new ThreadSafeClientConnManager(params, schemeRegistry);

		HttpContext context = new BasicHttpContext();
		context.setAttribute("http.auth.credentials-provider", credentialsProvider);

		this.setDefaultHttpClient(new DefaultHttpClient(clientConnectionManager, params));

		try {
			if(! BuiltAppConstants.cancelMediaFileUploadNetworkCalls){
				if (this.getRequestMethod().equalsIgnoreCase(BuiltAppConstants.RequestMethod.POST.toString())) {
					httpPost = new HttpPost(new URI(urlPath));

					httpPost.setHeaders(this.getHeaders());
					if (this.isFileAttached){
						httpPost.setEntity(multiPartEntity);
					}


					this.httpResponse = this.getDefaultHttpClient().execute(httpPost, context);

				}else if (this.getRequestMethod().equalsIgnoreCase(BuiltAppConstants.RequestMethod.PUT.toString())) {
					httpPut = new HttpPut(new URI(urlPath));

					httpPut.setHeaders(this.getHeaders());
					if (this.isFileAttached){
						httpPut.setEntity(multiPartEntity);
					}

					this.httpResponse = this.getDefaultHttpClient().execute(httpPut, context);


				}

				InputStream returnedInputStream = this.httpResponse.getEntity().getContent();

				Header contentEncoding = this.httpResponse.getFirstHeader("Content-Encoding");

				if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
					returnedInputStream = new GZIPInputStream(returnedInputStream);
				}

				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(returnedInputStream));
				StringBuilder responseData = new StringBuilder();
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					responseData.append(line);
				}

				RawAppUtils.showLog("BuiltFileUploadRequest", this.urlPath);
				RawAppUtils.showLog("BuiltFileUploadRequest", "STATUS CODE: "+ this.httpResponse.getStatusLine().getStatusCode());
				RawAppUtils.showLog("upload", "upload---------multiPartEntity|" + multiPartEntity);

				if(! BuiltAppConstants.cancelMediaFileUploadNetworkCalls){
					if(this.httpResponse.getStatusLine().getStatusCode() == BuiltAppConstants.OK || this.httpResponse.getStatusLine().getStatusCode() == BuiltAppConstants.CREATED){

						if(responseData != null){
							JSONObject responseJSON = new JSONObject(responseData.toString());
							UploadedFileModel model = new UploadedFileModel(responseJSON, false);
							fileObject.contentType  = model.contentType;
							fileObject.fileSize     = model.fileSize;
							fileObject.uploadUrl    = model.uploadUrl;
							fileObject.fileName     = model.fileName;
							fileObject.json         = model.json;
							fileObject.uploadUid    = model.uploadedUid;
							fileObject.setTags(model.tags);

							if(notifyUploadDone != null){
								notifyUploadDone.getResult(requestKey, fileObject);
							}else{
								if(callBackObjectForUpdate != null){
									callBackObjectForUpdate.onRequestFinish();
								}
							}
							model = null;
						}

					}else{

						JSONObject responseJSON = new JSONObject(responseData.toString());

						BuiltError error = new BuiltError();

						String errorMessage = (responseJSON).isNull("error_message") == true ? "" : (responseJSON).optString("error_message");

						if(errorMessage != null){

							error.errorMessage(errorMessage);

						}else{
							error.errorMessage(BuiltAppConstants.ErrorMessage_Default);
						}
						error.errorCode(this.httpResponse.getStatusLine().getStatusCode());


						if(! responseJSON.isNull("errors")){
							HashMap<String, Object> resultHashMap = new HashMap<String, Object>();
							if(responseJSON.opt("errors") instanceof JSONObject){
								JSONObject errorsJsonObj =  responseJSON.optJSONObject("errors");
								Iterator<String> iterator = errorsJsonObj.keys();
								while (iterator.hasNext()) {
									String key = iterator.next();
									Object value = errorsJsonObj.opt(key);
									resultHashMap.put(key, value);
								}
							}else{
								resultHashMap.put("errors", responseJSON.get("errors"));
							}
							error.errors(resultHashMap);
						}



						if(callBackObjectForUpload != null){
							callBackObjectForUpload.onRequestFail(error);
						}else if(callBackObjectForUpdate != null){
							callBackObjectForUpdate.onRequestFail(error);
						}
					}
				}else{
					if(notifyUploadDone != null){
						notifyUploadDone.getResult(null, null);
					}else{
						if(callBackObjectForUpdate != null){
							callBackObjectForUpdate.onRequestFinish();
						}
					}
				}
			}else{
				if(notifyUploadDone != null){
					notifyUploadDone.getResult(null, null);
				}else{
					if(callBackObjectForUpdate != null){
						if(BuiltAppConstants.cancelMediaFileUploadNetworkCalls){
							BuiltAppConstants.cancelMediaFileUploadNetworkCalls = false;
						}
					}
				}
			}
		} catch(Exception e) {
			BuiltError error = new BuiltError();
			error.errorMessage(e.toString());
			if(this.httpResponse != null){
				error.errorCode(this.httpResponse.getStatusLine().getStatusCode());
			}else{
				error.errorCode(BuiltAppConstants.NONETWORKCONNECTION);
			}
			if(callBackObjectForUpload != null){
				callBackObjectForUpload.onError(error);

			}else if(callBackObjectForUpdate != null){
				callBackObjectForUpdate.onError(error);
			}
		}
	}


	void setHeaders(Header[] requestHeaders) {
		this.requestHeaders = requestHeaders;
	}

	private Header[] getHeaders() {
		return this.requestHeaders;
	}

	void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}

	void setURL(String url) {
		this.urlPath = url;
	}

	void setCallbackObject(ResultCallBack callback) {
		if(callback instanceof BuildFileResultCallback){
			callBackObjectForUpload = (BuildFileResultCallback)callback;
		}else{
			callBackObjectForUpdate = (BuiltResultCallBack) callback;
		}
	}

	private String getRequestMethod() {
		return this.requestMethod;
	}



	void setTreatDuplicateKeysAsArrayItems(boolean treatDuplicateKeysAsArrayItems) {
		this.treatDuplicateKeysAsArrayItems = treatDuplicateKeysAsArrayItems;
	}

	void setFormParams(List<NameValuePair> formParams) {
		this.formParams = formParams;
	}

	void addFile(String fileField, String filePath, String fileName) {
		try {
			this.isFileAttached = true;
			Charset chars = Charset.forName("UTF-8");
			multiPartEntity  = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			if (this.formParams.size() > 0) {
				for (ListIterator<NameValuePair> it = this.formParams.listIterator(); it.hasNext();) {
					BasicNameValuePair object = (BasicNameValuePair) it.next();
					String key = object.getName();
					String value = object.getValue();
					ContentBody cbMessage = new StringBody(value, chars);
					multiPartEntity.addPart(key, cbMessage); 
				}
			}

			if(filePath != null){
				File file           = new File(filePath);
				ContentBody cbFile  = new FileBody(file, getMimeType(fileName));
				multiPartEntity.addPart(fileField, cbFile);
			}

		} catch (Exception e) {
			BuiltError error = new BuiltError();
			error.errorMessage(e.toString());
			if(httpResponse != null){
				error.errorCode(this.httpResponse.getStatusLine().getStatusCode());
			}
			if(callBackObjectForUpload != null){
				callBackObjectForUpload.onRequestFail(error);

			}else if(callBackObjectForUpdate != null){
				callBackObjectForUpdate.onRequestFail(error);
			}
		}
	}



	private String getMimeType(String filename) {
		filename = filename.toLowerCase();
		if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
			return "image/jpeg";
		} else if (filename.endsWith(".png")) {
			return "image/x-png";
		} else if (filename.endsWith(".gif")) {
			return "image/gif";
		} else if (filename.endsWith(".bmp")) {
			return "image/x-ms-bmp";
		} else if (filename.endsWith(".pdf")) {
			return "application/pdf";
		} else if (filename.endsWith(".doc")) {
			return "application/msword";
		} else if (filename.endsWith(".docx")) {
			return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
		} else if (filename.endsWith(".ppt")) {
			return "application/vnd.ms-powerpoint";
		} else if (filename.endsWith(".pptx")) {
			return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
		} else if (filename.endsWith(".xls")) {
			return "application/vnd.ms-excel";
		} else if (filename.endsWith(".xlsx")) {
			return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		} else if (filename.endsWith(".accdb")) {
			return "application/msaccess";
		}else if (filename.endsWith(".mp4")) {
			return "video/mp4";
		}else if (filename.endsWith(".mp3")) {
			return "audio/mp3";
		}  // msacess -x
		else {
			return "text/plain";
		}
	}


	private void setDefaultHttpClient(HttpClient defaultHttpClient) {
		this.defaultHttpClient = defaultHttpClient;
	}

	private HttpClient getDefaultHttpClient() {
		return defaultHttpClient;
	}


	@Override
	protected Void doInBackground(java.lang.Object... params) {
		send();
		return null;
	}


	protected void cancelCall() {
		if(this.getDefaultHttpClient() != null){
			this.getDefaultHttpClient().getConnectionManager().shutdown();
		}
	}

}
