package com.rj.instagram;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.rj.instagram.MainActivity.ResponseListener;



public class DisplayActivity extends Activity{
	private static final String TAG = "RJ";

	String accessToken, userid;	
	public static final String APIURL = "https://api.instagram.com/v1";

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);		
		SharedPreferences sp = getApplication().getApplicationContext().getSharedPreferences("stored", MODE_PRIVATE);
		setContentView(R.layout.activity_display);
		
		accessToken = sp.getString(ResponseListener.EXTRA_ACCESS_TOKEN, "");
		userid = sp.getString(ResponseListener.EXTRA_USERID, "");
		Log.e(TAG, "Token is " + accessToken + " userid is " + userid);
		
		
		String urlString = APIURL + "/users/"+ userid +"/media/recent/?access_token=" + accessToken;
		Log.e(TAG, "URL string is " + urlString);
		DisplayAsyncTask das = new DisplayAsyncTask(DisplayActivity.this);
		das.execute(urlString);
	}
}
