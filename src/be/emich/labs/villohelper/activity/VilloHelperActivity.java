package be.emich.labs.villohelper.activity;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import be.emich.labs.villohelper.application.VilloHelperApplication;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

public class VilloHelperActivity extends SherlockFragmentActivity {
	private VilloHelperApplication app;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		app = VilloHelperApplication.getInstance();
		
	}
	
	public VilloHelperApplication getApp() {
		return app;
	}
	

}
