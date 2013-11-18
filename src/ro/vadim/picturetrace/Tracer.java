package ro.vadim.picturetrace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;


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
	
	
	
	private Location previousLocation = null;
	private Picture previousPicture = null;
	
	
	
	
	
	public Tracer() {
		
		parser = new JsonParser();
		
		locationManager = (LocationManager) GlobalData.getActivity().
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
				
				if(previousLocation == null){
					previousLocation = location;					
					previousPicture = getFirstPicture(location);					
				}
			}
		};
		
		
	}
	
	
	
	
	
	
	
	
	public double[] getPositionRanges(Location initialLocation, Integer offset){
		
		if(offset == null)		
			offset = DEFAULT_OFFSET;
				
		double [] ranges = new double[4];
		
		double offsetLatitude = initialLocation.getLatitude() + (offset / ONE_DEGREE); 
		double offsetLongitude = initialLocation.getLongitude() + (offset / ONE_DEGREE);
				
		ranges[0] = initialLocation.getLatitude() - offsetLatitude;
		ranges[1] = initialLocation.getLatitude() + offsetLatitude;
		
		ranges[2] = initialLocation.getLongitude() - offsetLongitude;
		ranges[3] = initialLocation.getLongitude() + offsetLongitude;
		
		return ranges;
		
	}
	
	
	
	
	
	public String getResponseString(double minLatitude, double maxLatitude, double minLongitude, double maxLongitude){
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
	
	public ArrayList<Picture> getPictures(String responseString){
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
		
		return pictures;	
		
	}
	
	public Picture getFirstPicture(String responseString){
		
		ArrayList<Picture> pictures = getPictures(responseString);
		if(pictures.size() > 0)
			return pictures.get(0);
		
		return null;
		
	}
	
	public Picture getFirstPicture(Location myLocation){
		
		double[] ranges = getPositionRanges(myLocation, null);
		String responseString = getResponseString(
				ranges[0], ranges[1], 
				ranges[2], ranges[3]);
		
		return getFirstPicture(responseString);
		
	}
	
	
	
	
	
	
	
	
}
