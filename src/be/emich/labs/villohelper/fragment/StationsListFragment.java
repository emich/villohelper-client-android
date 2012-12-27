package be.emich.labs.villohelper.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import be.emich.labs.villohelper.activity.DetailActivity;
import be.emich.labs.villohelper.adapter.SimpleStationCursorAdapter;
import be.emich.labs.villohelper.application.VilloHelperApplication;
import be.emich.labs.villohelper.util.IntentUtil;
import be.emich.labs.villohelper.util.Log;
import be.emich.villo.R;


public class StationsListFragment extends VilloHelperListFragment implements LoaderCallbacks<Cursor>, OnSharedPreferenceChangeListener{
	public static final String EXTRA_MODE = IntentUtil.EXTRA_MODE;
	
	public static final int MODE_ALL = 0;
	public static final int MODE_NEARBY = 1;
	public static final int MODE_FAVORITES = 2;
	
	private SimpleStationCursorAdapter mAdapter;
	private int mMode;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_stationlist, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mMode = getArguments().getInt(EXTRA_MODE);
		
		mAdapter = getAdapter();
		setListAdapter(mAdapter);

		getLoaderManager().initLoader(mMode, null, this);
	}
	
	protected SimpleStationCursorAdapter getAdapter(){
		return new SimpleStationCursorAdapter(getActivity(), null);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle arg1) {
		Log.v(getClass().getName(),"onCreateLoader");
		switch(mMode){
		case MODE_NEARBY:
			return getApp().getDataHelper().getLoaderForClosestStations();
		case MODE_FAVORITES:
			return getApp().getDataHelper().getLoaderForFavoriteStations();
		}
		return getApp().getDataHelper().getLoaderForAllStations();
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		Log.v(getClass().getName(),"onLoadFinished");
		if(cursor==null){
			Log.v(getClass().getName(),"onLoadFinished - null cursor");
		}
		else{
			Log.v(getClass().getName(),"onLoadFinished - "+cursor.getCount()+" rows");
		}
		mAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		Log.v(getClass().getName(),"onLoaderReset");
		mAdapter.swapCursor(null);
	}
	
	@Override
	public void onResume() {
		PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(this);
		super.onResume();
	}
	
	@Override
	public void onPause() {
		PreferenceManager.getDefaultSharedPreferences(getActivity()).unregisterOnSharedPreferenceChangeListener(this);
		super.onPause();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if(key.equals(VilloHelperApplication.IS_CHECKED_IN)){
			((CursorAdapter)getListAdapter()).notifyDataSetChanged();
		}
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		String stationId = getApp().getDataHelper().getStationIdByRowId(getApp().getCurrentSystem().getSystemId(), id+"");
		
		
		Intent i = new Intent(getActivity(),DetailActivity.class);
		i.putExtra(DetailActivity.EXTRA_ID, stationId);
		startActivity(i);
	}
}
