package com.asc.app.ui;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.asc.app.R;

/**
 * @author zhanglei
 *
 */
public class ADActivity extends Activity {
	private WebView webView;
	private ProgressBar progressBar;
	private TextView adTitle;
	private final static String LOG_TAG = "ADActivity";
	private boolean isErrorPage = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.advertise);
		
//		this.stopService(new Intent(this, AdvertiseService.class));
		String adUrl = this.getIntent().getExtras().getString("adUrl");
		String location = this.getIntent().getExtras().getString("location");
		Log.e(LOG_TAG, adUrl + " " + location);

		NotificationManager notifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notifyManager.cancel(R.string.app_name);

		adTitle = (TextView) findViewById(R.id.ad_title);
		adTitle.setText("您所在的位置：" + location);
		
		progressBar = (ProgressBar) findViewById(R.id.progress_bar);
		progressBar.setIndeterminate(false);
		progressBar.setMax(100);
		progressBar.setProgress(0);

		webView = (WebView) findViewById(R.id.ad_webview);
		webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		webView.getSettings().setJavaScriptEnabled(true);  
		webView.loadUrl(adUrl);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
				Log.e(LOG_TAG, "sslError: " + error.SSL_EXPIRED);
				handler.proceed();
			}
			
			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				webView.loadUrl("file:///android_asset/error.xhtml");
				isErrorPage = true;
				super.onReceivedError(view, errorCode, description, failingUrl);
			}
			
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int progress) {
				if (progress == 100) {
					progressBar.setProgress(0);
					progressBar.setVisibility(View.GONE);
					adTitle.setVisibility(View.VISIBLE);
					return;
				}
				progressBar.setVisibility(View.VISIBLE);
				progressBar.setProgress(progress);
			}
		});
		
		Button button = (Button) findViewById(R.id.return_btn);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack() && !isErrorPage) {
			webView.goBack();
			return true;
		}else {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				ADActivity.this.finish();
				return true;
			}else {
				return super.onKeyDown(keyCode, event);
			}
		}
	}
}