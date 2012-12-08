package be.emich.labs.villohelper.task;

import android.content.ContentValues;
import be.emich.labs.villohelper.application.VilloHelperApplication;
import be.emich.labs.villohelper.exception.VilloHelperException;
import be.emich.labs.villohelper.parser.Station;
import be.emich.labs.villohelper.provider.DataHelper;
import be.emich.labs.villohelper.request.GetClosestStationsRequest;
import be.emich.labs.villohelper.response.GetStationsResponse;

public class GetClosestStationsTask extends GetAllStationsTask {
	private double latitude;
	private double longitude;
	private int limit;
	
	public GetClosestStationsTask(String system,double latitude,double longitude,int limit,String language) {
		super(system,language);
		this.latitude = latitude;
		this.longitude = longitude;
		this.limit = limit;
		this.system = system;
	}
	
	@Override
	protected Boolean doInBackground(String... params) {
		GetClosestStationsRequest request = new GetClosestStationsRequest(latitude,longitude,limit,language,system);
		GetStationsResponse response = null;
		try {
			response = (GetStationsResponse)request.performRequest();
		} catch (VilloHelperException e) {
			exception = e;
			return false;
		}
		
		if(response.isRequestSuccessful()){
			DataHelper dataHelper = VilloHelperApplication.getInstance().getDataHelper();
			ContentValues cvReset = new ContentValues();
			cvReset.put(Station.IS_CLOSEST, false);
			dataHelper.updateStations(cvReset);
			dataHelper.insertStations(response.getStationsAsContentValues(),false);
			return true;
		}
		
		return false;
	}
	
	
}
