package com.jirbo.videointerstitialdemo;

import android.app.*;
import android.content.pm.ActivityInfo;
import android.os.*; 
import android.util.Log;
import android.view.*;  
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.*;

import com.jirbo.adcolony.*;

public class VideoInterstitialDemo extends Activity
  implements AdColonyAdListener, AdColonyAdAvailabilityListener
{
  final static String APP_ID  = "app185a7e71e1714831a49ec7";
  final static String ZONE_ID = "vz06e8c32a037749699e7050";
  
  Handler button_text_handler;
  Runnable button_text_runnable;
  Button video_button;

  /** Called when the activity is first created. */
  @Override
  public void onCreate( Bundle savedInstanceState )
  {
    super.onCreate(savedInstanceState);
    
    AdColony.configure( this, "version:1.0,store:google", APP_ID, ZONE_ID );
    // version - arbitrary application version
    // store   - google or amazon
    
    // Add ad availability listener
    AdColony.addAdAvailabilityListener(this);

    // Disable rotation if not on a tablet-sized device (note: not
    // necessary to use AdColony).
    if ( !AdColony.isTablet() )
    {
      setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
    }

    setContentView( R.layout.main );
    
    video_button = (Button) findViewById(R.id.video_button);
    
    // Handler and Runnable for updating button text based on ad availability listener
    button_text_handler = new Handler();
    button_text_runnable = new Runnable()
    {
      public void run()
      {
    	video_button.setText("Show Video");
    	video_button.setOnClickListener(
    	new View.OnClickListener()
    	{
          public void onClick( View v )
    	  {
    		AdColonyVideoAd ad = new AdColonyVideoAd( ZONE_ID ).withListener( VideoInterstitialDemo.this );
    		ad.show();
    	  }
    	});
      }
    };
  }

  public void onPause()
  {
    super.onPause();
    AdColony.pause();
  }

  public void onResume()
  {
    super.onResume();
    AdColony.resume( this );
  }

  //Ad Started Callback - called only when an ad successfully starts playing
  public void onAdColonyAdStarted( AdColonyAd ad )
  {
	Log.d("AdColony", "onAdColonyAdStarted");
  }

  //Ad Attempt Finished Callback - called at the end of any ad attempt - successful or not.
  public void onAdColonyAdAttemptFinished( AdColonyAd ad )
  {
	// You can ping the AdColonyAd object here for more information:
	// ad.shown() - returns true if the ad was successfully shown.
	// ad.notShown() - returns true if the ad was not shown at all (i.e. if onAdColonyAdStarted was never triggered)
	// ad.skipped() - returns true if the ad was skipped due to an interval play setting
	// ad.canceled() - returns true if the ad was cancelled (either programmatically or by the user)
	// ad.noFill() - returns true if the ad was not shown due to no ad fill.
	  
    Log.d("AdColony", "onAdColonyAdAttemptFinished");
  }
  
  //Ad Availability Change Callback - update button text
  public void onAdColonyAdAvailabilityChange(boolean available, String zone_id) 
  {
	if (available) button_text_handler.post(button_text_runnable);
  }

}


