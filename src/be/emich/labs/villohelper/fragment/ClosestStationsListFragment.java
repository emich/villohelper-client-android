package be.emich.labs.villohelper.fragment;

import java.util.Calendar;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import be.emich.labs.villohelper.activity.MainActivity;
import be.emich.labs.villohelper.adapter.SimpleStationCursorAdapter;
import be.emich.labs.villohelper.adapter.StationCursorAdapter;
import be.emich.labs.villohelper.exception.ErrorType;
import be.emich.labs.villohelper.task.GetAllStationsTask;
import be.emich.labs.villohelper.task.GetAllStationsTask.OnStationsTaskCompletedListener;
import be.emich.labs.villohelper.task.GetClosestStationsTask;

public class ClosestStationsListFragment extends StationsListFragment implements LocationListener, OnStationsTaskCompletedListener{

	private LocationManager mLocationManager;
	private Location mLocation;
	
	private long lastFetch=0;
	
	@Override
	public void onResume() {
		super.onResume();
		mLocationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 200f, this);
		
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
		if(Calendar.getInstance().getTimeInMillis()-lastFetch<60*1000)return;
		
		if(mLocation!=null){
			lastFetch = Calendar.getInstance().getTimeInMillis();
		}
		else{
			mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		
		GetClosestStationsTask task = new GetClosestStationsTask(getApp().getCurrentSystem().getSystemId(), mLocation.getLatitude(), mLocation.getLongitude(), 10, getApp().getLanguage());
		task.setOnStationsTaskCompletedListener(this);
		task.execute();
	}

	@Override
	public void onStationsTaskCompleted(GetAllStationsTask task) {
		
	}

	@Override
	public void onStationsTaskFailed(GetAllStationsTask task,
			ErrorType errorType) {
		if(getActivity()!=null){((MainActivity)getActivity()).onStationsTaskFailed(task, errorType);}
	}
	
}
