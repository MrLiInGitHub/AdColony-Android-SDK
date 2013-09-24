package com.jirbo.multizonedemo;

import android.app.*;
import android.content.*;    // Intent
import android.content.pm.ActivityInfo;
import android.os.*;         // Bundle, Environment
import android.view.*;       // MotionEvent
import android.view.View.*;
import android.widget.*;
import android.util.*;

import com.jirbo.adcolony.*;

import java.io.*;
import java.util.*;

public class MultiZoneDemo extends Activity
{
  final static public String APP_ID = "app185a7e71e1714831a49ec7";
  final static public String ZONE_1  = "vz06e8c32a037749699e7050";
  final static public String ZONE_2  = "vz2d167a733dfe48fa99000d";
  final static public String ZONE_3  = "vze0c3973f70dc4bcfb15663";
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
    
    if ( !AdColony.isTablet() ) setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );

    setContentView( R.layout.main );

    final Button zone_1 = (Button) findViewById(R.id.zone1);
    final Button zone_2 = (Button) findViewById(R.id.zone2);
    final Button zone_3 = (Button) findViewById(R.id.zone3);

    zone_1.setOnClickListener(
        new OnClickListener()
        {
          public void onClick( View v )
          {
            AdColonyVideoAd ad = new AdColonyVideoAd( ZONE_1 );
            ad.show();
          }
        });

    zone_2.setOnClickListener(
        new OnClickListener()
        {
          public void onClick( View v )
          {
            AdColonyVideoAd ad = new AdColonyVideoAd( ZONE_2 );
            ad.show();
          }
        });

    zone_3.setOnClickListener(
        new OnClickListener()
        {
          public void onClick( View v )
          {
            AdColonyVideoAd ad = new AdColonyVideoAd( ZONE_3 );
            ad.show();
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

}

