package ro.vadim.picturetrails;

import java.io.IOException;

import ro.vadim.picturetrails.R;
import ro.vadim.picturetrails.utils.GlobalData;
import ro.vadim.picturetrails.visuals.fragments.MainMenuFragment;
import android.os.Bundle;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_main);
		
		GlobalData.initGlobal(this);
		GlobalData.getFragmentManager().loadFragment(MainMenuFragment.class.getCanonicalName(), null, null);
		GlobalData.registerBroadcastReceiver();
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
		GlobalData.unregisterBroadcastReceiver();
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
