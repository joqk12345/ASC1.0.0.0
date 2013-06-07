package com.asc.app.ui;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;

import com.asc.app.R;
import com.asc.app.ui.base.BaseActivity;

public class WelcomeActivity extends BaseActivity{
	/** Called when the activity is first created. */
	
	private Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		View view=View.inflate(this, R.layout.welcome, null);
		setContentView(view);
		
		Animation animation=AnimationUtils.loadAnimation(this, R.anim.alpha);
		view.startAnimation(animation);
		animation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {}
			@Override
			public void onAnimationRepeat(Animation arg0) {}
			
			@Override
			public void onAnimationEnd(Animation arg0) {
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						goHome();
					}
				}, 2000);
			}
		});
	
		
	}

	protected void onResume() {
		super.onResume();
	}

	private void goHome() {
		openActivity(ASCActivity.class);
		defaultFinish();
	};
	
	/*@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.welcome);
		new Handler().postDelayed(new Runnable() {
			public void run() {
				Intent intent = new Intent(WelcomeActivity.this, ASCActivity.class);
				startActivity(intent);
				WelcomeActivity.this.finish();
			}
		}, 2000);
	}*/
}