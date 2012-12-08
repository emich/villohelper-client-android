package be.emich.labs.villohelper.fragment;

import be.emich.labs.villohelper.application.VilloHelperApplication;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class VilloHelperFragment extends Fragment {
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
