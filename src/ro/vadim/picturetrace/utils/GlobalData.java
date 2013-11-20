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
	private static TracerServiceBroadcastReceiver broadcastReceiver = null;
	private static IntentFilter intentFilter = null;
	private static ServiceConnection serviceConnection = null;
		
	
	private static void initServiceConnection(){
		
		serviceConnection = new ServiceConnection() {				
			@Override
			public void onServiceDisconnected(ComponentName name) {
				Log.i("GlobalData", "ServiceConenction disconnected");
			}
			
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				Log.i("GlobalData", "ServiceConenction connected");					
			}
		};
	}
	
	public static void initGlobal(FragmentActivity activity){
		
				
		if(getFragmentManager() == null)
			setFragmentManager(new FragmentManager());
		
		getFragmentManager().setActivity(activity);
		setActivity(activity);
		initServiceConnection();
		
		if(Utils.isTracerServiceRunning(getActivity())){
			Log.i("GlogalData", "TracerService is running !");
		}
		
		setBroadcastReceiver(new TracerServiceBroadcastReceiver());
		setIntentFilter(new IntentFilter("ro.vadim.picturetrace.NewPicture"));
		
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

	public static TracerServiceBroadcastReceiver getBroadcastReceiver() {
		return broadcastReceiver;
	}

	public static void setBroadcastReceiver(TracerServiceBroadcastReceiver broadcastReceiver) {
		GlobalData.broadcastReceiver = broadcastReceiver;
	}

	public static IntentFilter getIntentFilter() {
		return intentFilter;
	}

	public static void setIntentFilter(IntentFilter intentFilter) {
		GlobalData.intentFilter = intentFilter;
	}


	

}
