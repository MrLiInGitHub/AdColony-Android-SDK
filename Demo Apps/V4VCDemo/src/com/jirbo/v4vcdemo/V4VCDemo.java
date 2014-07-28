package com.jirbo.v4vcdemo;

import android.app.*;
import android.content.pm.ActivityInfo;
import android.os.*;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.io.*;
import java.util.*;

import com.jirbo.adcolony.*;

public class V4VCDemo extends Activity
  implements AdColonyAdListener, AdColonyV4VCListener, AdColonyAdAvailabilityListener
{
  final static String APP_ID  = "app185a7e71e1714831a49ec7";
  final static String ZONE_ID = "vz1fd5a8b2bf6841a0a4b826";

  Handler button_text_handler;
  Runnable button_text_runnable;
  Properties properties;
  AdColonyV4VCAd v4vc_ad;
  Button video_button;
  String vc_name = "credits";
  int    total_amount;

  /** Called when the activity is first created. */
  @Override
  public void onCreate( Bundle savedInstanceState )
  {
    super.onCreate(savedInstanceState);

    // App-specific - load our accumulated total amount of virtual currency.
    loadProperties(); 
    
    // Configure ADC once early before any other ADC calls (except setCustomID/setDeviceID).
    AdColony.configure( this, "version:1.0,store:google", APP_ID, ZONE_ID );
    // version - arbitrary application version
    // store   - google or amazon

    // Disable rotation if not on a tablet-sized device (note: not
    // necessary to use AdColony).
    if ( !AdColony.isTablet() )
    {
      setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
    }

    // Notify this object about confirmed virtual currency.
    AdColony.addV4VCListener( this );
    
    // Notify this object about ad availability changes.
    AdColony.addAdAvailabilityListener( this );

    setContentView( R.layout.main );
    setResultsText();

    video_button = (Button) findViewById(R.id.video_button);
    
    // Handler and Runnable for updating button text based on ad availability listener
    button_text_handler = new Handler();
    button_text_runnable = new Runnable()
    {
      public void run()
      {
    	video_button.setText("Video With VC");
    	video_button.setOnClickListener(
    	new View.OnClickListener()
    	{
          public void onClick( View v )
    	  {
    		v4vc_ad = new AdColonyV4VCAd( ZONE_ID ).withListener( V4VCDemo.this ).withConfirmationDialog().withResultsDialog();
    		            
    		// Debug pop-up showing the number of plays today and the playcap.
    		//Toast.makeText( V4VCDemo.this, ""+v4vc_ad.getRewardName(), Toast.LENGTH_SHORT ).show();

    		String status = "Available views: " + v4vc_ad.getAvailableViews();
    		Toast.makeText( V4VCDemo.this, status, Toast.LENGTH_SHORT ).show();

    		v4vc_ad.show();
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

  // App-specific - load our accumulated total amount of virtual currency.
  void loadProperties()
  {
    properties = new Properties();
    try
    {
      properties.load( openFileInput("vc_info.properties") );
      vc_name = properties.getProperty( "vc_name", "credits" );
      total_amount = Integer.parseInt( properties.getProperty("total_amount","0") );
    }
    catch (Exception err)
    {
      vc_name = "credits";
      total_amount = 0;
    }
  }

  //App-specific - display the confirmed amount of VC.
  public void setResultsText()
  {
    TextView results = (TextView) findViewById(R.id.results_view);
    results.setText( "Earn game currency by watching videos!\nTotal amount earned: "
        + total_amount + " " + vc_name + "." );

    properties.setProperty( "vc_name", vc_name );
    properties.setProperty( "total_amount", ""+ total_amount );

    try
    {
      OutputStream outfile = openFileOutput( "vc_info.properties", 0 );
      properties.store( outfile, "vc info" );
      outfile.close();
    }
    catch (Exception err)
    {
    }
  }

  // Reward Callback
  public void onAdColonyV4VCReward( AdColonyV4VCReward reward )
  {
    if (reward.success())
    {
      vc_name = reward.name();
      total_amount += reward.amount();
      setResultsText();
    }
  }
  
  // Ad Started Callback - called only when an ad successfully starts playing
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
   setResultsText();
  }

  // Ad Availability Change Callback - update button text
  public void onAdColonyAdAvailabilityChange(boolean available, String zone_id) 
  {
	if (available) button_text_handler.post(button_text_runnable);
  }
}

