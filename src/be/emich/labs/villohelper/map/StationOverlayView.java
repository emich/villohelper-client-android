package be.emich.labs.villohelper.map;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import be.emich.labs.villohelper.util.Availability;
import be.emich.villo.R;

import com.google.android.maps.OverlayItem;
import com.readystatesoftware.mapviewballoons.BalloonOverlayView;

public class StationOverlayView<Item extends OverlayItem> extends BalloonOverlayView<StationOverlayItem> {
	private TextView textViewStationName;
	private TextView textViewBikes;
	private TextView textViewParking;
	private ImageView imageViewBikes;
	private ImageView imageViewParking;
	
	private Typeface typeFaceThin;
	private Typeface typeFaceRegular;
	private Typeface typeFaceLight;
	private Typeface typeFaceBold;
	
	private Context mContext;

	
	public StationOverlayView(Context c, int balloonBottomOffset) {
		super(c, balloonBottomOffset);
		typeFaceThin = Typeface.createFromAsset(c.getAssets(),
				"fonts/Roboto-Thin.ttf");
		typeFaceRegular = Typeface.createFromAsset(c.getAssets(),
				"fonts/Roboto-Regular.ttf");
		typeFaceLight = Typeface.createFromAsset(c.getAssets(),
				"fonts/Roboto-Light.ttf");
		typeFaceBold = Typeface.createFromAsset(c.getAssets(),
				"fonts/Roboto-Bold.ttf");
		mContext = c;
	}
	
	@Override
	protected void setupView(Context context, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.overlay_station, parent);
		textViewStationName = (TextView)v.findViewById(R.id.textViewStationName);
		textViewBikes = (TextView)v.findViewById(R.id.textViewBikes);
		textViewParking = (TextView)v.findViewById(R.id.textViewParking);
		imageViewBikes = (ImageView)v.findViewById(R.id.imageViewBikes);
		imageViewParking = (ImageView)v.findViewById(R.id.imageViewParking);
	}
	
	@Override
	protected void setBalloonData(StationOverlayItem item, ViewGroup parent) {
		int color = mContext.getResources().getColor(R.color.regular);
		textViewStationName.setText(item.getStationName());
		textViewStationName.setTypeface(typeFaceBold);
		textViewStationName.setTextColor(color);
		textViewBikes.setText(item.getBikes()+"");
		textViewBikes.setTypeface(typeFaceRegular);
		textViewBikes.setTextColor(color);
		textViewParking.setText(item.getParking()+"");
		textViewParking.setTypeface(typeFaceRegular);
		textViewParking.setTextColor(color);
		imageViewBikes.setImageLevel(Availability.getLevel(item.getBikes()).ordinal());
		imageViewParking.setImageLevel(Availability.getLevel(item.getParking()).ordinal());
	}
	
	
}
