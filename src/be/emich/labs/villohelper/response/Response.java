package be.emich.labs.villohelper.response;

import org.apache.http.Header;

import be.emich.labs.villohelper.exception.VilloHelperException;

public class Response {
	public static final int HTTP_ERROR_NETWORK = 1;
	public static final int HTTP_LOGIN_FAILED = 2;
	
	public static final int HTTP_OK = 200;
	public static final int HTTP_UNMANAGED_ERROR = 500;
	public static final int HTTP_BACKEND_UNAVAILABLE = 503;
	public static final int HTTP_BAD_REQUEST = 400;
	public static final int HTTP_NOT_FOUND = 404;
	
	private int httpStatusCode;
	private int errorCode;
	private String errorMessage;
	
	private String contractId;
	
	private Header[] headers;
	
	public Response() {
		;
	}
	
	public int getHttpStatusCode() {
		return httpStatusCode;
	}
	
	//This will return true if the http status code of the page is 200, 301 or 302.
	public boolean isRequestSuccessful(){
		return httpStatusCode==200||httpStatusCode==301||httpStatusCode==302;
	}
	
	public int getErrorCode() {
		return errorCode;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}

	public Response handleResponse(int httpStatusCode,String jsonString,Header[] headers) throws VilloHelperException{
		this.httpStatusCode = httpStatusCode;
		this.headers = headers;
		return this;
	}
	
	public void setContractId(String contractId) {
		this.contractId = contractId;
	}
	
	public String getContractId() {
		return contractId;
	}
	
	public Header[] getHeaders() {
		return headers;
	}
	
	public void setHeaders(Header[] headers) {
		this.headers = headers;
	}
	
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
