package com.example.adcolonyinstantfeedadsdemo;

import java.util.ArrayList;

import com.jirbo.adcolony.*;

import android.os.Bundle;
import android.os.Looper;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class InstantFeedAdActivity extends Activity
	   implements AdColonyAdAvailabilityListener, AdColonyNativeAdListener, AdColonyNativeAdMutedListener
{
	final String APP_ID = "app559cdb8415094615bc";
	final String ZONE_ID = "vzcc6a56458a2e47b9b1";
	
	//It is recommended to keep a list of native ads, so that you are able to properly destroy
	//them or otherwise modify them as needed.
	ArrayList<AdColonyNativeAdView> native_ad_list = new ArrayList<AdColonyNativeAdView>();
	
	//Layout that we will be adding our Instant Feed ad into
	LinearLayout native_layout;
	
	//Used to calculate appropriate native ad width
	float density;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_instant_feed_ad);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		//Get this device's density multiplier
		density = getResources().getDisplayMetrics().density;
		
		//Get layout from the layout.xml
		native_layout = (LinearLayout)findViewById(R.id.native_layout);
		
		//Configure AdColony and add an ad availability listener
		AdColony.configure(this, "version:1.0,store:google", APP_ID, ZONE_ID);
		AdColony.addAdAvailabilityListener( this );
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		AdColony.resume(this); //REQUIRED
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		AdColony.pause(); //REQUIRED
	}
	
	/*
	 * Called from our ad availability listener when a native ad is available.
	 * Construct your layout to insert into the ScrollView (or ViewGroup of your
	 * choice) here. For more complex integrations it may be easier to use an
	 * AsyncTask.
	 * 
	 * This is just an example of how one may want to layout our Instant Feed ads.
	 * Only the native ad view, advertiser name, advertiser image, and sponsored text
	 * are required.
	 */
	void addInstantFeedAd(AdColonyNativeAdView native_ad)
	{
		//Create inner layouts for ad unit
		LinearLayout header_layout = new LinearLayout(this);
		LinearLayout sub_header_layout = new LinearLayout(this);
		header_layout.setOrientation(LinearLayout.HORIZONTAL);
		sub_header_layout.setOrientation(LinearLayout.VERTICAL);
		sub_header_layout.setGravity(Gravity.CENTER_VERTICAL);
	
		
		//Create views - retrieve ad specific info
		TextView advertiser_name = new TextView(this);
		TextView sponsored_text = new TextView(this);
		ImageView advertiser_image = native_ad.getAdvertiserImage();
		advertiser_name.setText(native_ad.getAdvertiserName());
		advertiser_name.setTextSize(20);
		advertiser_name.setTextColor(Color.BLACK);
		advertiser_name.setTypeface(Typeface.DEFAULT_BOLD);
		sponsored_text.setText("Sponsored");
		sponsored_text.setTextColor(Color.GRAY);
		
		//Create and setup layout params
		LinearLayout.LayoutParams advertiser_image_params = 
				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		LinearLayout.LayoutParams sub_header_params = 
				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		LinearLayout.LayoutParams advertiser_name_params =
				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		
		advertiser_image_params.setMargins(0, 0, (int)(6*density), (int)(6*density));
		sub_header_params.setMargins(0, (int)(6*density), 0, 0);
		advertiser_name_params.setMargins(0, 0, 0, (int)(-6*density));
		
		//Attach listeners
		native_ad.withListener(this).withMutedListener(this);
		
		/*
		 * You can also optionally retrieve the ad title
		 * and description for your use using the following methods:
		 * 
		 * native_ad.getTitle()       : String
		 * native_ad.getDescription() : String
		 */
		
		sub_header_layout.addView(advertiser_name, advertiser_name_params);
		sub_header_layout.addView(sponsored_text, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		header_layout.addView(advertiser_image, advertiser_image_params);
		header_layout.addView(sub_header_layout, sub_header_params);
		native_layout.addView(header_layout);
		native_layout.addView(native_ad, new LinearLayout.LayoutParams(native_ad.getNativeAdWidth(), native_ad.getNativeAdHeight()));
		native_layout.setVisibility(View.VISIBLE);
	}
	
	public void onAdColonyAdAvailabilityChange(boolean available, String zone_id)
	{
		if (Looper.myLooper() == null) Looper.prepare();
		if (available)
		{
			//Create native ad object, and add to layout (on UI thread) if one is available
			final AdColonyNativeAdView native_ad = new AdColonyNativeAdView(InstantFeedAdActivity.this, zone_id, (int)(300*density));
			if (native_ad.isReady())
			{
				runOnUiThread(new Runnable()
				{
					public void run()
					{
						addInstantFeedAd(native_ad);
						native_ad_list.add(native_ad);
						Toast.makeText(InstantFeedAdActivity.this, "Added Instant-Feed ad to the layout.", Toast.LENGTH_SHORT).show();
					}
				});
			}
		}
	}

	@Override
	public void onAdColonyNativeAdFinished(boolean expanded, AdColonyNativeAdView native_ad) 
	{
		if (expanded)
		{
			//We are choosing to remove native ad view from the layout after an expanded ad play finishes.
			//Note that you can choose to remove the ad at any point that you wish -
			//the important thing is to remember to call destroy() whenever you do.
			runOnUiThread(new Runnable()
			{
				public void run()
				{
					Toast.makeText(InstantFeedAdActivity.this, "Removed Instant-Feed ad from the layout.", Toast.LENGTH_SHORT).show();
					native_layout.removeAllViews();
					for (AdColonyNativeAdView native_ad : native_ad_list) native_ad.destroy();
					native_ad_list.clear();
				}
			});
		}
	}

	@Override
	public void onAdColonyNativeAdStarted(boolean expanded, AdColonyNativeAdView native_ad) 
	{	
		//Called when the ad starts
	}

	@Override
	public void onAdColonyNativeAdMuted(AdColonyNativeAdView native_ad, boolean muted) 
	{
		//Called when the ad is muted or unmuted by the user
	}


}
