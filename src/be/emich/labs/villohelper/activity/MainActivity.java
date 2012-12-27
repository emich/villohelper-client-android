package be.emich.labs.villohelper.activity;

import java.util.Calendar;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import be.emich.labs.villohelper.exception.ErrorType;
import be.emich.labs.villohelper.fragment.AlertDialogFragment;
import be.emich.labs.villohelper.fragment.AlertDialogFragment.AlertDialogListener;
import be.emich.labs.villohelper.fragment.ClosestStationsListFragment;
import be.emich.labs.villohelper.fragment.FavoriteStationsListFragment;
import be.emich.labs.villohelper.fragment.StationsListFragment;
import be.emich.labs.villohelper.task.GetAllStationsTask;
import be.emich.labs.villohelper.task.GetAllStationsTask.OnStationsTaskCompletedListener;
import be.emich.labs.villohelper.util.Log;
import be.emich.villo.R;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.viewpagerindicator.TabPageIndicator;

public class MainActivity extends VilloHelperActivity implements OnStationsTaskCompletedListener, OnPageChangeListener, AlertDialogListener {

	private GetAllStationsTask taskAllStations;
	private VilloPagerAdapter pagerAdapter;
	private ViewPager viewPager;
	private TabPageIndicator indicator;
	
	private Fragment fragmentAllStations;
	private ClosestStationsListFragment fragmentNearbyStations;
	private Fragment fragmentFavoriteStations;
	
	private MenuItem mMenuItemPlayStop;
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_main);
		
		viewPager = (ViewPager)findViewById(R.id.viewPager);
		pagerAdapter = new VilloPagerAdapter();
		viewPager.setAdapter(pagerAdapter);
		indicator = (TabPageIndicator)findViewById(R.id.indicator);
		indicator.setViewPager(viewPager);
		indicator.setOnPageChangeListener(this);
		
		bindViews();
		
		if(bundle==null){
			getApp().trackPageView("/home");
		}
		
		if(getApp().mustSync()){
			taskAllStations = new GetAllStationsTask(getApp().getCurrentSystem().getSystemId(),getApp().getLanguage());
			taskAllStations.setOnStationsTaskCompletedListener(this);
			taskAllStations.execute();
			setProgressBarIndeterminateVisibility(true);
		}
	}
	
	private void bindViews(){
	}

	@Override
	public void onStationsTaskCompleted(GetAllStationsTask task) {
		Log.v(getClass().getName(),"Station task completed.");
		setProgressBarIndeterminateVisibility(false);
		getApp().setDoneFirstSync(true);
		getApp().setLastSyncDate(Calendar.getInstance().getTime());
		bindViews();
	}

	@Override
	public void onStationsTaskFailed(GetAllStationsTask task,
			ErrorType errorType) {
		Log.v(getClass().getName(),"Station task failed.");
		setProgressBarIndeterminateVisibility(false);
		if(getSupportFragmentManager().findFragmentByTag("error")==null){
			if(errorType.equals(ErrorType.INVALID_DATA)){
				AlertDialogFragment fragment = AlertDialogFragment.newInstance(AlertDialogFragment.TYPE_ERROR_PARSING);
				fragment.show(getSupportFragmentManager(), "error");
			}else if(errorType.equals(ErrorType.CONNECTION_ERROR)){
				AlertDialogFragment fragment = AlertDialogFragment.newInstance(AlertDialogFragment.TYPE_ERROR_CONNECTING);
				fragment.show(getSupportFragmentManager(), "error");
			}
		}
	}
	
	private class VilloPagerAdapter extends FragmentPagerAdapter {
		
		public VilloPagerAdapter() {
			super(getSupportFragmentManager());
			
			fragmentAllStations = new StationsListFragment();
			Bundle argumentsAllStations = new Bundle();
			argumentsAllStations.putInt(StationsListFragment.EXTRA_MODE, StationsListFragment.MODE_ALL);
			fragmentAllStations.setArguments(argumentsAllStations);
			
			fragmentNearbyStations = new ClosestStationsListFragment();
			Bundle argumentsNearbyStations = new Bundle();
			argumentsNearbyStations.putInt(StationsListFragment.EXTRA_MODE, StationsListFragment.MODE_NEARBY);
			fragmentNearbyStations.setArguments(argumentsNearbyStations);
			
			fragmentFavoriteStations = new FavoriteStationsListFragment();
			Bundle argumentsFavoriteStations = new Bundle();
			argumentsFavoriteStations.putInt(StationsListFragment.EXTRA_MODE, StationsListFragment.MODE_FAVORITES);
			fragmentFavoriteStations.setArguments(argumentsFavoriteStations);
		}
		
		@Override
		public int getCount() {
			return 3;
		}
		
		@Override
		public Fragment getItem(int page) {
			switch(page){
			case 0:
				return fragmentNearbyStations;
			case 1:
				return fragmentFavoriteStations;
			case 2:
				return fragmentAllStations;
			}
			return fragmentAllStations;
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			int stringId = 0;
			switch(position){
			case 0:
				stringId = R.string.tab_nearby;
				break;
			case 1:
				stringId = R.string.tab_favs;
				break;
			case 2:
				stringId = R.string.tab_all;
				break;
			}
			return getResources().getString(stringId).toUpperCase();

		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int position) {
		//fragmentNearbyStations.reloadClosestStations();
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_main, menu);
        mMenuItemPlayStop = menu.getItem(1);
        bindOptionsMenu();
        return true;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		bindOptionsMenu();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()==R.id.menuStart){
			getApp().setCheckedIn(!getApp().isCheckedIn());
			bindOptionsMenu();
			if(getApp().isCheckedIn()){getApp().trackEvent("click", "checkinStop", "checkinStop");}
			else{getApp().trackEvent("click", "checkinStart", "checkinStart");}
		}
		else if(item.getItemId()==R.id.menuMap){
			Intent i = new Intent(this,NearbyMapActivity.class);
			startActivity(i);
		}
		else if(item.getItemId()==R.id.menuAbout){
			AlertDialogFragment dialogFragment = AlertDialogFragment.newInstance(AlertDialogFragment.TYPE_ABOUT);
			dialogFragment.show(getSupportFragmentManager(), "about");
			getApp().trackEvent("click", "about", "about");
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void bindOptionsMenu(){
		if(mMenuItemPlayStop!=null){
			if(getApp().isCheckedIn()){
				mMenuItemPlayStop.setIcon(R.drawable.ic_stop);
				mMenuItemPlayStop.setTitle(getResources().getString(R.string.action_stop));
				
			}
			else{
				mMenuItemPlayStop.setIcon(R.drawable.ic_play);
				mMenuItemPlayStop.setTitle(getResources().getString(R.string.action_start));
			}
		}
	}

	@Override
	public void doPositiveClick() {
	}

	@Override
	public void doNegativeClick() {
		Intent intent = new Intent(Intent.ACTION_VIEW); 
		intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=be.emich.villo")); 
		startActivity(intent);
		getApp().trackEvent("click", "rating", "rating");
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		//Nothing to do in this case;
	}

	@Override
	public void doNeutralClick() {
		//Nothing to see, move along...
	}
	
}
