package ro.vadim.picturetrails.utils;

import ro.vadim.picturetrails.visuals.fragments.LastImpressionFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

public class TracerServiceBroadcastReceiver extends BroadcastReceiver{
	
	
	public TracerServiceBroadcastReceiver() {
		super();
		Log.i("TracerServiceBroadcastReceiver", "broadcastReceiver started !");
	}
	
	public void handleIntent(Intent intent){
		
		synchronized (GlobalData.getFragmentManager()) {
				
			Fragment currentFragment = GlobalData.getFragmentManager().getCurrentFragment(); 
			
			if(currentFragment.getClass().equals(LastImpressionFragment.class)){
				
				Picture picture = GlobalData.getDatabase().getLastPicture();
				if(picture != null)
					((LastImpressionFragment) currentFragment).loadPictureIntoView(picture);						
			}
		}
	}
	
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		Bundle extras = intent.getExtras();
		
		if(intent.getAction().equals("ro.vadim.picturetrace.NewPicture")){
			
			final Intent thisIntent = intent;
			GlobalData.getActivity().runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					handleIntent(thisIntent);					
				}
			});			
		}
	}
}
