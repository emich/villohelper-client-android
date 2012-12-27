package be.emich.labs.villohelper.activity;

import android.os.Bundle;
import android.view.Window;
import be.emich.labs.villohelper.application.VilloHelperApplication;

import com.actionbarsherlock.app.SherlockFragmentActivity;

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
