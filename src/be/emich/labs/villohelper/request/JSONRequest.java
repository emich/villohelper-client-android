package be.emich.labs.villohelper.request;

import org.json.JSONObject;

public abstract class JSONRequest extends Request {
	
	public static final int METHOD_POST_JSON = 10;
	
	public static final String CONTENT_TYPE_JSON = "application/json";
	
	public JSONRequest(String hostBaseUrl) {
		super(hostBaseUrl);
	}
	
	public void setJsonContent(JSONObject jsonContent) {
		setMethod(METHOD_POST_CONTENT);
		setContent(jsonContent.toString());
		setContentType(CONTENT_TYPE_JSON);
	}	
}
