package be.emich.labs.villohelper.fragment;

import android.os.Bundle;
import be.emich.labs.villohelper.application.VilloHelperApplication;

import com.actionbarsherlock.app.SherlockListFragment;

public class VilloHelperListFragment extends SherlockListFragment {
	private VilloHelperApplication app;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = VilloHelperApplication.getInstance();
	}
	
	public VilloHelperApplication getApp() {
		return app;
	}
}
