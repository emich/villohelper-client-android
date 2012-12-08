package be.emich.labs.villohelper.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import be.emich.labs.villohelper.application.VilloHelperApplication;
import be.emich.labs.villohelper.exception.ErrorType;
import be.emich.labs.villohelper.fragment.AlertDialogFragment;
import be.emich.labs.villohelper.parser.Station;
import be.emich.labs.villohelper.task.GetAllStationsTask;
import be.emich.labs.villohelper.task.GetAllStationsTask.OnStationsTaskCompletedListener;
import be.emich.labs.villohelper.task.GetSpecificStationsTask;
import be.emich.labs.villohelper.util.Availability;
import be.emich.labs.villohelper.util.IntentUtil;
import be.emich.labs.villohelper.util.Log;
import be.emich.villo.R;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class DetailActivity extends VilloHelperActivity implements OnClickListener, OnStationsTaskCompletedListener {

	public static final String EXTRA_ID = IntentUtil.EXTRA_ID;
	public static final String EXTRA_SYSTEM = IntentUtil.EXTRA_SYSTEM;

	protected View viewClosed;
	protected TextView textViewStationName;
	protected TextView textViewHeaderAddress;
	protected TextView textViewAddress;
	protected TextView textViewBikes;
	protected TextView textViewParking;
	protected TextView textViewClosed;
	protected TextView textViewCreditCard;
	protected ViewGroup layoutAvailability;
	protected ImageView imageViewMap;
	protected ImageView imageViewBikes;
	protected ImageView imageViewParking;
	protected Typeface typeFaceThin;
	protected Typeface typeFaceRegular;
	protected Typeface typeFaceLight;
	protected Typeface typeFaceBold;
	
	private boolean mapLayoutDone=false;
	
	protected String stationId;
	protected Double mLatitude;
	protected Double mLongitude;
	
	protected MenuItem menuItemFavorite;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_detail);
		
		stationId = getIntent().getStringExtra(EXTRA_ID);
		

		typeFaceThin = Typeface.createFromAsset(getAssets(),
				"fonts/Roboto-Thin.ttf");
		typeFaceRegular = Typeface.createFromAsset(getAssets(),
				"fonts/Roboto-Regular.ttf");
		typeFaceLight = Typeface.createFromAsset(getAssets(),
				"fonts/Roboto-Light.ttf");
		typeFaceBold = Typeface.createFromAsset(getAssets(),
				"fonts/Roboto-Bold.ttf");
		textViewStationName = (TextView) findViewById(R.id.textViewStationName);
		textViewStationName.setTypeface(typeFaceThin);
		
		textViewAddress = (TextView)findViewById(R.id.textViewDetailAddress);
		textViewAddress.setTypeface(typeFaceLight);
		
		textViewHeaderAddress = (TextView)findViewById(R.id.textViewHeaderAddress);
		textViewHeaderAddress.setTypeface(typeFaceRegular);
		
		textViewBikes = (TextView)findViewById(R.id.textViewBikes);
		textViewBikes.setTypeface(typeFaceLight);
		
		textViewParking = (TextView)findViewById(R.id.textViewParking);
		textViewParking.setTypeface(typeFaceLight);
		
		textViewClosed = (TextView)findViewById(R.id.textViewStationClosed);
		textViewClosed.setTypeface(typeFaceLight);
		
		textViewCreditCard = (TextView)findViewById(R.id.textViewCardAccepted);
		textViewCreditCard.setTypeface(typeFaceLight);
		
		layoutAvailability = (LinearLayout)findViewById(R.id.layoutAvailability);

		imageViewMap = (ImageView) findViewById(R.id.imageViewMap);
		imageViewMap.setOnClickListener(this);
		imageViewBikes = (ImageView) findViewById(R.id.imageViewBikes);
		imageViewParking = (ImageView) findViewById(R.id.imageViewParking);
		viewClosed = findViewById(R.id.viewClosed);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		if(savedInstanceState==null){
			getApp().trackPageView("/detail");
			getApp().trackEvent("opendetail", getApp().getCurrentSystem().getSystemId(), stationId);
		}
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		bindViews();
	}

	@Override
	protected void onResume() {
		super.onResume();
		GetSpecificStationsTask task = new GetSpecificStationsTask(getApp().getCurrentSystem(), getApp().getLanguage(), stationId);
		task.setOnStationsTaskCompletedListener(this);
		task.execute();
		setProgressBarIndeterminateVisibility(true);
	}
	
	protected class ImageTask extends AsyncTask<String, Void, Bitmap> {
		protected Bitmap doInBackground(String... params) {
			try {
				return BitmapFactory.decodeStream((InputStream) new URL(
						params[0]).getContent());
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}

		};

		protected void onPostExecute(Bitmap result) {
			if (result != null) {
				imageViewMap.setImageBitmap(result);
			}
		};
	};
	
	protected class GeocodingTask extends AsyncTask<Double,Void,Address> {
		@Override
		protected Address doInBackground(Double... params) {
			try{
				Double latitude = params[0];
				Double longitude = params[1];
				
				Geocoder geocoder = new Geocoder(DetailActivity.this, new Locale(VilloHelperApplication.getInstance().getLanguage()));
				List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
				return addresses.get(0);
			}
			catch(IOException e){
				e.printStackTrace();
				return null;
			}
			
		}
		
		@Override
		protected void onPostExecute(Address result) {
			if(result!=null){
				textViewAddress.setText(result.getAddressLine(0)+"\n"+result.getAddressLine(1));
			}
			else{
				textViewAddress.setText(R.string.label_not_available);
			}
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()==android.R.id.home){
			finish();
			return true;
		}
		else if(item.getItemId()==R.id.menuFavorite){
			getApp().getDataHelper().toggleFavorite(stationId, getApp().getCurrentSystem().getSystemId());
			bindFavorites();
			getApp().trackEvent("click", "favorite", "favorite");
			return true;
		}
		else if(item.getItemId()==R.id.menuDirections){
			Intent i = new Intent(Intent.ACTION_VIEW,Uri.parse("http://maps.google.com/maps?daddr="+mLatitude+","+mLongitude));
			getApp().trackEvent("click", "directions", "directions");
			startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void bindViews(){
		Cursor c = getApp().getDataHelper().getStationById(getApp().getCurrentSystem().getSystemId(), stationId);
		if(c.getCount()!=0){
			c.moveToFirst();
			mLatitude = c.getDouble(c.getColumnIndex(Station.LATITUDE));
			mLongitude = c.getDouble(c.getColumnIndex(Station.LONGITUDE));
			int bikes = c.getInt(c.getColumnIndex(Station.BIKES));
			int parking = c.getInt(c.getColumnIndex(Station.PARKING));
			int open = c.getInt(c.getColumnIndex(Station.OPEN));
			int ticket = c.getInt(c.getColumnIndex(Station.TICKET));
			
			GeocodingTask task = new GeocodingTask();
			task.execute(mLatitude,mLongitude);
			
			imageViewMap.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				
				@Override
				public void onGlobalLayout() {
					if(!mapLayoutDone){
						String url = "http://maps.googleapis.com/maps/api/staticmap?center="+mLatitude+","+mLongitude+"&zoom=16&scale=2&size="+imageViewMap.getMeasuredWidth()/2+"x"+imageViewMap.getMeasuredHeight()/2+"&maptype=roadmap&markers=color:blue%7Clabel:A%7C"+mLatitude+","+mLongitude+"&sensor=false&api=AIzaSyAKk716dcqiDOAfujkD5mPJw5FnbflnE40";
						ImageTask imageTask = new ImageTask();
						imageTask.execute(url);
						Log.v(getClass().getName(), "imageViewWidth: "
								+ imageViewMap.getMeasuredWidth());
						mapLayoutDone=true;
					}
				}
			});
			
			textViewStationName.setText(c.getString(c.getColumnIndex(Station.NAME)).toUpperCase());
			
			imageViewParking.setImageLevel(Availability.getLevel(parking).ordinal());
			imageViewBikes.setImageLevel(Availability.getLevel(bikes).ordinal());
			
			if(bikes==0){
				textViewBikes.setText(R.string.label_bike_availability_none);
			}
			else if(bikes==1){
				textViewBikes.setText(R.string.label_bike_availability_one);
			}
			else if(bikes>1){
				textViewBikes.setText(String.format(getResources().getString(R.string.label_bike_availability_multiple),bikes));
			}
			
			if(parking==0){
				textViewParking.setText(R.string.label_parking_availability_none);
			}
			else if(parking==1){
				textViewParking.setText(R.string.label_parking_availability_one);
			}
			else if(parking>1){
				textViewParking.setText(String.format(getResources().getString(R.string.label_parking_availability_multiple),parking));
			}
			
			viewClosed.setVisibility(open==1?View.GONE:View.VISIBLE);
			layoutAvailability.setVisibility(open==1?View.VISIBLE:View.GONE);
			textViewClosed.setVisibility(open==1?View.GONE:View.VISIBLE);
			
			textViewCreditCard.setVisibility(ticket==1?View.VISIBLE:View.GONE);
		}
		c.close();
		invalidateOptionsMenu();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_detail, menu);
		menuItemFavorite = menu.getItem(1);
		bindFavorites();
		return super.onCreateOptionsMenu(menu);
	}
	
	private void bindFavorites(){
		if(menuItemFavorite!=null && stationId!=null){
			if(getApp().getDataHelper().isFavorite(stationId, getApp().getCurrentSystem().getSystemId())){
				menuItemFavorite.setIcon(R.drawable.ic_rating_important);
				menuItemFavorite.setTitle(R.string.action_add_to_favorite);
			}
			else{
				menuItemFavorite.setIcon(R.drawable.ic_rating_not_important);
				menuItemFavorite.setTitle(R.string.action_remove_from_favorite);
			}
		}
	}

	@Override
	public void onClick(View v) {
		final String uri = "http://maps.google.com/?q=" + mLatitude + "," + mLongitude;
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
	}

	@Override
	public void onStationsTaskCompleted(GetAllStationsTask task) {
		setProgressBarIndeterminateVisibility(false);
		bindViews();
	}

	@Override
	public void onStationsTaskFailed(GetAllStationsTask task,
			ErrorType errorType) {
		setProgressBarIndeterminateVisibility(false);
		if(errorType.equals(ErrorType.INVALID_DATA)){
			AlertDialogFragment fragment = AlertDialogFragment.newInstance(AlertDialogFragment.TYPE_ERROR_PARSING);
			fragment.show(getSupportFragmentManager(), "error");
		}else if(errorType.equals(ErrorType.CONNECTION_ERROR)){
			AlertDialogFragment fragment = AlertDialogFragment.newInstance(AlertDialogFragment.TYPE_ERROR_CONNECTING);
			fragment.show(getSupportFragmentManager(), "error");
		}
	}
	
	

}
