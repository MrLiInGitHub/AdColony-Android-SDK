package com.adcolony.instantfeeddemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.jirbo.adcolony.*;



public class InstantFeedActivity extends Activity implements AdColonyAdAvailabilityListener
{
    private AdColonyNativeAdView native_ad;
    private LinearLayout native_ad_layout;

    private int width;
    private float density;

    private final String APP_ID = "app185a7e71e1714831a49ec7";
    private final String ZONE_ID = "vze4675ec2638048a789";

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.layout );

        ScrollView scroll_view;

        native_ad_layout = (LinearLayout) findViewById( R.id.instant_feed_layout );
        scroll_view = (ScrollView) findViewById( R.id.scroll_view );

        DisplayMetrics display_metrics = getResources().getDisplayMetrics();
        width = display_metrics.widthPixels > display_metrics.heightPixels ? display_metrics.heightPixels : display_metrics.widthPixels;
        density = display_metrics.density;;


        ViewGroup.LayoutParams params = scroll_view.getLayoutParams();
        params.width = width;
        scroll_view.setLayoutParams( params );

        AdColony.configure( this, "version:1.0,store:google", APP_ID, ZONE_ID );
        AdColony.addAdAvailabilityListener( this );
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        AdColony.pause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        AdColony.resume( this );

        //For when Activity is recreated - let's try and add a Instant-Feed ad if available
        if (native_ad == null && AdColony.statusForZone( ZONE_ID ).equals( "active" ) )
        {
            add_native_ad_view( ZONE_ID );
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (native_ad != null)
        {
            native_ad.destroy();
            native_ad = null;
        }
    }

    private boolean add_native_ad_view( String zone_id )
    {
        native_ad = new AdColonyNativeAdView( this, zone_id, width, (int)(density*200) );

        //Additionally, you can retrieve/place the following metadata into your layout as desired:
        //native_ad.getAdvertiserName();
        //native_ad.getTitle();
        //native_ad.getDescription();
        //native_ad.getAdvertiserImage();

        if (native_ad.isReady())
        {
            //Make sure UI changes are happening on the main thread
            runOnUiThread( new Runnable()
            {
                @Override
                public void run()
                {
                    native_ad_layout.addView( native_ad );
                    Toast.makeText( InstantFeedActivity.this,
                                    "Instant-Feed ad added to layout. Scroll through your feed to find it.",
                                    Toast.LENGTH_LONG).show();
                }
            } );

            return true;
        }

        native_ad = null;
        return false;
    }

    @Override
    public void onAdColonyAdAvailabilityChange( boolean available, String zone_id )
    {
        //If the zone now has ads available and we don't currently have an ad in
        //our feed, we'll add one here.
        if (available && native_ad == null)
        {
            add_native_ad_view( zone_id );
        }
    }
}
