package be.emich.labs.villohelper.response;

import org.apache.http.Header;

import android.content.ContentValues;
import be.emich.labs.villohelper.exception.VilloHelperException;
import be.emich.labs.villohelper.parser.Stations;

public class GetStationsResponse extends Response {

	private Stations stations;
	private boolean isClosest;
	private String language;
	private double currentLatitude;
	private double currentLongitude;
	
	public GetStationsResponse(boolean isClosest,double currentLatitude,double currentLongitude,String language) {
		super();
		this.isClosest = true;
		this.language = language;
		this.currentLatitude = currentLatitude;
		this.currentLongitude = currentLongitude;
	}
	
	public GetStationsResponse(String language) {
		super();
		this.isClosest=false;
		this.language=language;
	}
	
	@Override
	public Response handleResponse(int httpStatusCode, String responseString,
			Header[] headers) throws VilloHelperException {
		super.handleResponse(httpStatusCode, responseString, headers);
		
		if(isRequestSuccessful()){
			this.stations = new Stations(isClosest,currentLatitude,currentLongitude,language);
			stations.parse(responseString);
		}
		
		return this;
	}
	
	public Stations getStations() {
		return stations;
	}
	
	public ContentValues[] getStationsAsContentValues(){
		return stations!=null?stations.getStationsAsContentValues():null;
	}
	
}
