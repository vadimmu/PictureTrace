package ro.vadim.picturetrace.utils;


import ro.vadim.picturetrace.trace.Tracer;
import ro.vadim.picturetrace.trace.TracerService;
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
	private static TracerService tracerService = null;
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
		
		tracerService = new TracerService();
		broadcastReceiver = new TracerServiceBroadcastReceiver();
		intentFilter = new IntentFilter("ro.vadim.picturetrace.NewPicture");
		//getActivity().registerReceiver(broadcastReceiver, intentFilter);
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
	

}
