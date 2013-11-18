package ro.vadim.picturetrace;

import android.app.Activity;

public class GlobalData {
	
	private static Activity activity = null;	
	private static Tracer tracer = null;
	
	
	
	
	
	public static void initGlobal(Activity activity){
		setActivity(activity);
		tracer = new Tracer();		
	}

	
	
	
	
	public static Activity getActivity() {
		return activity;
	}


	public static void setActivity(Activity activity) {
		GlobalData.activity = activity;
	}
	
	

}
