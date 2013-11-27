package ro.vadim.picturetrails;

import java.io.IOException;

import ro.vadim.picturetrails.R;
import ro.vadim.picturetrails.utils.GlobalData;
import ro.vadim.picturetrails.utils.Utils;
import ro.vadim.picturetrails.visuals.fragments.MainMenuFragment;
import android.os.Bundle;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends FragmentActivity {
	
	
	private void initUncaughtExceptionHandler(){
		
		final Activity thisActivity = this;
		
		Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
			
			@Override
			public void uncaughtException(Thread thread, Throwable ex) {
				
				Log.e("MainActivity", "uncaught exception: "+ex.getMessage());
				Log.e("MainActivity", "uncaught exception: "+ex.getStackTrace());
				Utils.stopTracerService(thisActivity);
			}
		};
	}
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_main);
		
		initUncaughtExceptionHandler();
		
		GlobalData.initGlobal(this);
		GlobalData.getFragmentManager().loadFragment(MainMenuFragment.class.getCanonicalName(), null, null);
		
		try {
			GlobalData.loadTrace();
		}
		catch (IOException e) {
			Log.e("MainActivity", "GlobalData.loadTrace(): error: "+e.toString());
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onPause() {		
		super.onPause();
		try {
			GlobalData.saveTrace();
		} 
		catch (IOException e) {
			Log.e("MainActivity", "GlobalData.saveTrace(): error: "+e.toString());
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();		
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
