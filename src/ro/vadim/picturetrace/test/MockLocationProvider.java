package ro.vadim.picturetrace.test;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

public class MockLocationProvider {
	
	String providerName;
	Context ctx;
	
	public double testMinLatitude = 44.466360;
	public double testMaxLatitude = 44.468243;	
	
	public double testMinLongitude = 26.065076;
	public double testMaxLongitude = 26.060439;
	
	
 
	public MockLocationProvider(String name, Context ctx) {
		this.providerName = name;
		this.ctx = ctx;
	 
		LocationManager lm = (LocationManager) ctx.getSystemService(
	    Context.LOCATION_SERVICE);
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
	    LocationManager lm = (LocationManager) ctx.getSystemService(
	      Context.LOCATION_SERVICE);
	 
	    Location mockLocation = new Location(providerName);
	    mockLocation.setLatitude(lat);
	    mockLocation.setLongitude(lon);
	    mockLocation.setAltitude(0);
	    mockLocation.setTime(System.currentTimeMillis());
	    lm.setTestProviderLocation(providerName, mockLocation);
	}
	 
	public void shutdown() {
	    LocationManager lm = (LocationManager) ctx.getSystemService(
	      Context.LOCATION_SERVICE);
	    lm.removeTestProvider(providerName);
	}
}
