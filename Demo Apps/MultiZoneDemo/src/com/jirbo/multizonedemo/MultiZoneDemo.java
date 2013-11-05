package com.jirbo.multizonedemo;

import java.util.ArrayList;

import android.app.*;
import android.content.pm.ActivityInfo;
import android.os.*;  
import android.util.Log;
import android.view.*; 
import android.view.View.*;
import android.widget.*;

import com.jirbo.adcolony.*;

public class MultiZoneDemo extends Activity 
  implements AdColonyAdListener, AdColonyAdAvailabilityListener
{
  final static public String APP_ID = "app185a7e71e1714831a49ec7";
  final static public String ZONE_1  = "vz06e8c32a037749699e7050";
  final static public String ZONE_2  = "vz2d167a733dfe48fa99000d";
  final static public String ZONE_3  = "vze0c3973f70dc4bcfb15663";
  
  Handler button_text_handler;
  Runnable button_text_runnable;
  ArrayList<String> zones_to_update;
  Button video_button_1;
  Button video_button_2;
  Button video_button_3;

  /** Called when the activity is first created. */
  @Override
  public void onCreate( Bundle savedInstanceState )
  {
    super.onCreate(savedInstanceState);

    AdColony.configure( this,
        "version:1.0,store:google",
          //   version - arbitrary application version
          //   store   - google or amazon
        APP_ID,
        ZONE_1, ZONE_2, ZONE_3
      );
    
    AdColony.addAdAvailabilityListener(this);
    
    if ( !AdColony.isTablet() ) setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );

    setContentView( R.layout.main );

    video_button_1 = (Button) findViewById( R.id.zone1 );
    video_button_2 = (Button) findViewById (R.id.zone2 );
    video_button_3 = (Button) findViewById( R.id.zone3 );

    video_button_1.setOnClickListener(
        new OnClickListener()
        {
          public void onClick( View v )
          {
            AdColonyVideoAd ad = new AdColonyVideoAd( ZONE_1 ).withListener( MultiZoneDemo.this );
            ad.show();
          }
        });

    video_button_2.setOnClickListener(
        new OnClickListener()
        {
          public void onClick( View v )
          {
            AdColonyVideoAd ad = new AdColonyVideoAd( ZONE_2 ).withListener( MultiZoneDemo.this );
            ad.show();
          }
        });

    video_button_3.setOnClickListener(
        new OnClickListener()
        {
          public void onClick( View v )
          {
            AdColonyVideoAd ad = new AdColonyVideoAd( ZONE_3 ).withListener( MultiZoneDemo.this );
            ad.show();
          }
        });
    
    // Handler and Runnable for updating button text based on ad availability listener
    button_text_handler = new Handler();
    button_text_runnable = new Runnable()
    {
      public void run()
      {
    	if ( zones_to_update.contains(ZONE_1) ) video_button_1.setText("Video Zone 1");
    	if ( zones_to_update.contains(ZONE_2) ) video_button_2.setText("Video Zone 2");
    	if ( zones_to_update.contains(ZONE_3) ) video_button_3.setText("Video Zone 3");
      }
    };
    
    zones_to_update = new ArrayList<String>();
    
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
	if (available)
	{
	  zones_to_update.add(zone_id);
	  button_text_handler.post(button_text_runnable);
	}
  }

}

