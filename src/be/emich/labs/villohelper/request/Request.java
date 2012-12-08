package be.emich.labs.villohelper.request;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecFactory;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.util.Config;
import be.emich.labs.villohelper.exception.VilloHelperException;
import be.emich.labs.villohelper.response.Response;
import be.emich.labs.villohelper.ssl.EasySSLSocketFactory;
import be.emich.labs.villohelper.util.Log;
import be.emich.villo.BuildConfig;


public abstract class Request {
	public static final int METHOD_GET = 0;
	public static final int METHOD_POST = 1;
	public static final int METHOD_POST_CONTENT = 2;
	
	public static final String CONTENT_TYPE_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
	
	private String baseUrl;
	private String path;
	private Map<String,String> parameters;
	private Map<String,String> customHeaders;
	private String content;
	private String contentType;
	private String hostName; 
	
	private int method;
	private boolean usesCookieStore=true;
	private static CookieStore sharedCookieStore = new BasicCookieStore();
	private CookieStore mUserCookieStore;
	private boolean followsRedirects=false;
	private int connectTimeout=-1;
	private int socketTimeout=-1;
	
	
	public Request(String baseUrl) {
		parameters = new HashMap<String,String>();
		this.baseUrl = baseUrl;
	}
	
	public String getBaseUrl() {
		return baseUrl;
	}
	
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getContentType() {
		return contentType;
	}
	
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	public void setMethod(int method) {
		this.method = method;
	}
	
	public int getMethod() {
		return method;
	}
	
	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}
	
	public void setParameters(String... parameterKeyValues){
		if(parameterKeyValues!=null && parameterKeyValues.length%2==1){
			throw new RuntimeException("Wrong parameter count, needs to be even (set of key-value pairs)");
		}
		
		if(parameterKeyValues!=null){
			for(int i=0;i<parameterKeyValues.length;i+=2){
				if(parameterKeyValues[i+1]!=null){
					parameters.put(parameterKeyValues[i],parameterKeyValues[i+1]);
				}
			}
		}
	}
	
	public void setUsesCookieStore(boolean usesCookieStore) {
		this.usesCookieStore = usesCookieStore;
	}

	private HttpRequestBase prepareGetRequest(HttpClient client){
		HttpGet httpGet = null;
		
		StringBuilder parametersString = new StringBuilder();
		
		for(Map.Entry<String, String> entry:parameters.entrySet()){
			if(parametersString.length()>0)parametersString.append("&");
			parametersString.append(URLEncoder.encode(entry.getKey()));
			parametersString.append("=");
			parametersString.append(URLEncoder.encode(entry.getValue()));
		}
		
		if(parametersString.length()>0){
			httpGet = new HttpGet(baseUrl+(path==null?"":path)+"?"+parametersString.toString());
		}
		else{
			httpGet = new HttpGet(baseUrl+(path==null?"":path));
		}
		
		Log.v(getClass().getName(), "[GET] Getting from "+baseUrl+(path==null?"":path));
		
		return httpGet;
	}
	
	private HttpRequestBase preparePostRequest(HttpClient client){
		HttpPost httpPost = new HttpPost(baseUrl+(path==null?"":path));
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		for(Map.Entry<String, String> entry:parameters.entrySet()){
			nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
		if(nameValuePairs.size()>0){
			try {
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		Log.v(getClass().getName(), "[POST] Getting from "+baseUrl+(path==null?"":path));
		
		return httpPost;
	}
	
	public abstract Response handleResponse(int httpStatusCode,String responseString,Header[] header) throws VilloHelperException;
	
	public Response performRequest() throws VilloHelperException{
		HttpContext localContext = new BasicHttpContext();
		if(usesCookieStore){
			localContext.setAttribute(ClientContext.COOKIE_STORE, getCookieStore());
		}
		
		if(getCookieStore()!=null){
			//cookieStore.clearExpired(Calendar.getInstance().getTime());
			for(Cookie cookie:getCookieStore().getCookies()){
				Log.v(getClass().getName(), "Cookiestore before : "+cookie.getName()+" : "+cookie.getValue()+ " / "+cookie.getExpiryDate());
			}
		}

		//Begin experimental code
		SchemeRegistry schemeRegistry = null;
		schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", Config.DEBUG?new EasySSLSocketFactory():SSLSocketFactory.getSocketFactory(), 443));
		 
		HttpParams params = new BasicHttpParams();
		params.setParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 30);
		params.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, new ConnPerRouteBean(30));
		params.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, false);
		if(connectTimeout!=-1)HttpConnectionParams.setConnectionTimeout(params, connectTimeout);
		if(socketTimeout!=-1)HttpConnectionParams.setSoTimeout(params, socketTimeout);
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		 
		ClientConnectionManager cm = new SingleClientConnManager(params, schemeRegistry);
		DefaultHttpClient httpClient = BuildConfig.DEBUG?new DefaultHttpClient(cm, params):new DefaultHttpClient(params);
		
		httpClient.getParams().setParameter("http.protocol.handle-redirects",followsRedirects);
		
		CookieSpecFactory csf = new CookieSpecFactory() {
            public CookieSpec newInstance(HttpParams params) {
                return new org.apache.http.impl.cookie.BrowserCompatSpec() {
                    @Override
                    public void validate(Cookie cookie, org.apache.http.cookie.CookieOrigin origin)
                    throws MalformedCookieException {
                      // allow all cookies
                    	System.out.println("Validate cookie : " + cookie.getName());
                    }
                };
            }
        };
        httpClient.getCookieSpecs().register("easy", csf);
        httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, "easy"); 
		//End experimental code
		
		HttpRequestBase httpRequestBase=null;
		switch(method){
		case METHOD_GET:
			httpRequestBase=prepareGetRequest(httpClient);
			break;
		case METHOD_POST:
			httpRequestBase=preparePostRequest(httpClient);
			break;
		case METHOD_POST_CONTENT:
			httpRequestBase=preparePostContentRequest(httpClient);
			break;
		}
		
		
		if(hostName!=null)httpRequestBase.addHeader("Host", hostName);
		if(contentType!=null && contentType.length()!=0)httpRequestBase.addHeader("Content-type", contentType);
		if(customHeaders!=null){
			for(Entry<String,String> entry : customHeaders.entrySet()){
				httpRequestBase.addHeader(entry.getKey(),entry.getValue());
			}
		}
		
		
		if(getCookieStore()!=null){
			//cookieStore.clearExpired(Calendar.getInstance().getTime());
			for(Cookie cookie:getCookieStore().getCookies()){
				Log.v(getClass().getName(), "Cookiestore after : "+cookie.getName()+" : "+cookie.getValue()+ " / "+cookie.getExpiryDate());
			}
		}
		
		try {
			
			HttpResponse response = httpClient.execute(httpRequestBase,localContext);
			
			int statusCode = response.getStatusLine().getStatusCode();
			
			Header[] headers = response.getAllHeaders();
            
            Log.v(getClass().getName(),"Status code : "+statusCode);
			
			String responseString = null;
			
			
			
			try{
				/*BasicResponseHandler handler = new BasicResponseHandler();
				responseString = handler.handleResponse(response);*/
				responseString = EntityUtils.toString(response.getEntity());
			}
			catch(HttpResponseException e){
				responseString = e.getMessage();
			}
			
			Log.v(getClass().getName(),"Response: "+responseString);
			
			return handleResponse(statusCode,responseString,headers);
		} catch (UnknownHostException e){
			e.printStackTrace();
			throw new VilloHelperException(e);
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			throw new VilloHelperException(e);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			throw new VilloHelperException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new VilloHelperException(e);
		}
	}

	public CookieStore getCookieStore() {
		return mUserCookieStore!=null?mUserCookieStore:sharedCookieStore;
	}
	
	private HttpRequestBase preparePostContentRequest(HttpClient client){
		
		try {
			HttpPost request = new HttpPost(baseUrl+(path==null?"":path));
			request.setEntity(new ByteArrayEntity(
			    content.toString().getBytes("UTF8")));
			
			Log.v(getClass().getName(), "Posting to "+baseUrl+(path==null?"":path));
			Log.v(getClass().getName(), "JSON content: "+content);
			return request;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}
	
	public int getConnectTimeout() {
		return connectTimeout;
	}
	
	public void setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
	}
	
	public int getSocketTimeout() {
		return socketTimeout;
	}
	
	public void setFollowsRedirects(boolean followsRedirects) {
		this.followsRedirects = followsRedirects;
	}
	
	public boolean getFollowsRedirects() {
		return followsRedirects;
	}
	
	public void setCustomHeaders(Map<String, String> customHeaders) {
		this.customHeaders = customHeaders;
	}
	
	public Map<String, String> getCustomHeaders() {
		return customHeaders;
	}
	
	/*
	 * Used for header only.
	 */
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	
	public String getHostName() {
		return hostName;
	}
	
	public void setCookieStore(CookieStore userCookieStore) {
		mUserCookieStore = userCookieStore;
	}
	
	public static CookieStore getSharedCookieStore() {
		return sharedCookieStore;
	}
}
