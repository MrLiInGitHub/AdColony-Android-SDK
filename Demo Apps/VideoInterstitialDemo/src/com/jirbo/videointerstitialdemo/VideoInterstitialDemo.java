package com.jirbo.videointerstitialdemo;

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

public class VideoInterstitialDemo extends Activity
  implements AdColonyAdListener
{
  final static String APP_ID  = "app185a7e71e1714831a49ec7";
  final static String ZONE_ID = "vz06e8c32a037749699e7050";
  Context ctx;

  /** Called when the activity is first created. */
  @Override
  public void onCreate( Bundle savedInstanceState )
  {
    super.onCreate(savedInstanceState);

    AdColony.configure( this, "version:1.0,store:google", APP_ID, ZONE_ID );
    //   version - arbitrary application version
    //   store   - google or amazon

    // Disable rotation if not on a tablet-sized device (note: not
    // necessary to use AdColony).
    if ( !AdColony.isTablet() )
    {
      setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
    }

    setContentView( R.layout.main );

    final Button video_button = (Button) findViewById(R.id.video_button);

    video_button.setOnClickListener(
        new OnClickListener()
        {
          public void onClick( View v )
          {
            AdColonyVideoAd ad = new AdColonyVideoAd();
            ad.show();
          }
        } );
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

  public void onAdColonyAdStarted( AdColonyAd ad )
  {
    //do something
  }

  public void onAdColonyAdAttemptFinished( AdColonyAd ad )
  {
    Toast.makeText( this, "Video Finished", Toast.LENGTH_SHORT ).show();
  }

}


