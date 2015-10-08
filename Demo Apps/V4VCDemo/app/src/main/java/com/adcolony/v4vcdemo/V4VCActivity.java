package com.adcolony.v4vcdemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jirbo.adcolony.*;

public class V4VCActivity extends Activity implements AdColonyAdAvailabilityListener, AdColonyV4VCListener, AdColonyAdListener
{
    final private String APP_ID  = "app185a7e71e1714831a49ec7";
    final private String ZONE_ID = "vz1fd5a8b2bf6841a0a4b826";

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

        //Register an AdColonyV4VCListener to be notified of reward events
        AdColony.addV4VCListener( this );

        progress = (ProgressBar)findViewById( R.id.progress );

        button = (Button)findViewById( R.id.button );
        button.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                AdColonyV4VCAd ad = new AdColonyV4VCAd().withConfirmationDialog().withResultsDialog().withListener(V4VCActivity.this);
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
                //If zone has ads available, enable the button
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
    public void onAdColonyV4VCReward( AdColonyV4VCReward reward )
    {
        if (reward.success())
        {
            //Reward was successful, reward your user here
            int amount  = reward.amount();
            String name = reward.name();
            Toast.makeText( this, amount + " " + name + " awarded!", Toast.LENGTH_LONG ).show();
        }
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
