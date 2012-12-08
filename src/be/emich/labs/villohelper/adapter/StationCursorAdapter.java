package be.emich.labs.villohelper.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import be.emich.labs.villohelper.application.VilloHelperApplication;
import be.emich.labs.villohelper.parser.Station;
import be.emich.labs.villohelper.util.Util;
import be.emich.villo.R;


public class StationCursorAdapter extends SimpleStationCursorAdapter {
	private Typeface typefaceThin;
	private Typeface typefaceBoldCondensed;
	private Typeface typefaceRegular;

	public StationCursorAdapter(Context ctx, Cursor c) {
		super(ctx, c);
		typefaceThin = Typeface.createFromAsset(ctx.getAssets(),
				"fonts/Roboto-Thin.ttf");
		typefaceBoldCondensed = Typeface.createFromAsset(ctx.getAssets(),
				"fonts/Roboto-Bold.ttf");
		typefaceRegular = Typeface.createFromAsset(ctx.getAssets(),
				"fonts/Roboto-Regular.ttf");
	}

	@Override
	public int getLayout() {
		return R.layout.item_station_full;
	}

	@Override
	public void bindView(View view, Context ctx, Cursor c) {
		super.bindView(view, ctx, c);
		TextView tvBikes = (TextView) view
				.getTag(R.id.textViewStationAvailableBikes);
		TextView tvParking = (TextView) view
				.getTag(R.id.textViewStationAvailableParking);
		TextView tvDistance = (TextView) view
				.getTag(R.id.textViewStationDistance);
		
		int bikeAvailability = c.getInt(c.getColumnIndex(Station.BIKES));
		int parkingAvailability = c.getInt(c.getColumnIndex(Station.PARKING));
		
		tvBikes.setText(bikeAvailability+"");
		tvParking.setText(parkingAvailability+"");
		tvDistance.setText(Util.convertKilometerDistanceToString(c.getDouble(c
				.getColumnIndex(Station.DISTANCE))));

		if (VilloHelperApplication.getInstance().isCheckedIn()) {
			tvBikes.setTypeface(typefaceThin);
			tvBikes.setTextColor(mContext.getResources().getColor(R.color.availability_regular));
			tvParking.setTypeface(typefaceRegular);
			setTextViewColor(tvParking, parkingAvailability);
			
		} else {
			tvBikes.setTypeface(typefaceRegular);
			setTextViewColor(tvBikes, bikeAvailability);
			tvParking.setTypeface(typefaceThin);
			tvParking.setTextColor(mContext.getResources().getColor(R.color.availability_regular));
		}
	}

	@Override
	public View newView(Context ctx, Cursor c, ViewGroup viewGroup) {
		View v = super.newView(ctx, c, viewGroup);
		TextView tvBikes = (TextView) v
				.findViewById(R.id.textViewStationAvailableBikes);
		// tvBikes.setTypeface(typefaceThin);
		/*
		 * TextView tvParking =
		 * (TextView)v.findViewById(R.id.textViewStationAvailableParking);
		 * tvParking.setTypeface(typefaceThin);
		 */
		TextView tvName = (TextView) v.findViewById(R.id.textViewStationName);
		tvName.setTypeface(typefaceBoldCondensed);
		v.setTag(R.id.textViewStationAvailableBikes, tvBikes);
		v.setTag(R.id.textViewStationAvailableParking,
				v.findViewById(R.id.textViewStationAvailableParking));
		v.setTag(R.id.textViewStationDistance,
				v.findViewById(R.id.textViewStationDistance));
		return v;
	}

	public void setTextViewColor(TextView textView, int availability) {
		if (availability <= 2) {
			textView.setTextColor(mContext.getResources().getColor(R.color.availability_red));
			return;
		} else if (availability <= 5) {
			textView.setTextColor(mContext.getResources().getColor(R.color.availability_orange));
			return;
		} else {
			textView.setTextColor(mContext.getResources().getColor(R.color.availability_green));
			return;
		}
	}
}
