package be.emich.labs.villohelper.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Window;
import be.emich.labs.villohelper.application.VilloHelperApplication;
import be.emich.labs.villohelper.exception.ErrorType;
import be.emich.labs.villohelper.map.StationItemizedOverlay;
import be.emich.labs.villohelper.map.StationOverlayItem;
import be.emich.labs.villohelper.parser.Station;
import be.emich.labs.villohelper.provider.DataHelper;
import be.emich.labs.villohelper.task.GetAllStationsTask;
import be.emich.labs.villohelper.task.GetAllStationsTask.OnStationsTaskCompletedListener;
import be.emich.labs.villohelper.util.Availability;
import be.emich.labs.villohelper.util.IntentUtil;
import be.emich.labs.villohelper.util.Log;
import be.emich.villo.R;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class NearbyMapActivity extends SherlockMapActivity implements OnStationsTaskCompletedListener, LocationListener {

	private MapView mapView;
	private DataHelper dataHelper;
	private MyLocationOverlay myLocationOverlay;
	private LocationManager mLocationManager;
	private boolean mIsLocationSet=false;
	
	private StationItemizedOverlay<StationOverlayItem> itemsRed;
	private StationItemizedOverlay<StationOverlayItem> itemsOrange;
	private StationItemizedOverlay<StationOverlayItem> itemsGreen;
	private StationItemizedOverlay<StationOverlayItem> itemsGrey;
	
	public static int DIALOG_ERROR_PARSING=1;
	public static int DIALOG_ERROR_CONNECTING=2;
	
	public static final String EXTRA_IS_LOCATION_SET=IntentUtil.EXTRA_IS_LOCATION_SET;
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_map);
		
		mapView = (MapView)findViewById(R.id.mapview);
		
		dataHelper = VilloHelperApplication.getInstance().getDataHelper();
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		VilloHelperApplication app = VilloHelperApplication.getInstance();
		
		myLocationOverlay = new MyLocationOverlay(this, mapView);
		myLocationOverlay.enableMyLocation();
		mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		
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
						
			mapView.getController().setCenter(new GeoPoint((int)(location.getLatitude()*1E6), (int)(location.getLongitude()*1E6)));
			mapView.getController().setZoom(17);
			
			GetAllStationsTask taskAllStations = new GetAllStationsTask(app.getCurrentSystem().getSystemId(),app.getLanguage(),false);
			taskAllStations.setOnStationsTaskCompletedListener(this);
			taskAllStations.execute();
			setProgressBarIndeterminateVisibility(true);
		}
		
		mapView.setBuiltInZoomControls(true);
		
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		
		bindData();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mapView.getOverlays().add(myLocationOverlay);
		myLocationOverlay.enableMyLocation();
	}
	
	@Override
	protected void onPause() {
		mapView.getOverlays().remove(myLocationOverlay);
		mLocationManager.removeUpdates(this);
		myLocationOverlay.disableMyLocation();
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
			mapView.setSatellite(!mapView.isSatellite());
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void bindData(){
		
		if(itemsRed!=null){
			mapView.getOverlays().remove(itemsRed);
			//mapView.getOverlays().remove(itemsGreen);
			//mapView.getOverlays().remove(itemsOrange);
			
		}

		itemsRed = new StationItemizedOverlay<StationOverlayItem>(getResources().getDrawable(R.drawable.ic_placemark_red), mapView);
		itemsGreen = new StationItemizedOverlay<StationOverlayItem>(getResources().getDrawable(R.drawable.ic_placemark_green), mapView);
		itemsOrange = new StationItemizedOverlay<StationOverlayItem>(getResources().getDrawable(R.drawable.ic_placemark_orange), mapView);
		itemsGrey = new StationItemizedOverlay<StationOverlayItem>(getResources().getDrawable(R.drawable.ic_placemark_grey), mapView);
		
		
		boolean isCheckedIn = VilloHelperApplication.getInstance().isCheckedIn();
		
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
				
				//Log.v(getClass().getName(), "Latitude: "+latitude);
				
				GeoPoint geoPoint = new GeoPoint((int)(latitude*1E6), (int)(longitude*1E6));
				
				StationOverlayItem item = new StationOverlayItem(geoPoint, stationName, bikes, parking, ticket, open, id);
				
				int number = bikes;
				if(isCheckedIn){
					number = parking;
				}
				
				if(!open){
					itemsGrey.addOverlay(item);
				}else{
					if(Availability.getLevel(number).equals(Availability.NONE)){
						itemsRed.addOverlay(item);
					}
					else if(Availability.getLevel(number).equals(Availability.PARTIAL)){
						itemsOrange.addOverlay(item);
					}
					else if(Availability.getLevel(number).equals(Availability.AVAILABLE)){
						itemsGreen.addOverlay(item);
					}
					else{
						itemsGrey.addOverlay(item);
					}
				}
				
				Log.v(getClass().getName(),"Item added "+geoPoint.toString()+"/"+geoPoint.getLatitudeE6()+":"+geoPoint.getLongitudeE6());
				
				c.moveToNext();
			}
		}
		c.close();
		
		if(itemsRed.size()>0)mapView.getOverlays().add(itemsRed);
		if(itemsOrange.size()>0)mapView.getOverlays().add(itemsOrange);
		if(itemsGreen.size()>0)mapView.getOverlays().add(itemsGreen);
		if(itemsGrey.size()>0)mapView.getOverlays().add(itemsGrey);
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
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
		
		GeoPoint geoPoint = new GeoPoint((int)(location.getLatitude()*1E6),(int)(location.getLongitude()*1E6));
		mapView.getController().animateTo(geoPoint);
		
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

}
