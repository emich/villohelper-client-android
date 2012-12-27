package be.emich.labs.villohelper.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.SectionIndexer;
import android.widget.TextView;
import be.emich.labs.villohelper.parser.Station;
import be.emich.villo.R;


public class SimpleStationCursorAdapter extends CursorAdapter implements SectionIndexer {

	protected Context mContext;
	
	protected AlphabetIndexer mAlphabetIndexer;
	
	public SimpleStationCursorAdapter(Context ctx,Cursor c) {
		super(ctx,c,CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		
		mContext=ctx;
	}
	
	@Override
	public Cursor swapCursor(Cursor newCursor) {
		if(newCursor!=null && mAlphabetIndexer==null){
		mAlphabetIndexer = new AlphabetIndexer(newCursor,
                newCursor.getColumnIndex(Station.NAME),
                " ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		}
		mAlphabetIndexer.setCursor(newCursor);
		return super.swapCursor(newCursor);
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
	
	@Override
	public int getPositionForSection(int section) {
		return mAlphabetIndexer.getPositionForSection(section);
	}
	
	@Override
	public int getSectionForPosition(int position) {
		return mAlphabetIndexer.getSectionForPosition(position);
	}
	
	@Override
	public Object[] getSections() {
		return mAlphabetIndexer.getSections();
	}

}
