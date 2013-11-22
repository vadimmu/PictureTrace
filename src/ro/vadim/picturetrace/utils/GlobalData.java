package ro.vadim.picturetrace.utils;


import java.util.LinkedList;

import ro.vadim.picturetrace.visuals.BoilerplateFragmentManager;
import ro.vadim.picturetrace.visuals.FragmentManager;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class GlobalData {
	
	private static Activity activity = null;
	
	private static BoilerplateFragmentManager fragmentManager = null;
	private static TracerServiceBroadcastReceiver broadcastReceiver = null;
	
	private static IntentFilter intentFilter = null;
	private static ServiceConnection serviceConnection = null;
	private static boolean initialized = false;	
	private static LinkedList<String> pictureURLs = new LinkedList<String>();
		
	public static void initGlobal(FragmentActivity activity){
		
		pictureURLs.add("http://static.panoramio.com/photos/large/2140192.jpg");
		pictureURLs.add("http://static.panoramio.com/photos/large/76123992.jpg");
		
		if(isInitialized()){
			Log.i("GlobalData", "the global data components are already initialized !");			
			return;
		}
		if(getFragmentManager() == null)
			setFragmentManager(new FragmentManager());
		
		getFragmentManager().setActivity(activity);
		setActivity(activity);		
		
		broadcastReceiver = new TracerServiceBroadcastReceiver();
		getActivity().registerReceiver(broadcastReceiver, new IntentFilter("ro.vadim.picturetrace.NewPicture"));
		
		
		if(Utils.isTracerServiceRunning(getActivity())){
			Log.i("GlogalData", "TracerService is running !");
		}
		
		setInitialized(true);
	}
	
	
	
	
	
	public static Activity getActivity() {
		return activity;
	}

	public static void setActivity(Activity activity) {
		GlobalData.activity = activity;
	}
	
	public static BoilerplateFragmentManager getFragmentManager() {
		return fragmentManager;
	}

	public static void setFragmentManager(BoilerplateFragmentManager fragmentManager) {
		GlobalData.fragmentManager = fragmentManager;
	}

	public static IntentFilter getIntentFilter() {
		return intentFilter;
	}

	public static void setIntentFilter(IntentFilter intentFilter) {
		GlobalData.intentFilter = intentFilter;
	}

	public static boolean isInitialized() {
		return initialized;
	}

	public static void setInitialized(boolean initialized) {
		GlobalData.initialized = initialized;
	}
	
	public static LinkedList<String> getPictureURLs() {
		return pictureURLs;
	}
	
	public static void setPictureURLs(LinkedList<String> pictureURLs) {
		GlobalData.pictureURLs = pictureURLs;
	}


	

}
