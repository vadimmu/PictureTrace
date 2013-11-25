package ro.vadim.picturetrails.test;

import ro.vadim.picturetrails.utils.GlobalData;
import android.content.Context;
import android.location.LocationManager;
import android.util.Log;

public class MockLocationRunnable implements Runnable{
	private int SLEEP_TIME = 1000; 
	private boolean stopped = false;
	private MockLocationProvider mock = null;
	private Context ctx = null;
	
	
	public MockLocationRunnable(Context context) {
		this.ctx = context;
	}
	
	
	
	
	
	@Override
	public void run() {
		
		Log.e("MockLocationRunnable", "run()");
		
		mock = new MockLocationProvider(LocationManager.NETWORK_PROVIDER, ctx);		
		
		while(!isStopped()){
			
			try {
				Thread.sleep(SLEEP_TIME);				
				double[] mockLatLong = mock.generateRandomLocation();
				mock.pushLocation(mockLatLong[0], mockLatLong[1]);
			}
			
			catch (InterruptedException e) {
				Log.e("MockLocationRunnable", "run(): can't sleep: "+e.toString());
				e.printStackTrace();
			}
			
			
			
		}
		
		mock.shutdown();
	}

	
	
	
	

	public boolean isStopped() {
		return stopped;
	}


	public void setStopped(boolean stopped) {
		Log.e("MockLocationRunnable", "setStopped( "+String.valueOf(stopped)+" )");
		this.stopped = stopped;
	}

}
