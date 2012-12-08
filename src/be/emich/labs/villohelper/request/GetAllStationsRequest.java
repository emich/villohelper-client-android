package be.emich.labs.villohelper.request;

import org.apache.http.Header;

import be.emich.labs.villohelper.application.VilloHelperApplication;
import be.emich.labs.villohelper.exception.VilloHelperException;
import be.emich.labs.villohelper.parser.BikeSystem;
import be.emich.labs.villohelper.response.GetStationsResponse;
import be.emich.labs.villohelper.response.Response;

public class GetAllStationsRequest extends Request {

	protected String language;
	
	public GetAllStationsRequest(String language) {
		super(VilloHelperApplication.BASEPATH);
		setMethod(METHOD_GET);
		setPath("/closest.php");
		this.language=language;
		setSocketTimeout(5000);
		setConnectTimeout(5000);
	}
	
	public GetAllStationsRequest(BikeSystem system,String language){
		this(language);
		setPath("/closest.php?system="+system.getSystemId());
	}
	
	@Override
	public Response handleResponse(int httpStatusCode, String responseString,
			Header[] header) throws VilloHelperException {
		GetStationsResponse response = createStationsResponse();
		return response.handleResponse(httpStatusCode, responseString, header);
	}
	
	protected GetStationsResponse createStationsResponse(){
		return new GetStationsResponse(language);
	}

}
