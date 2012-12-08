package be.emich.labs.villohelper.fragment;

import java.util.Calendar;

import be.emich.labs.villohelper.activity.MainActivity;
import be.emich.labs.villohelper.adapter.SimpleStationCursorAdapter;
import be.emich.labs.villohelper.adapter.StationCursorAdapter;
import be.emich.labs.villohelper.exception.ErrorType;
import be.emich.labs.villohelper.task.GetAllStationsTask;
import be.emich.labs.villohelper.task.GetAllStationsTask.OnStationsTaskCompletedListener;
import be.emich.labs.villohelper.task.GetClosestStationsTask;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class ClosestStationsListFragment extends StationsListFragment implements LocationListener, OnStationsTaskCompletedListener{

	private LocationManager mLocationManager;
	private Location mLocation;
	
	private long lastFetch=0;
	
	@Override
	public void onResume() {
		super.onResume();
		mLocationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000, 250f, this);
		
		mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		reloadClosestStations();
	}
	
	@Override
	protected SimpleStationCursorAdapter getAdapter() {
		return new StationCursorAdapter(getActivity(), null);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mLocationManager.removeUpdates(this);
	}

	@Override
	public void onLocationChanged(Location location) {
		mLocation = location;
		reloadClosestStations();
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
	
	public void reloadClosestStations(){
		//if(Calendar.getInstance().getTimeInMillis()-lastFetch<60*1000)return;
		
		if(mLocation!=null){
			GetClosestStationsTask task = new GetClosestStationsTask(getApp().getCurrentSystem().getSystemId(), mLocation.getLatitude(), mLocation.getLongitude(), 10, getApp().getLanguage());
			task.setOnStationsTaskCompletedListener(this);
			task.execute();
			lastFetch = Calendar.getInstance().getTimeInMillis();
		}
	}

	@Override
	public void onStationsTaskCompleted(GetAllStationsTask task) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStationsTaskFailed(GetAllStationsTask task,
			ErrorType errorType) {
		if(getActivity()!=null){((MainActivity)getActivity()).onStationsTaskFailed(task, errorType);}
	}
	
}
