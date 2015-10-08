package com.adcolony.interstitialdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.jirbo.adcolony.*;

public class InterstitialActivity extends Activity implements AdColonyAdAvailabilityListener, AdColonyAdListener
{
    final private String APP_ID  = "app185a7e71e1714831a49ec7";
    final private String ZONE_ID = "vz06e8c32a037749699e7050";

    //Button used to play videos, enabled when ads are available
    private Button button;
    private ProgressBar progress;

    @Override
    protected void onCreate( Bundle bundle )
    {
        super.onCreate(bundle);
        setContentView(R.layout.layout);

        //Configure AdColony in your launching Activity's onCreate() method
        //so that ads can be available as soon as possible.
        AdColony.configure( this, "version:1.0,store:google", APP_ID, ZONE_ID );

        //Register an AdColonyAdAvailabilityListener to be notified of changes in a zone's
        //ad availability.
        AdColony.addAdAvailabilityListener( this );

        progress = (ProgressBar)findViewById( R.id.progress );

        button = (Button)findViewById( R.id.button );
        button.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                AdColonyVideoAd ad = new AdColonyVideoAd( ZONE_ID ).withListener( InterstitialActivity.this );
                ad.show();
            }
        } );

        //If ads are already ready for zone (i.e. if onCreate is happening for a second time) we want
        //to make sure our button is enabled.
        if (AdColony.statusForZone( ZONE_ID ).equals( "active" ))
        {
            button.setEnabled( true );
            progress.setVisibility( View.INVISIBLE );
        }
    }

    @Override
    public void onAdColonyAdAvailabilityChange( final boolean available, String zone_id )
    {
        runOnUiThread( new Runnable()
        {
            @Override
            public void run()
            {
                if (available)
                {
                    button.setEnabled( true );
                    progress.setVisibility( View.INVISIBLE );
                }
                else
                {
                    button.setEnabled( false );
                    progress.setVisibility( View.VISIBLE );
                }
            }
        } );
    }

    @Override
    public void onAdColonyAdAttemptFinished( AdColonyAd ad )
    {
        //Can use the ad object to determine information about the ad attempt:
        //ad.shown();
        //ad.notShown();
        //ad.canceled();
        //ad.noFill();
        //ad.skipped();
    }

    @Override
    public void onAdColonyAdStarted( AdColonyAd ad )
    {
        //Called when the ad has started playing
    }

    @Override
    public void onResume()
    {
        super.onResume();
        AdColony.resume(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        AdColony.pause();
    }
}
