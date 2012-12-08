package be.emich.labs.villohelper.task;

import be.emich.labs.villohelper.application.VilloHelperApplication;
import be.emich.labs.villohelper.exception.VilloHelperException;
import be.emich.labs.villohelper.parser.BikeSystem;
import be.emich.labs.villohelper.provider.DataHelper;
import be.emich.labs.villohelper.request.GetSpecificStationsRequest;
import be.emich.labs.villohelper.response.GetStationsResponse;

public class GetSpecificStationsTask extends GetAllStationsTask {
	private String[] mIds;
	private String mLanguage;
	private BikeSystem mSystem;
	
	public GetSpecificStationsTask(BikeSystem system,String language,String... ids) {
		super(system.getSystemId(),language);
		
		mSystem=system;
		mLanguage=language;
		mIds=ids;
	}
	
	@Override
	protected Boolean doInBackground(String... params) {
		GetSpecificStationsRequest request = new GetSpecificStationsRequest(mSystem, mLanguage, mIds);
		GetStationsResponse response = null;
		try{
			response = (GetStationsResponse)request.performRequest();
		}
		catch(VilloHelperException e){
			e.printStackTrace();
			return false;
		}
		
		if(response.isRequestSuccessful()){
			DataHelper dataHelper = VilloHelperApplication.getInstance().getDataHelper();
			dataHelper.insertStations(response.getStationsAsContentValues(),false);
			return true;
		}
		
		return false;
	}
}
