package ro.vadim.picturetrace.test;

import ro.vadim.picturetrace.utils.GlobalData;
import android.location.LocationManager;
import android.util.Log;

public class MockLocationRunnable implements Runnable{
	private int SLEEP_TIME = 1000; 
	private boolean stopped = false;
	private MockLocationProvider mock = null;
	
	@Override
	public void run() {
		
		mock = new MockLocationProvider(LocationManager.NETWORK_PROVIDER, GlobalData.getActivity());		
		
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
		this.stopped = stopped;
	}

}
