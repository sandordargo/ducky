package com.dargo.findtheduck.common;

import com.dargo.findtheduck.R;
import com.google.ads.AdRequest;
import com.google.ads.AdView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;


public class SettingsMenu extends Activity 
{
	private AdView adView;
	private CheckBox mySoundCB;
	private CheckBox myTextCB;
		
		@Override
		protected void onCreate(Bundle savedInstanceState) 
		{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.settings_menu);
			mySoundCB = (CheckBox) findViewById(R.id.is_sound_enabled_CB);
			myTextCB = (CheckBox) findViewById(R.id.is_text_enabled_CB);
			readPreferences();
			Utilities.showActionBar(this);
	        adView = (AdView) findViewById(R.id.settings_ad);
	        adView.loadAd(new AdRequest());

		}
		
		
		private void readPreferences() 
		{
			SharedPreferences aSettings = getSharedPreferences(Utilities.PREFS_NAME, 0);
	    	Boolean aIsSoundOn = aSettings.getBoolean(Utilities.IS_SOUND_ON, true);
	    	Boolean aIsTextOn = aSettings.getBoolean(Utilities.IS_TEXT_ON, false);
	    	
	    	if (aIsSoundOn)
	    	{
	    		mySoundCB.setChecked(true);
	    	}
	    	else
	    	{
	    		mySoundCB.setChecked(false);
	    	}
	    	
	    	if (aIsTextOn)
	    	{
	    		myTextCB.setChecked(true);
	    	}
	    	else
	    	{
	    		myTextCB.setChecked(false);
	    	}
	    	
		}

		
		@SuppressLint("NewApi")
		public void setBackgroundRunCB()
		{
			final boolean aIsSoundChecked = mySoundCB.isChecked();
			final boolean aIsTextChecked = myTextCB.isChecked();
			
	    	SharedPreferences settings = getSharedPreferences(Utilities.PREFS_NAME, 0);
	    	SharedPreferences.Editor editor = settings.edit();

    		editor.putBoolean(Utilities.IS_SOUND_ON, aIsSoundChecked)
    		.putBoolean(Utilities.IS_TEXT_ON, aIsTextChecked)
    		.apply();

		}
		
		
		public void onBackPressed()
		{
			setBackgroundRunCB();
			finish();
		}
		
		public void onPause()
		{
			super.onPause();
			setBackgroundRunCB();
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
