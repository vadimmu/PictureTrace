package ro.vadim.picturetrace.utils;


import ro.vadim.picturetrace.trace.Tracer;
import ro.vadim.picturetrace.trace.TracerService;
import ro.vadim.picturetrace.visuals.BoilerplateFragmentManager;
import ro.vadim.picturetrace.visuals.FragmentManager;
import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class GlobalData {
	
	private static Activity activity = null;	
	private static TracerService tracerService = null;
	private static BoilerplateFragmentManager fragmentManager = null;
	
	
	
	
	
	public static void initGlobal(FragmentActivity activity){
		if(getFragmentManager() == null)
			setFragmentManager(new FragmentManager());
		
		getFragmentManager().setActivity(activity);
		setActivity(activity);
		tracerService = new TracerService();		
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
