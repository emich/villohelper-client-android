package be.emich.labs.villohelper.thirdparty.runkeeper;

public enum OAuth {
	//TODO: fill in
	RUNKEEPER("","","");
	
	private String authorizationUrl;
	private String redirectUrl;
	private String appId;
	
	OAuth(String authorizationUrl,String redirectUrl,String appId){
		this.authorizationUrl = authorizationUrl;
		this.redirectUrl = redirectUrl;
		this.appId = appId;
	}
	
	public String getAuthorizationUrl() {
		return authorizationUrl;
	}
	
	public String getRedirectUrl() {
		return redirectUrl;
	}
	
	public String getAppId() {
		return appId;
	}
}
