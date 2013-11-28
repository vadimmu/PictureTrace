package ro.vadim.picturetrails.test;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.SystemClock;
import android.util.Log;

public class MockLocationProvider {
	
	public static String TAG = "MockLocationProvider";
	public static String providerName;
	Location completeLocationObject = null;
	Context ctx;
	
	
	public double testMinLatitude = 25.076944;
	public double testMaxLatitude = 25.079722;
	
	
	public double testMinLongitude = 55.129444;
	public double testMaxLongitude = 55.138889;
	
	
 
	public MockLocationProvider(String name, Context ctx) {
		this.providerName = name;
		this.ctx = ctx;
		
		LocationManager lm = (LocationManager) ctx.getSystemService(
	    Context.LOCATION_SERVICE);
		
		completeLocationObject = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		
	    lm.addTestProvider(providerName, false, false, false, false, false,
	      true, true, 0, 5);
	    lm.setTestProviderEnabled(providerName, true);
	}
	
	
	

	public double[] generateRandomLocation(){
		
		double[] latLong = new double[2];
		
		latLong[0] = testMinLatitude + Math.random() * (testMaxLatitude - testMinLatitude);
		latLong[1] = testMinLongitude + Math.random() * (testMaxLongitude - testMinLongitude);
				
		return latLong;
	}
	
	public void pushLocation(double lat, double lon) {
	    
		try{
			LocationManager lm = (LocationManager) ctx.getSystemService(
		      Context.LOCATION_SERVICE);	    
		    
		    Location mockLocation = new Location(providerName);
		    	    
		    mockLocation.setProvider(providerName);
		    mockLocation.setAltitude(100);
		    mockLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
		    
		    mockLocation.setLatitude(lat);
		    mockLocation.setLongitude(lon);
		    	    
		    mockLocation.setTime(System.currentTimeMillis());	    
		    mockLocation.setAccuracy(10);
		    
		    lm.setTestProviderLocation(providerName, mockLocation);
		}
		catch(IllegalArgumentException e){
			Log.e(TAG, "pushLocation(): IllegalArgumentException: "+e.toString());
			e.printStackTrace();
		}
		
	}
	 
	public void shutdown() {
		try{
		    LocationManager lm = (LocationManager) ctx.getSystemService(
		      Context.LOCATION_SERVICE);
		    lm.removeTestProvider(providerName);
		}
		catch(IllegalArgumentException e){
			Log.e(TAG, "shutdown(): IllegalArgumentException: "+e.toString());
			e.printStackTrace();
		}
	}
}
