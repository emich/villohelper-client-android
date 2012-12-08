package be.emich.labs.villohelper.request;

import be.emich.labs.villohelper.response.GetStationsResponse;

public class GetClosestStationsRequest extends GetAllStationsRequest {
	
	private double currentLatitude;
	private double currentLongitude;
	
	public GetClosestStationsRequest(double latitude,double longitude,Integer limit,String language,String systemid) {
		super(language);
		setPath("/closest.php?max=10&latitude="+latitude+"&longitude="+longitude+"&system="+systemid);
		
		this.currentLatitude = latitude;
		this.currentLongitude = longitude;
	}
	
	@Override
	protected GetStationsResponse createStationsResponse(){
		return new GetStationsResponse(true,currentLatitude,currentLongitude,language);
	}
}
