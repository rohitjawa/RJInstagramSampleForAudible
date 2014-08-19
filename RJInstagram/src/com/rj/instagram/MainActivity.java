package com.rj.instagram;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
	
	private Button mButton;
	private InstaImpl mInstaImpl;
	private Context mContext;
	private ResponseListener mResponseListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_main);
		Button viewImagesBtn = (Button) findViewById(R.id.displayActivityLaunchButton);
		SharedPreferences sp = getApplication().getApplicationContext().getSharedPreferences("stored", MODE_PRIVATE);
		if(sp!=null)
		{
			viewImagesBtn.setEnabled(true);
		}
		else
		{
			viewImagesBtn.setEnabled(false);
			Toast.makeText(this, "Login to Instagram first", Toast.LENGTH_LONG).show();
			
		}
		
		mButton = (Button)findViewById(R.id.registrationButton);
		mButton.setOnClickListener(this);
		mResponseListener = new ResponseListener();
	}


	@Override
	public void onClick(View view) {
		switch(view.getId())
		{
			case R.id.registrationButton:
				mInstaImpl = new InstaImpl(this);
				mInstaImpl.setAuthAuthenticationListener(new AuthListener());
				break;
				
			case R.id.displayActivityLaunchButton:
				Intent in = new Intent(this, DisplayActivity.class);
				startActivity(in);	
		}
		
	}
	
	public class AuthListener implements com.rj.instagram.InstaImpl.AuthAuthenticationListener
	{
		@Override
		public void onSuccess() {
			Toast.makeText(MainActivity.this, "Instagram Authorization Successful", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onFail(String error) {
			Toast.makeText(MainActivity.this, "Authorization Failed", Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter filter = new IntentFilter();
        filter.addAction("com.rj.instagram.responselistener");
        filter.addCategory("com.rj.instagram");
        registerReceiver(mResponseListener, filter);
	}
	
	@Override
	protected void onPause() {
		unregisterReceiver(mResponseListener);
		super.onPause();
	}
	
	public class ResponseListener extends BroadcastReceiver {
		
		public static final String ACTION_RESPONSE = "com.rj.instagram.responselistener";
		public static final String EXTRA_NAME = "name";
		public static final String EXTRA_ACCESS_TOKEN = "token";
		public static final String EXTRA_USERID = "userid";

		@Override
		public void onReceive(Context context, Intent intent) {
			mInstaImpl.dismissDialog();
			Bundle extras = intent.getExtras();
			String name = extras.getString(EXTRA_NAME);
			String accessToken = extras.getString(EXTRA_ACCESS_TOKEN);
			String userid = extras.getString(EXTRA_USERID);
			final AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
			alertDialog.setTitle("Your Details");
			alertDialog.setMessage("Name - " + name + ", Access Token - " + accessToken);
			alertDialog.setPositiveButton("Ok", null);
			alertDialog.show();
			
			SharedPreferences sp = getApplication().getApplicationContext().getSharedPreferences("stored", MODE_PRIVATE);
			SharedPreferences.Editor spe = sp.edit();
			spe.putString(EXTRA_ACCESS_TOKEN, accessToken);
			spe.putString(EXTRA_USERID, userid);
			spe.commit();
		}
	}
}
