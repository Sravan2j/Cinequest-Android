package edu.sjsu.cinequest;
//credit goes to Android Application Development Cookbook, Wei-Meng Lee
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class PostOnFacebook extends Activity
{
	private WebView postOnFacebook;
	private String facebookURL;
	public void onCreate(Bundle savedInstanceState) {    	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.facebookviewport);
		postOnFacebook = (WebView) findViewById(R.id.WebView01);
		facebookURL = getIntent().getExtras().getString("facebookURL");
		postOnFacebook.setWebViewClient(new WebViewClient() { @Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
			return (false); }
		});
		WebSettings webSettings = postOnFacebook.getSettings();
		webSettings.setBuiltInZoomControls(false);
		postOnFacebook.loadUrl(facebookURL);
		postOnFacebook.goBackOrForward(1);
	}
}