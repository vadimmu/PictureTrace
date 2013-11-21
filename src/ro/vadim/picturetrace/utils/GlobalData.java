package ro.vadim.picturetrace.utils;


import ro.vadim.picturetrace.visuals.BoilerplateFragmentManager;
import ro.vadim.picturetrace.visuals.FragmentManager;
import android.app.Activity;
import android.content.ComponentName;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class GlobalData {
	
	private static Activity activity = null;
	
	private static BoilerplateFragmentManager fragmentManager = null;	
	private static IntentFilter intentFilter = null;
	private static ServiceConnection serviceConnection = null;
	private static boolean initialized = false;	
	
		
	public static void initGlobal(FragmentActivity activity){
		
		if(isInitialized()){
			Log.i("GlobalData", "the global data components are already initialized !");			
			return;
		}
		if(getFragmentManager() == null)
			setFragmentManager(new FragmentManager());
		
		getFragmentManager().setActivity(activity);
		setActivity(activity);		
		
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


	

}
