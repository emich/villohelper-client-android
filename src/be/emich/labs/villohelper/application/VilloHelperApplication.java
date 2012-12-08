package be.emich.labs.villohelper.application;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import be.emich.labs.villohelper.parser.BikeSystem;
import be.emich.labs.villohelper.provider.DataHelper;
import be.emich.villo.BuildConfig;

public class VilloHelperApplication extends Application {
	public static final String BASEPATH = "http://www.bxlblog.be/villo";
	
	public static final String BIKESYSTEM = "be.emich.labs.villohelper.system";
	public static final String HAS_RAN_FIRST_TIME = "be.emich.labs.villohelper.has_ran_first_time";
	public static final String HAS_DONE_FIRST_SYNC = "be.emich.labs.villohelper.has_done_first_sync";
	public static final String LAST_SYNC = "be.emich.labs.villohelper.last_sync";
	public static final String IS_CHECKED_IN = "be.emich.labs.villohelper.is_checked_in";
	
	public static VilloHelperApplication instance;
	
	private DataHelper dataHelper;
	private SharedPreferences prefs;
	private BikeSystem bikeSystem;
	
	private GoogleAnalyticsTracker mTracker;
	
	public VilloHelperApplication() {
		super();
		VilloHelperApplication.instance = this;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		dataHelper = new DataHelper(this);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		mTracker = GoogleAnalyticsTracker.getInstance();
		mTracker.startNewSession("UA-1251388-9", getApplicationContext());
		
		/*if(BuildConfig.DEBUG){
			Editor editor = prefs.edit();
			editor.clear();
			editor.commit();
		}*/
		
		
		LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if(location!=null){
			BikeSystem detectedSystem = BikeSystem.getBestSystemForLatLong(location.getLatitude(), location.getLongitude());
			BikeSystem currentSystem = getCurrentSystem();
			setCurrentSystem(detectedSystem);
			if(!detectedSystem.equals(currentSystem)){
				setLastSyncDate(new Date(0));
			}
		}
		else{
			setCurrentSystem(BikeSystem.VILLO);
		}
		
	}
	
	public void trackPageView(String pageView){
		mTracker.trackPageView(pageView);
		mTracker.dispatch();
	}
	
	public void trackEvent(String category,String action, String label){
		mTracker.trackEvent(category, action, label, 1);
		mTracker.dispatch();
	}
	
	public static VilloHelperApplication getInstance() {
		return instance;
	}
	
	public DataHelper getDataHelper() {
		return dataHelper;
	}
	
	public BikeSystem getCurrentSystem(){
		if(bikeSystem!=null)return bikeSystem;
		
		String systemId = prefs.getString(BIKESYSTEM, null);
		if(systemId==null)return BikeSystem.ALL;
		for(BikeSystem bikeSystem:BikeSystem.values()){
			if(bikeSystem.getSystemId().equals(systemId)){
				return bikeSystem;
			}
		}
		return BikeSystem.ALL;
	}
	
	public void setCurrentSystem(BikeSystem system){
		Editor editor = prefs.edit();
		editor.putString(BIKESYSTEM, system.getSystemId());
		editor.commit();
		
		bikeSystem = system;
	}
	
	public boolean hasRanFirstTime(){
		return prefs.getBoolean(HAS_RAN_FIRST_TIME, false);
	}
	
	public void setHasRanFirstTime(){
		Editor editor = prefs.edit();
		editor.putBoolean(HAS_RAN_FIRST_TIME, true);
		editor.commit();
	}
	
	public boolean hasDoneFirstSync(){
		return prefs.getBoolean(HAS_DONE_FIRST_SYNC, false);
	}
	
	public void setDoneFirstSync(boolean hasDoneFirstSync){
		Editor editor = prefs.edit();
		editor.putBoolean(HAS_DONE_FIRST_SYNC, hasDoneFirstSync);
		editor.commit();
	}
	
	public void setLastSyncDate(Date lastSyncDate){
		Editor editor = prefs.edit();
		editor.putLong(LAST_SYNC, lastSyncDate.getTime());
		editor.commit();
	}
	
	public boolean mustSync(){
		if(!hasDoneFirstSync())return true;
		long lastSync = prefs.getLong(LAST_SYNC, 0);
		long diff=Calendar.getInstance().getTimeInMillis()-lastSync;
		if(diff>(24*60*60*1000))return true;
		return false;
	}
	
	public void resetSync(){
		setDoneFirstSync(false);
	}
	
	public String getLanguage(){
		return Locale.getDefault().getLanguage();
	}
	
	public boolean isCheckedIn(){
		return prefs.getBoolean(IS_CHECKED_IN, false);
	}
	
	public void setCheckedIn(boolean isCheckedIn){
		Editor editor = prefs.edit();
		editor.putBoolean(IS_CHECKED_IN, isCheckedIn);
		editor.commit();
	}
	
}
