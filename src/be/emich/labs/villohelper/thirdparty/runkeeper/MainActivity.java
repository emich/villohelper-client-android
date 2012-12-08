package be.emich.labs.villohelper.thirdparty.runkeeper;

import java.io.StringWriter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import be.emich.labs.villohelper.thirdparty.runkeeper.model.PathEventType;
import be.emich.labs.villohelper.thirdparty.runkeeper.model.PathItem;
import be.emich.villo.R;


import android.app.Activity;
import android.os.Bundle;
import android.util.JsonWriter;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {

	private Button mButtonAuthorize;
	private WebView mWebView;
	private OAuth mSite;
	private OAuthWebViewClient mWebViewClient;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //mButtonAuthorize = (Button)findViewById(R.id.buttonAuthorize);
        mButtonAuthorize.setOnClickListener(this);
        
        mWebViewClient = new OAuthWebViewClient();
        
        //mWebView = (WebView)findViewById(R.id.webview);
        mWebView.setWebViewClient(mWebViewClient);
        
        mSite = OAuth.RUNKEEPER;
        
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

	@Override
	public void onClick(View v) {
		mWebView.loadUrl(mSite.getAuthorizationUrl());
	}
	
	private class OAuthWebViewClient extends WebViewClient {
		@Override
		public void onLoadResource(WebView view, String url) {
			if(url.contains(mSite.getRedirectUrl())){
				view.stopLoading();
			}
			super.onLoadResource(view, url);
		}
	}
}
