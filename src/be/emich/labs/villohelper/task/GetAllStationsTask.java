package be.emich.labs.villohelper.task;

import android.os.AsyncTask;
import be.emich.labs.villohelper.application.VilloHelperApplication;
import be.emich.labs.villohelper.exception.ErrorType;
import be.emich.labs.villohelper.exception.VilloHelperException;
import be.emich.labs.villohelper.exception.VilloHelperNetworkException;
import be.emich.labs.villohelper.exception.VilloHelperXMLException;
import be.emich.labs.villohelper.provider.DataHelper;
import be.emich.labs.villohelper.request.GetAllStationsRequest;
import be.emich.labs.villohelper.response.GetStationsResponse;

public class GetAllStationsTask extends AsyncTask<String, Void, Boolean> {

	private OnStationsTaskCompletedListener onStationsTaskCompletedListener;
	protected VilloHelperException exception;
	protected String system;
	protected String language;
	protected boolean shouldTruncate=true;
	
	public GetAllStationsTask(String system,String language) {
		this.system = system;
		this.language = language;
	}
	
	public GetAllStationsTask(String system,String language,boolean shouldTruncate) {
		this.system = system;
		this.language = language;
		this.shouldTruncate = shouldTruncate;
	}
	
	@Override
	protected Boolean doInBackground(String... params) {
		GetAllStationsRequest request = new GetAllStationsRequest(VilloHelperApplication.getInstance().getCurrentSystem(),language);
		GetStationsResponse response = null;
		try {
			response = (GetStationsResponse)request.performRequest();
		} catch (VilloHelperException e) {
			exception = e;
			e.printStackTrace();
			return false;
		}
		
		if(response.isRequestSuccessful()){
			DataHelper dataHelper = VilloHelperApplication.getInstance().getDataHelper();
			dataHelper.insertStations(response.getStationsAsContentValues(),shouldTruncate);
			return true;
			
		}
		
		return false;
	}
	
	@Override
	protected void onPostExecute(Boolean successful) {
		if(successful){
			if(this.onStationsTaskCompletedListener!=null){
				onStationsTaskCompletedListener.onStationsTaskCompleted(this);
			}
		}
		else{
			if(exception!=null){
				if(exception instanceof VilloHelperXMLException){
					if(this.onStationsTaskCompletedListener!=null){
						onStationsTaskCompletedListener.onStationsTaskFailed(this, ErrorType.INVALID_DATA);
					}
				}
				if(exception instanceof VilloHelperNetworkException){
					if(this.onStationsTaskCompletedListener!=null){
						onStationsTaskCompletedListener.onStationsTaskFailed(this, ErrorType.CONNECTION_ERROR);
					}
				}
				else{
					if(this.onStationsTaskCompletedListener!=null){
						onStationsTaskCompletedListener.onStationsTaskFailed(this, ErrorType.CONNECTION_ERROR);
					}
				}
			}
			else{
				onStationsTaskCompletedListener.onStationsTaskFailed(this, ErrorType.CONNECTION_ERROR);
			}
		}
	}
	
	public void setOnStationsTaskCompletedListener(
			OnStationsTaskCompletedListener onStationsTaskCompletedListener) {
		this.onStationsTaskCompletedListener = onStationsTaskCompletedListener;
	}
	
	public interface OnStationsTaskCompletedListener {
		void onStationsTaskCompleted(GetAllStationsTask task);
		void onStationsTaskFailed(GetAllStationsTask task,ErrorType errorType);
	}
	
}
