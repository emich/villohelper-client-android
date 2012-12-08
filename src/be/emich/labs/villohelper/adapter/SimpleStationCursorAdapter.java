package be.emich.labs.villohelper.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import be.emich.labs.villohelper.parser.Station;
import be.emich.villo.R;


public class SimpleStationCursorAdapter extends CursorAdapter {

	protected Context mContext;
	
	public SimpleStationCursorAdapter(Context ctx,Cursor c) {
		super(ctx,c,CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		this.mContext=ctx;
	}
	
	@Override
	public void bindView(View view, Context ctx, Cursor c) {
		TextView textViewStationName = (TextView)view.getTag(R.id.textViewStationName);
		
		textViewStationName.setText(c.getString(c.getColumnIndex(Station.NAME)));
	}

	@Override
	public View newView(Context ctx, Cursor c, ViewGroup viewGroup) {
		View v = LayoutInflater.from(ctx).inflate(getLayout(), viewGroup,false);
		v.setTag(R.id.textViewStationName,v.findViewById(R.id.textViewStationName));
		return v;
	}
	
	public int getLayout(){
		return R.layout.item_station;
	}

}
