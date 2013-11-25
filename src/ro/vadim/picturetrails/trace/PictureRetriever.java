package ro.vadim.picturetrails.trace;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import ro.vadim.picturetrails.utils.Picture;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class PictureRetriever {

	
	private static final String DEFAULT_ALBUM_NAME = "PictureTrace";
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	
	
	private final int DEFAULT_OFFSET = 50; //meters
	private final double DEFAULT_OFFSET_DEGREES = 0.00045000045;
	private final int EARTH_RADIUS = 6378137; //meters
	private final int ONE_DEGREE = 111111; //meters
	
	private final int PICTURES_FROM = 0;
	private final int PICTURES_TO = 20;
	private JsonParser parser = null;	
	private int offset = DEFAULT_OFFSET;
	
	private HttpRequester httpRequester = null;
	
	
	
	public PictureRetriever(Integer positionRange){
		if(positionRange != null)
			offset = positionRange;
		
		
		httpRequester = new HttpRequester();
		parser = new JsonParser();
	}
	
	
	
	
	
	public double[] getPositionRanges_DEFAULT_OFFSET_DEGREES(Location initialLocation){
		
		double [] ranges = new double[4];
		
		ranges[0] = initialLocation.getLatitude() - DEFAULT_OFFSET_DEGREES;
		ranges[1] = initialLocation.getLongitude() - DEFAULT_OFFSET_DEGREES;
				
		ranges[2] = initialLocation.getLatitude() + DEFAULT_OFFSET_DEGREES;
		ranges[3] = initialLocation.getLongitude() + DEFAULT_OFFSET_DEGREES;
		
		return ranges;
		
	}
	
	public double[] getPositionRanges(Location initialLocation, Integer offset){
		
				
		Log.i("Tracer", "getPositionRanges(): offset = "+String.valueOf(offset));
		Log.i("Tracer", "getPositionRanges(): offset/ONE_DEGREE = "+String.valueOf((double)offset / ONE_DEGREE));
		
		double [] ranges = new double[4];
		
		double degreeoffset =(double)offset / ONE_DEGREE;
						
		ranges[0] = initialLocation.getLatitude() - degreeoffset;
		ranges[1] = initialLocation.getLongitude() - degreeoffset;
				
		ranges[2] = initialLocation.getLatitude() + degreeoffset;
		ranges[3] = initialLocation.getLongitude() + degreeoffset;
		
		return ranges;
		
	}
		
	public String getResponseString(double minLatitude, double minLongitude, double maxLatitude, double maxLongitude){
				
		if(httpRequester == null)
			httpRequester = new HttpRequester();
		
		
		Log.i("TracerService", "getResponseString()");
		
		String minLatitudeString = String.valueOf(minLatitude);
		String maxLatitudeString = String.valueOf(maxLatitude);
		
		
		String minLongitudeString = String.valueOf(minLongitude);
		String maxLongitudeString = String.valueOf(maxLongitude);
		
		Log.i("TracerService", "getResponseString(): between "+minLatitudeString+", "+minLongitudeString+
															" and "+maxLatitudeString+", "+maxLongitudeString);
		
		
		String url = "http://www.panoramio.com/map/get_panoramas.php?"+
		"set=public&"+
		"from="+String.valueOf(PICTURES_FROM)+"&"+
		"to="+String.valueOf(PICTURES_TO)+"&"+
		"minx="+minLongitudeString+"&"+
		"miny="+minLatitudeString+"&"+
		"maxx="+maxLongitudeString+"&"+
		"maxy="+maxLatitudeString+"&"+
		"size=small&mapfilter=true";
		
		Log.i("TracerService", "getResponseString(): request url: "+url);
		try {			
			return httpRequester.sendGet(url);
		}
		
		
		catch (Exception e) {
			System.out.println("requester.sendGet(): "+e.toString());
			e.printStackTrace();
			return null;
		}		
	}
	
	public ArrayList<Picture> getPictures(String responseString){
		
		Log.i("TracerService", "getPictures(): responseString: "+responseString);
		
		ArrayList<Picture> pictures = new ArrayList<Picture>(PICTURES_TO - PICTURES_FROM + 1);
				
		Map<String, Object> largeJSON = parser.extractObject(responseString);		
		ArrayList<Map> photoData = parser.extractArrayOfObjects(largeJSON, "photos");
		
		if( photoData != null){
			
			for(Map photo : photoData){			
				pictures.add(new Picture(
						(String)photo.get("photo_file_url"),
						(String)photo.get("photo_title"),
						(Double)photo.get("latitude"),
						(Double)photo.get("longitude"),
						null
				));
			}
		}
		
		Log.i("Tracer", "getPictures(): "+String.valueOf(pictures.size())+" retrieved pictures");
		return pictures;		
	}
	
	public Picture getFirstPictureInfo(String responseString){
		
		ArrayList<Picture> pictures = getPictures(responseString);
		if(pictures.size() > 0){
			Log.i("Tracer", "getFirstPicture(): "+pictures.get(0).getUrl());			
			return pictures.get(0);
		}
		
		Log.i("Tracer", "getFirstPicture(): NULL");
		return null;
		
	}
	
	public Picture getFirstPictureInfo(Location myLocation){
		
		Log.i("TracerService", "getFirstPicture(): location: "+
				String.valueOf(myLocation.getLatitude())+ ", "+
				String.valueOf(myLocation.getLongitude()));
		
		double[] ranges = getPositionRanges(myLocation, offset);
		String responseString = getResponseString(
				ranges[0], ranges[1], 
				ranges[2], ranges[3]);
		
		return getFirstPictureInfo(responseString);
		
	}
	
	public Picture getRandomPictureInfo(String responseString){
		ArrayList<Picture> pictures = getPictures(responseString);		
		if(pictures.size() > 0){
			
			int index = (int)(Math.random()*pictures.size());			
			Log.i("Tracer", "getFirstPicture(): "+pictures.get(index).getUrl());			
			return pictures.get(index);
		}
		Log.i("Tracer", "getRandomPicture(): NULL");
		return null;
	}
	
	public Picture getRandomPictureInfo(Location myLocation){
		Log.i("TracerService", "getFirstPicture(): location: "+
				String.valueOf(myLocation.getLatitude())+ ", "+
				String.valueOf(myLocation.getLongitude()));
		
		double[] ranges = getPositionRanges(myLocation, offset);
		String responseString = getResponseString(
				ranges[0], ranges[1], 
				ranges[2], ranges[3]);
		
		return getRandomPictureInfo(responseString);		
	}
	
	
	
	

	
	public static File getAlbumStorageDir(String albumName) {
		// TODO Auto-generated method stub
		return new File(
		  Environment.getExternalStoragePublicDirectory(
		    Environment.DIRECTORY_PICTURES
		  ), 
		  albumName
		);
	}
		
	public static File getAlbumDir() throws IOException {
		File storageDir = null;

		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			Log.i("TracerService", "the external storage is mounted...");
			storageDir = getAlbumStorageDir(DEFAULT_ALBUM_NAME);
			if(!storageDir.exists())
				storageDir.mkdirs();
						
			Log.i("TracerService", "the external storage directory: " + storageDir.getCanonicalPath());
			
			if (storageDir != null) {
				if (! storageDir.mkdirs()) {
					if (! storageDir.exists()){
						Log.d("TracerService", "failed to create picture directory");
						return null;
					}
				}
			}
			
		} 
		else {
			Log.v("TracerService", "External storage is not mounted READ/WRITE.");
		}
		
		return storageDir;
	}
	
	private File createImageFile(Picture picture) throws IOException {
		// Create an image file name
		
		Date newDate = new Date();
		
		String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(picture.getTimestamp());
		
		String imageFileName = JPEG_FILE_PREFIX + timeStamp;
		
		Log.i("TracerService", "createImageFile(): imageFileName: "+imageFileName);
				
		File albumF = getAlbumDir();
		File imageF = new File(albumF+"/"+imageFileName+JPEG_FILE_SUFFIX);
		
		
		Log.i("TracerService", "createImageFile(): album name: "+albumF.getCanonicalPath());
		Log.i("TracerService", "createImageFile(): image name: "+imageF.getCanonicalPath());
		
		return imageF;
	}
	
	
	
	public File savePictureToFile(Picture picture) throws IOException{
		
		if(picture == null){
			Log.i("TracerService", "onLocationChanged(): no picture available for this location !");
			return null;
		}
				
		File pictureFile = createImageFile(picture);	
		httpRequester.getPicture(picture.getUrl(), pictureFile);
				
		
		return pictureFile;		
	}
	
	public File savePictureToFile(Location location) throws IOException{
		Picture picture = getRandomPictureInfo(location);		
		return savePictureToFile(picture);
	}	
	
	
	
}
