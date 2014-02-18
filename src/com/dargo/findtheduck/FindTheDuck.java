package com.dargo.findtheduck;

import java.util.HashMap;

import com.dargo.findtheduck.common.Utilities;
import com.google.ads.AdRequest;
import com.google.ads.AdView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FindTheDuck extends Activity {

	private AdView adView;
	private ImageView myBackground;
	private Context myContext;
	private int myScreenHeight = 0;
	private int myScreenWidth = 0;
	private Duck myDuck = null;
	private SoundPool soundPool;
	private HashMap<Integer, Integer> soundPoolMap;
	public static final int QUACKQUACK = R.raw.quackquack;
	public static final int HAPHAP = R.raw.haphap;
	public boolean myIsSoundOn;
	public boolean myIsTextOn;

	public int myAdHeight = 0;
	private int myScore = 0;
	private int myHighScore = 0;
	private TextView myScoreTV;
	private TextView myHighScoreTV;
	private SharedPreferences mySettings;
	//private MediaPlayer myMP;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_my_duck);
		mySettings = getSharedPreferences(Utilities.PREFS_NAME, 0);
		myHighScoreTV = (TextView) findViewById(R.id.high_score_value_TV); 
		myScoreTV = (TextView) findViewById(R.id.current_score_value_TV);
		myContext = this;
		readPreferences();
		myHighScoreTV.setText(String.valueOf(myHighScore));
		initSounds();
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		Utilities.showActionBar(this);
		adView = (AdView) findViewById(R.id.duck_ad);
        adView.loadAd(new AdRequest());
        adView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
        	 
            @Override
            public void onGlobalLayout() {
            	myAdHeight = adView.getHeight();
         
                if (myAdHeight > 0) {

                    adView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    myScreenHeight = myScreenHeight - myAdHeight;
                    if ( myDuck.myYCoordinate > ( myScreenHeight - myAdHeight ) )
                    {
                    	myDuck.myYCoordinate = myDuck.myYCoordinate - myAdHeight; 
                    }
                    //DEBUG
                    //Toast.makeText(myContext, "Ad height is: " + String.valueOf(myAdHeight), Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        
		
		myBackground = (ImageView) findViewById(R.id.imageView1);
		myBackground.setOnTouchListener( new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
            	
            	if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN && !myDuck.myIsFound)
            	{
            		int posX;
	            	int posY;
	            	posX = (int) event.getX();
	            	posY = (int) event.getY();
	            	myScore++;
	            	myScoreTV.setText(String.valueOf(myScore));
	            	handleTap(posX,posY);
	            	return true;
            	}
            	return false;
            }
            });
	}
	
	
	private void readPreferences() 
	{
    	myIsSoundOn = mySettings.getBoolean(Utilities.IS_SOUND_ON, true);
    	myIsTextOn = mySettings.getBoolean(Utilities.IS_TEXT_ON, false);
    	myHighScore = mySettings.getInt(Utilities.HIGH_SCORE, 0);
	}

	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void initSounds()
	{
		soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 100);
		soundPoolMap = new HashMap(3);    
		soundPoolMap.put( QUACKQUACK, soundPool.load(this, R.raw.quackquack, 1) );
		soundPoolMap.put( HAPHAP, soundPool.load(this, R.raw.haphap, 2) );
	}
	
	public void handleTap(int posX, int posY)
	{
		//FOR DEBUG
		//double aDistance = calculateDistance(posX, posY);
		//int aTapLevel = calculateTapLevel(aDistance, myDuck.myMaxDistance); // quality of tapping based on distance
		if ( isDuckThere(posX, posY) )
		{
			if (myScore < myHighScore || myHighScore == 0)
			{
				Toast.makeText(myContext, "Congratulations! You have just beaten your high score!", Toast.LENGTH_SHORT).show();
		    	SharedPreferences.Editor anEditor = mySettings.edit();

	    		anEditor.putInt(Utilities.HIGH_SCORE, myScore)
	    		.apply();

			}
			myDuck.myIsFound = true;
			myBackground.setImageResource(R.raw.duck);
			if (myIsSoundOn)
			{
				soundPool.play((Integer) soundPoolMap.get(HAPHAP), 1f, 1f, 1, 0, 1f);
			}
		}
		else
		{
			int aTapQuality = calculateTapLevel(calculateDistance(posX, posY), myDuck.myMaxDistance);
			if (myIsSoundOn)
			{
			createSoundNotification(aTapQuality);
			}
			if (myIsTextOn)
			{
			createTextNotification(aTapQuality);	
			}
			//FOR DEBUG
	    	//Toast.makeText(myContext, "You tapped at X: " + String.valueOf(posX) + "/" + String.valueOf(myScreenWidth) + 
	    	//		" Y: " + String.valueOf(posY) + "/" + String.valueOf(myScreenHeight) + " distance: " + aDistance + " maxD: " + myDuck.myMaxDistance + " qual: " + String.valueOf(aTapLevel), Toast.LENGTH_SHORT).show();
	
		}
		
	}
	

	public boolean isDuckThere(int iPosX, int iPosY)
	{
		if (Math.abs(iPosX - myDuck.myXCoordinate) < 0.015 * myScreenWidth && Math.abs(iPosY-myDuck.myYCoordinate) < 0.015 * myScreenHeight)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	
	public void createSoundNotification(int iTapQuality)
	{
		
		float volume = (float) (iTapQuality * 0.2);
		switch (iTapQuality){
		
		case 1	: 	volume =  0.1f;
					break;
		case 2	: 	volume =  0.25f;
					break;
		case 3	:	volume =  0.45f;
					break;
		case 4	:	volume =  0.7f;
					break;
		case 5  :	volume =  1f;
					break;
		
		}
		
		
		soundPool.play((Integer) soundPoolMap.get(QUACKQUACK), volume, volume, 1, 0, 1f);
	}
	
	public void createTextNotification(int iTapQuality)
	{
		
		String aMsg = "";
		switch (iTapQuality){
		
		case 1	: 	aMsg = "Far from it...";
					break;
		case 2	: 	aMsg = "Keep searching!";
					break;
		case 3	:	aMsg = "You are on the track!";
					break;
		case 4	:	aMsg = "You almost caught it!";
					break;
		case 5  :	aMsg = "HOT!!!HOT!!!HOT!!!";
					break;
		
		}
		
		
		Toast.makeText(myContext, aMsg, Toast.LENGTH_SHORT).show();
	}
	
	public int calculateTapLevel(double iDistance, double iMaxDistance)
	{
		double anAbsTapLevel = iDistance / iMaxDistance;
		int retVal = 0;
		if (anAbsTapLevel < 0.1)
		{
			retVal = 5;
		}
		else if (anAbsTapLevel < 0.25)
		{
			retVal = 4;
		}
		else if (anAbsTapLevel < 0.45)
		{
			retVal = 3;
		}
		else if (anAbsTapLevel < 0.70)
		{
			retVal = 2;
		}
		else
		{
			retVal = 1;
		}
		
		
		return retVal;
	}
	
	public double calculateDistance(int iTappedX, int iTappedY)
	{
		//calculate distance of Duck and tapped
		//d = sqrt (sqr(x2-x1) + sqr(y2-y1))
		
		return Math.sqrt(Math.pow(myDuck.myXCoordinate - iTappedX, 2) + Math.pow(myDuck.myYCoordinate - iTappedY, 2));		
	}
	
	public void onWindowFocusChanged(boolean hasFocus) 
	{
		super.onWindowFocusChanged(hasFocus);
		RelativeLayout aLayout = (RelativeLayout) findViewById(R.id.main_view);
		myScreenHeight = aLayout.getHeight();
		myScreenWidth = aLayout.getWidth();
		 
		if (myDuck == null)
		{
			 myDuck = new Duck(Utilities.randInt(0, myScreenWidth), Utilities.randInt(0, myScreenHeight), myScreenWidth, myScreenHeight);
		}
	}
	
	public void onResume()
	{
		super.onResume();
		readPreferences();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
    	try
    	{
    		startActivity(Utilities.clickedOnMainMenu(item, this));
            return super.onOptionsItemSelected(item);	
    	}
    	catch (NullPointerException ex)
    	{
    		super.onBackPressed();
    		return false;
    	}
    }

}
