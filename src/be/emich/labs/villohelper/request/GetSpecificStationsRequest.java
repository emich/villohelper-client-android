package be.emich.labs.villohelper.request;

import be.emich.labs.villohelper.parser.BikeSystem;
import be.emich.labs.villohelper.response.GetStationsResponse;

public class GetSpecificStationsRequest extends GetAllStationsRequest {
	public GetSpecificStationsRequest(BikeSystem system,String language,String... ids) {
		super(system,language);
		
		StringBuffer idsStr = new StringBuffer();
		for(String id:ids){
			idsStr.append(id+",");
		}
		
		setPath("/closest.php?ids="+idsStr.toString()+"&system="+system.getSystemId());
	}
	
	@Override
	protected GetStationsResponse createStationsResponse(){
		return new GetStationsResponse(language);
	}
}
