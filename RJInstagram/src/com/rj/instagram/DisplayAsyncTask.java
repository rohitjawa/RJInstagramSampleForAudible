package com.rj.instagram;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.squareup.picasso.Picasso;

public class DisplayAsyncTask extends AsyncTask<String, String, ArrayList<String>>{
	private static final String TAG = "RJ";
	URL url;
	InputStream inputStream;
	ArrayList<String> imageUrlStrings = new ArrayList<String>();
	Activity activity;
	
	public DisplayAsyncTask(Activity a)
	{
		activity = a;
	}
	
	@Override
	protected ArrayList<String> doInBackground(String... params) {
		try {
			String urlString = params[0];
			url = new URL(urlString);
			inputStream = url.openConnection().getInputStream();
			String response = InstaImpl.streamToString(inputStream);
			Log.e(TAG, "Response string is " + urlString);
			 JSONObject jsonObject = (JSONObject) new JSONTokener(response).nextValue();
			 JSONArray jsonArray = jsonObject.getJSONArray("data");
//			 Log.e(TAG, "JSON Array is " + jsonArray.toString());

			 for(int i=0; i<jsonArray.length(); i++)
			 {
				 JSONObject currentObject = jsonArray.getJSONObject(i);
	
				 JSONArray tagsArray = currentObject.getJSONArray("tags");
				//check for selfie tag
				 for(int j=0; j<tagsArray.length(); j++)
				 {
					 if(tagsArray.optString(j).equals("selfie"))
					 {
						 JSONObject mainImageJsonObject = currentObject.getJSONObject("images").getJSONObject("low_resolution");
						 String imageUrlString = mainImageJsonObject.getString("url");
						 Log.e(TAG, "URL is: " + imageUrlString);
						 imageUrlStrings.add(imageUrlString);
					 }
				 }
				 Log.e(TAG, "Tags are " + tagsArray.toString()); 
			 }
			 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return imageUrlStrings;
	}
	
	@Override
	protected void onPostExecute(ArrayList<String> imageUrlStrings) {
		super.onPostExecute(imageUrlStrings);
		LinearLayout ll = (LinearLayout) activity.findViewById(R.id.displayActivityLayout);
		
		for(int i=0; i< imageUrlStrings.size(); i++)
		{
			String urlString = imageUrlStrings.get(i);
			ImageView iv = new ImageView(activity);
			ll.addView(iv);
			Picasso.with(activity).load(urlString).into(iv);	
			if(i%2==0)
			{
				iv.getLayoutParams().height = 1800;
				iv.getLayoutParams().width = 1800;
				iv.setAdjustViewBounds(true);
			}
			
			else
			{
				iv.getLayoutParams().height = 900;
				iv.getLayoutParams().width = 900;
				iv.setAdjustViewBounds(true);
			}
		}
		
		
	}

}
