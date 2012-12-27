package be.emich.labs.villohelper.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Window;
import be.emich.labs.villohelper.application.VilloHelperApplication;
import be.emich.labs.villohelper.exception.ErrorType;
import be.emich.labs.villohelper.fragment.AlertDialogFragment;
import be.emich.labs.villohelper.fragment.AlertDialogFragment.AlertDialogListener;
import be.emich.labs.villohelper.map.AvailabilityInfoWindowAdapter;
import be.emich.labs.villohelper.parser.Station;
import be.emich.labs.villohelper.provider.DataHelper;
import be.emich.labs.villohelper.task.GetAllStationsTask;
import be.emich.labs.villohelper.task.GetAllStationsTask.OnStationsTaskCompletedListener;
import be.emich.labs.villohelper.util.Availability;
import be.emich.labs.villohelper.util.IntentUtil;
import be.emich.villo.R;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class NearbyMapActivity extends VilloHelperActivity implements OnStationsTaskCompletedListener, LocationListener, OnInfoWindowClickListener, AlertDialogListener{
	private GoogleMap googleMap;
	private DataHelper dataHelper;
	private LocationManager mLocationManager;
	private boolean mIsLocationSet=false;
	
	public static int DIALOG_ERROR_PARSING=1;
	public static int DIALOG_ERROR_CONNECTING=2;
	
	private enum MapMode {MAP,HYBRID,RELIEF};
	
	public static final String EXTRA_IS_LOCATION_SET=IntentUtil.EXTRA_IS_LOCATION_SET;
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_map2);
		
		googleMap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		
		googleMap.setMyLocationEnabled(true);
		googleMap.setIndoorEnabled(true);
		googleMap.setInfoWindowAdapter(new AvailabilityInfoWindowAdapter(this));
		googleMap.setOnInfoWindowClickListener(this);
		setMapMode(getApp().getMapMode());
		
		dataHelper = VilloHelperApplication.getInstance().getDataHelper();
		
		mLocationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		VilloHelperApplication app = VilloHelperApplication.getInstance();
		
		if(bundle!=null){
			mIsLocationSet = bundle.getBoolean(EXTRA_IS_LOCATION_SET);
			VilloHelperApplication.getInstance().trackPageView("/nearby");
		}
		
		if(!mIsLocationSet){
			String bestProvider = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)?LocationManager.GPS_PROVIDER:LocationManager.NETWORK_PROVIDER;
			mLocationManager.requestLocationUpdates(bestProvider, 1000, 100, this);
		}
		
		if(bundle==null){
			Location location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if(location==null){
				location=new Location(LocationManager.GPS_PROVIDER);
				location.setLatitude(app.getCurrentSystem().getCenterLatitude());
				location.setLongitude(app.getCurrentSystem().getCenterLongitude());
			}
			
			GetAllStationsTask taskAllStations = new GetAllStationsTask(app.getCurrentSystem().getSystemId(),app.getLanguage(),false);
			taskAllStations.setOnStationsTaskCompletedListener(this);
			taskAllStations.execute();
			setProgressBarIndeterminateVisibility(true);
		}
		
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		
		bindData();
	}
	
	@Override
	protected void onPause() {
		mLocationManager.removeUpdates(this);
		super.onPause();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_map, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(EXTRA_IS_LOCATION_SET, mIsLocationSet);
		super.onSaveInstanceState(outState);
	}
	
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()==android.R.id.home){
			finish();
			return true;
		}
		else if(item.getItemId()==R.id.menuMode){
			//FIXME
			//mapView.setSatellite(!mapView.isSatellite());
			AlertDialogFragment adf = AlertDialogFragment.newInstance(AlertDialogFragment.TYPE_MAP_MODE);
			adf.show(getSupportFragmentManager(), "mapmode");
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void bindData(){
		
		googleMap.clear();
		
		Cursor c = dataHelper.getAllStations();
		
		if(c.getCount()>0){
			c.moveToFirst();
			while(!c.isAfterLast()){
				String stationName = c.getString(c.getColumnIndex(Station.NAME));
				String id = c.getString(c.getColumnIndex(Station.ID));
				int bikes = c.getInt(c.getColumnIndex(Station.BIKES));
				int parking = c.getInt(c.getColumnIndex(Station.PARKING));
				boolean open = c.getInt(c.getColumnIndex(Station.OPEN))==1;
				boolean ticket = c.getInt(c.getColumnIndex(Station.TICKET))==1;
				double latitude = c.getDouble(c.getColumnIndex(Station.LATITUDE));
				double longitude = c.getDouble(c.getColumnIndex(Station.LONGITUDE));
				
				int number = bikes;
				
				boolean isCheckedIn = VilloHelperApplication.getInstance().isCheckedIn();
				
				if(isCheckedIn){
					number = parking;
				}
				
				if(!open){
					
					googleMap.addMarker(new MarkerOptions()
						.position(new LatLng(latitude,longitude))
	                    .title(stationName)
	                    .snippet(id)
	                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_placemark_grey)));
				}else{
					if(Availability.getLevel(number).equals(Availability.NONE)){
						googleMap.addMarker(new MarkerOptions()
	                    .position(new LatLng(latitude,longitude))
	                    .title(stationName)
	                    .snippet(id)
	                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_placemark_red)));
					}
					else if(Availability.getLevel(number).equals(Availability.PARTIAL)){
						googleMap.addMarker(new MarkerOptions()
	                    .position(new LatLng(latitude,longitude))
	                    .title(stationName)
	                    .snippet(id)
	                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_placemark_orange)));
					}
					else if(Availability.getLevel(number).equals(Availability.AVAILABLE)){
						googleMap.addMarker(new MarkerOptions()
	                    .position(new LatLng(latitude,longitude))
	                    .title(stationName)
	                    .snippet(id)
	                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_placemark_green)));
					}
					else{
						googleMap.addMarker(new MarkerOptions()
	                    .position(new LatLng(latitude,longitude))
	                    .title(stationName)
	                    .snippet(id)
	                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_placemark_grey)));
					}
				}
				
				c.moveToNext();
			}
		}
		
		c.close();
		
	}
	
	@Override
	public void onStationsTaskCompleted(GetAllStationsTask task) {
		setProgressBarIndeterminateVisibility(false);
		bindData();
	}

	@Override
	public void onStationsTaskFailed(GetAllStationsTask task,
			ErrorType errorType) {
		setProgressBarIndeterminateVisibility(false);
		if(errorType.equals(ErrorType.CONNECTION_ERROR)){
			showDialog(DIALOG_ERROR_CONNECTING);
		}else if(errorType.equals(ErrorType.INVALID_DATA)){
			showDialog(DIALOG_ERROR_PARSING);
		}
	}
	
	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		if(id==DIALOG_ERROR_CONNECTING){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.dialog_title_error_connecting);
			builder.setMessage(R.string.dialog_content_error_connecting);
			builder.setPositiveButton(R.string.dialog_action_ok, null);
			return builder.create();
		}
		else if(id==DIALOG_ERROR_PARSING){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.dialog_title_error_parsing);
			builder.setMessage(R.string.dialog_content_error_parsing);
			builder.setPositiveButton(R.string.dialog_action_ok, null);
			return builder.create();
		}
		return super.onCreateDialog(id);
	}

	@Override
	public void onLocationChanged(Location location) {
		mLocationManager.removeUpdates(this);
		
		LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
		CameraPosition position = new CameraPosition(pos, 16, 0, 0);
		CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(position);
		googleMap.moveCamera(cameraUpdate);
		
		
		mIsLocationSet=true;
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

	@Override
	public void onInfoWindowClick(Marker marker) {
		String id = marker.getSnippet();
		Intent i = new Intent(this,DetailActivity.class);
		i.putExtra(DetailActivity.EXTRA_ID, id);
		i.putExtra(DetailActivity.EXTRA_SYSTEM, getApp().getCurrentSystem().getSystemId());
		startActivity(i);
	}
	
	@Override
	public void doNegativeClick() {
	}
	
	@Override
	public void doPositiveClick() {
	}
	
	@Override
	public void doNeutralClick() {
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		setMapMode(which);
		getApp().setMapMode(which);
	}
	
	private void setMapMode(int mapMode){
		if(mapMode==MapMode.MAP.ordinal()){
			googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		}
		else if(mapMode==MapMode.HYBRID.ordinal()){
			googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		}
		else if(mapMode==MapMode.RELIEF.ordinal()){
			googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
		}
		
	}
}
