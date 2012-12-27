package be.emich.labs.villohelper.map;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import be.emich.labs.villohelper.application.VilloHelperApplication;
import be.emich.labs.villohelper.parser.Station;
import be.emich.labs.villohelper.util.Availability;
import be.emich.labs.villohelper.util.Log;
import be.emich.villo.R;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;


public class AvailabilityInfoWindowAdapter implements GoogleMap.InfoWindowAdapter{

	private Typeface typeFaceThin;
	private Typeface typeFaceRegular;
	private Typeface typeFaceLight;
	private Typeface typeFaceBold;
	
	private Context mContext;
	
	public AvailabilityInfoWindowAdapter(Context context) {
		mContext = context;
		
		typeFaceThin = Typeface.createFromAsset(mContext.getAssets(),
				"fonts/Roboto-Thin.ttf");
		typeFaceRegular = Typeface.createFromAsset(mContext.getAssets(),
				"fonts/Roboto-Regular.ttf");
		typeFaceLight = Typeface.createFromAsset(mContext.getAssets(),
				"fonts/Roboto-Light.ttf");
		typeFaceBold = Typeface.createFromAsset(mContext.getAssets(),
				"fonts/Roboto-Bold.ttf");
	}
	
	@Override
	public View getInfoContents(Marker marker) {
		Log.v(getClass().getName(),"InfoWindowAdapter : "+marker);
		return null;
	}
	
	@Override
	public View getInfoWindow(Marker marker) {
		String stationId = marker.getSnippet();
		String systemId = VilloHelperApplication.getInstance().getCurrentSystem().getSystemId();
		
		Cursor c = VilloHelperApplication.getInstance().getDataHelper().getStationById(systemId, stationId);
		
		c.moveToFirst();
		String stationName = c.getString(c.getColumnIndex(Station.NAME));
		String id = c.getString(c.getColumnIndex(Station.ID));
		int bikes = c.getInt(c.getColumnIndex(Station.BIKES));
		int parking = c.getInt(c.getColumnIndex(Station.PARKING));
		boolean open = c.getInt(c.getColumnIndex(Station.OPEN))==1;
		boolean ticket = c.getInt(c.getColumnIndex(Station.TICKET))==1;
		c.close();
		
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.overlay_station, null, false);
		TextView textViewStationName = (TextView)v.findViewById(R.id.textViewStationName);
		TextView textViewBikes = (TextView)v.findViewById(R.id.textViewBikes);
		TextView textViewParking = (TextView)v.findViewById(R.id.textViewParking);
		ImageView imageViewBikes = (ImageView)v.findViewById(R.id.imageViewBikes);
		ImageView imageViewParking = (ImageView)v.findViewById(R.id.imageViewParking);
		int color = mContext.getResources().getColor(R.color.regular);
		textViewStationName.setText(stationName);
		textViewStationName.setTypeface(typeFaceBold);
		textViewStationName.setTextColor(color);
		textViewBikes.setText(bikes+"");
		textViewBikes.setTypeface(typeFaceRegular);
		textViewBikes.setTextColor(color);
		textViewParking.setText(parking+"");
		textViewParking.setTypeface(typeFaceRegular);
		textViewParking.setTextColor(color);
		imageViewBikes.setImageLevel(Availability.getLevel(bikes).ordinal());
		imageViewParking.setImageLevel(Availability.getLevel(parking).ordinal());
		
		Log.v(getClass().getName(),"InfoWindowAdapter : "+marker);
		return v;
	}
}
