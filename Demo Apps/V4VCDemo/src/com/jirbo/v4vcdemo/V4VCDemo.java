package com.jirbo.v4vcdemo;

import android.app.*;
import android.content.*;    // Intent
import android.content.pm.ActivityInfo;
import android.os.*;         // Bundle, Environment
import android.view.*;       // MotionEvent
import android.view.View.*;
import android.widget.*;
import android.util.*;

import java.io.*;
import java.util.*;

import com.jirbo.adcolony.*;

public class V4VCDemo extends Activity
  implements AdColonyAdListener, AdColonyV4VCListener
{
  final static String APP_ID  = "app185a7e71e1714831a49ec7";
  final static String ZONE_ID = "vz1fd5a8b2bf6841a0a4b826";

  Properties properties;
  AdColonyV4VCAd v4vc_ad;
  String vc_name = "credits";
  int    total_amount;

  /** Called when the activity is first created. */
  @Override
  public void onCreate( Bundle savedInstanceState )
  {
    super.onCreate(savedInstanceState);

    // App-specific - load our accumulated total amount of virtual currency.
    loadProperties();

    // Configure ADC once early on before any other ADC calls.
    AdColony.configure( this, "version:1.0,store:google", APP_ID, ZONE_ID );
    //   version - arbitrary application version
    //   store   - google or amazon

    // Disable rotation if not on a tablet-sized device (note: not
    // necessary to use AdColony).
    if ( !AdColony.isTablet() )
    {
      setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
    }

    // Notify this object about confirmed virtual currency.
    AdColony.addV4VCListener( this );

    setContentView( R.layout.main );
    setResultsText();

    Button  video_button = (Button) findViewById(R.id.video_button);

    video_button.setOnClickListener(
        new OnClickListener()
        {
          public void onClick( View v )
          {
            // Create a video ad object.
            // ad = new AdColonyVideoAd();

            v4vc_ad = new AdColonyV4VCAd( ZONE_ID ).withListener( V4VCDemo.this ).withConfirmationDialog().withResultsDialog();

            // Debug pop-up showing the number of plays today and the playcap.
            Toast.makeText( V4VCDemo.this, ""+v4vc_ad.getRewardName(), Toast.LENGTH_SHORT ).show();

            String status = "Available views: " + v4vc_ad.getAvailableViews();
            Toast.makeText( V4VCDemo.this, status, Toast.LENGTH_SHORT ).show();

            v4vc_ad.show();

          }
        });
  }

  public void onPause()
  {
    super.onPause();
    AdColony.pause();  // necessary for correct session length reporting
  }

  public void onResume()
  {
    super.onResume();
    AdColony.resume( this );  // necessary for correct session length reporting
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
  void setResultsText()
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

  public void onAdColonyV4VCReward( AdColonyV4VCReward reward )
  {
    if (reward.success())
    {
      vc_name = reward.name();
      total_amount += reward.amount();
      setResultsText();
    }
  }

  public void onAdColonyAdAttemptFinished( AdColonyAd ad )
  {
    System.out.println("ADC [onAdColonyAdFinished]");
    setResultsText();
  }
  public void onAdColonyAdStarted( AdColonyAd ad )
  {
	  //do stuff here
  }
}

