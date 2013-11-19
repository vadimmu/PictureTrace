package ro.vadim.picturetrace.trace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ro.vadim.picturetrace.utils.GlobalData;
import ro.vadim.picturetrace.utils.Picture;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;


public class Tracer {
	
	
	private final int DEFAULT_PICTURE_TRACE_DISTANCE = 100; //meters
	private final int DEFAULT_OFFSET = 50; //meters
	private final int EARTH_RADIUS = 6378137; //meters
	private final int ONE_DEGREE = 111111; //meters	
	
	
	private final int PICTURES_FROM = 0;
	private final int PICTURES_TO = 20;
	
	
	private LocationManager locationManager = null;
	private LocationListener locationListener = null;
	private JsonParser parser = null;
	private TracerService tracerService = null;
	
	
	private Location lastLocation = null;
	private Picture lastPicture = null;
	
	
	
	
	
	public Tracer(TracerService tracerService) {
		
		setTracerService(tracerService);
		
		parser = new JsonParser();
		
		locationManager = (LocationManager) tracerService.
				getSystemService(Context.LOCATION_SERVICE);
		
		locationListener = new LocationListener() {
			
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLocationChanged(Location location) {
				
				if(getLastLocation() == null){
					setLastLocation(location);					
					setLastPicture(getFirstPicture(location));
				}
								
				else if(getDistanceBetweenPositions(location, getLastLocation()) >= DEFAULT_PICTURE_TRACE_DISTANCE){
					
					Log.i("Tracer", "LocationListener.onLocationChanged(): LOCATION CHANGED !");
					
					setLastLocation(location);
					setLastPicture(getFirstPicture(location));
					getTracerService().getTraceThread().notify();
				}				
			}
		};
	}
	
	
	
	
	
	public static double getDistanceBetweenPositions(Location position1, Location position2){
		
		if((position1 != null)&&(position2 != null))
			return getDistanceBetweenPositions(
					position1.getLatitude(), position1.getLongitude(),
					position2.getLatitude(), position2.getLongitude()); 
		
		return -1;		
	}
	
	public static double getDistanceBetweenPositions(double latitude1, double longitude1, double latitude2, double longitude2){

		float [] results = new float[2];
				
		Location.distanceBetween(latitude1, longitude1, latitude2, longitude2, results);
				
		return (double)results[0];
	}
	
	
	
	private double[] getPositionRanges(Location initialLocation, Integer offset){
		
		
		if(offset == null)		
			offset = DEFAULT_OFFSET;
		
		Log.i("Tracer", "getPositionRanges(): offset = "+String.valueOf(offset));
		
		double [] ranges = new double[4];
		
		double offsetLatitude = initialLocation.getLatitude() + (offset / ONE_DEGREE); 
		double offsetLongitude = initialLocation.getLongitude() + (offset / ONE_DEGREE);
				
		ranges[0] = initialLocation.getLatitude() - offsetLatitude;
		ranges[1] = initialLocation.getLatitude() + offsetLatitude;
		
		ranges[2] = initialLocation.getLongitude() - offsetLongitude;
		ranges[3] = initialLocation.getLongitude() + offsetLongitude;
		
		return ranges;
		
	}
	
	
	
	
	
	private String getResponseString(double minLatitude, double maxLatitude, double minLongitude, double maxLongitude){
		/* One minute approx= 1 mile*/
		
		
		HttpRequester requester = new HttpRequester();
		
		
		String minLatitudeString = String.valueOf(minLatitude);
		String maxLatitudeString = String.valueOf(maxLatitude);
		
		
		String minLongitudeString = String.valueOf(minLongitude);
		String maxLongitudeString = String.valueOf(maxLatitude);
		
		
		String url = "http://www.panoramio.com/map/get_panoramas.php?"+
		"set=public&"+
		"from="+String.valueOf(PICTURES_FROM)+"&"+
		"to="+String.valueOf(PICTURES_TO)+"&"+
		"minx="+minLatitudeString+"&"+
		"miny="+minLongitudeString+"&"+
		"maxx="+maxLatitudeString+"&"+
		"maxy="+maxLongitudeString+"&"+
		"size=medium&mapfilter=true";
		
		
		try {
			return requester.sendGet(url);
		}
		
		
		catch (Exception e) {
			System.out.println("requester.sendGet(): "+e.toString());
			e.printStackTrace();
			return null;
		}		
	}
	
	private ArrayList<Picture> getPictures(String responseString){
		ArrayList<Picture> pictures = new ArrayList<Picture>(PICTURES_TO - PICTURES_FROM + 1);
				
		Map<String, Object> largeJSON = parser.extractObject(responseString);		
		ArrayList<Map> photoData = parser.extractArrayOfObjects(largeJSON, "photos");
		
		for(Map photo : photoData){			
			pictures.add(new Picture(
					(String)photo.get("photo_file_url"),
					(String)photo.get("photo_title"),
					Double.valueOf((String)photo.get("latitude")),
					Double.valueOf((String)photo.get("longitude"))
			));
		}
		
		Log.i("Tracer", "getPictures(): "+String.valueOf(pictures.size())+" retrieved pictures");
		return pictures;
		
	}
	
	private Picture getFirstPicture(String responseString){
		
		ArrayList<Picture> pictures = getPictures(responseString);
		if(pictures.size() > 0){
			Log.i("Tracer", "getFirstPicture(): "+pictures.get(0).url);			
			return pictures.get(0);
		}
		
		Log.i("Tracer", "getFirstPicture(): NULL");
		return null;
		
	}
	
	private Picture getFirstPicture(Location myLocation){
		
		double[] ranges = getPositionRanges(myLocation, null);
		String responseString = getResponseString(
				ranges[0], ranges[1], 
				ranges[2], ranges[3]);
		
		return getFirstPicture(responseString);
		
	}
	
	
	
	
	
	
	
	
	
	
	public TracerService getTracerService() {
		return tracerService;
	}

	public void setTracerService(TracerService tracerService) {
		this.tracerService = tracerService;
	}

	public Location getLastLocation() {
		return lastLocation;
	}
	
	public void setLastLocation(Location lastLocation) {
		this.lastLocation = lastLocation;
	}
	
	public Picture getLastPicture() {
		return lastPicture;
	}
	
	public void setLastPicture(Picture lastPicture) {
		this.lastPicture = lastPicture;
	}
	
	
	
	
	
	
	
	
}
