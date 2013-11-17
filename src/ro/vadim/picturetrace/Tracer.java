package ro.vadim.picturetrace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Tracer {
	
	private final int PICTURES_FROM = 0;
	private final int PICTURES_TO = 20;
	
	
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
		
		JsonParser parser = new JsonParser();
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
	
	
	
	
	
}
