package ro.vadim.picturetrace;


public class Tracer {
	
	
	
	public String getResponseString(double minLatitude, double maxLatitude, double minLongitude, double maxLongitude){
		/* One minute approx= 1 mile*/
		
		HttpRequester requester = new HttpRequester();
		
		String minLatitudeString = String.valueOf(minLatitude);
		String maxLatitudeString = String.valueOf(maxLatitude);
				
		String minLongitudeString = String.valueOf(minLongitude);
		String maxLongitudeString = String.valueOf(maxLatitude);
		
		
		String url = "http://www.panoramio.com/map/get_panoramas.php?"+
		"set=public&"+
		"from=0&to=20&"+"" +
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
	
	public void getPictures(){
		
		
		
		
		
		
		
	}
	
	
	
	
	
	
	
	
	
}
