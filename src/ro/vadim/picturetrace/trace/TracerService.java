package ro.vadim.picturetrace.trace;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.Observer;



import ro.vadim.picturetrace.test.MockLocationProvider;
import ro.vadim.picturetrace.test.MockLocationRunnable;
import ro.vadim.picturetrace.utils.Picture;
import ro.vadim.picturetrace.utils.Utils;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class TracerService extends Service{
	
	
	private static final String DEFAULT_ALBUM_NAME = "PictureTrace";
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	private final int DEFAULT_PICTURE_TRACE_DISTANCE = 10; //meters
	private final int DEFAULT_OFFSET = 50; //meters
	private final double DEFAULT_OFFSET_DEGREES = 0.00045000045;
	private final int EARTH_RADIUS = 6378137; //meters
	private final int ONE_DEGREE = 111111; //meters	
	
		
	
	private static long minTimeMillis = 2000;
	private static long minDistanceMeters = 1;
	private static float minAccuracyMeters = 200;
	
	
	private final int PICTURES_FROM = 0;
	private final int PICTURES_TO = 20;
	
	
	private LocationManager locationManager = null;
	private LocationListener locationListener = null;
	private JsonParser parser = null;	
	
	
	private Location lastLocation = null;
	private Picture lastPicture = null;
	
	
		
	private HttpRequester httpRequester = null;
	
	
	private boolean paused = false;
	private boolean stopped = false;
	
	
	private LinkedList<Picture> pictures = null;	
	
	/////
	Thread mockLocationsThread = null;
	MockLocationRunnable mockLocationRunnable = null;	
	/////
	
	

	
	
	private void initPictureRetrieval(){
		
		//////////TEST ////////////
		
		
		mockLocationRunnable = new MockLocationRunnable(this);
		mockLocationsThread = new Thread(mockLocationRunnable);
		mockLocationsThread.start();
		
		
		
		/////////////////////////////
		
		
		
		
		
		
		
		
		
		Log.i("TracerService", "initPictureRetrieval()");
				
		parser = new JsonParser();
		
		
		locationManager = (LocationManager) getApplicationContext().
				getSystemService(Context.LOCATION_SERVICE);
		
		
				
		final TracerService thisService = this;
		
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
								
				Log.i("TracerService", "LocationListener.onLocationChanged(): LOCATION CHANGED !");
				Log.i("TracerService", "new location: "+String.valueOf(location.getLatitude())+", "+String.valueOf(location.getLongitude()));
				Toast.makeText(thisService, "TracerService: location changed !", Toast.LENGTH_SHORT);
				
				if(getLastLocation() == null){
					Log.i("TracerService", "LastLocation = null !");					
				}
				
				else if(getDistanceBetweenPositions(location, getLastLocation()) >= DEFAULT_PICTURE_TRACE_DISTANCE){
					
					Log.i("TracerService", "LocationListener.onLocationChanged(): location changed and satisfies the distance crieria!");
					
					final Location thisLocation = location;
					
					Thread retrievePictureThread = new Thread(new Runnable() {
						
						@Override
						public void run() {
							
							try {							
								
								Picture picture = getRandomPicture(thisLocation);
																
								if(picture == null){
									Log.i("TracerService", "onLocationChanged(): no picture available for this location !");
									return;
								}
								
								Log.i("TracerService", "GOT PICTURE !");
								Log.i("TracerService", picture.url);
								Log.i("TracerService", String.valueOf(picture.latitude)+" "+String.valueOf(picture.longitude));
																
								File pictureFile = createImageFile();							
								httpRequester.getPicture(picture.url, pictureFile);								
								galleryAddPic(pictureFile);
							}
							catch (IOException e) {
								Log.i("TracerService", "traceRunnable.createImageFile(): IOException: "+e.toString());
								e.printStackTrace();
							}
						}
					});
					
					retrievePictureThread.start();
				}
				
				setLastLocation(location);
			}
		};
				
		// MockLocationProvider is set as LocationManager.NETWORK_PROVIDER
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 
				minTimeMillis, 
				minDistanceMeters,
				locationListener);
		
		Location lastLocation = locationManager.getLastKnownLocation(MockLocationProvider.providerName);
		if(lastLocation != null)
			Log.i("TracerService", "initPictureRetrieval(): last known location: "+String.valueOf(lastLocation));
		
	}
		
	
		
	private void init(){
		
		Log.i("TracerService", "init()");
		pictures = new LinkedList<Picture>();
		httpRequester = new HttpRequester();
		initPictureRetrieval();		
	}
	
	
	public TracerService() {
		
		Log.i("TracerService", "service object initialized");
		
	}
	
	
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {		
		Toast.makeText(this, " TracerService has started", Toast.LENGTH_LONG).show();		
		return super.onStartCommand(intent, flags, startId);
	}
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		Toast.makeText(this, "TracerService has been created", Toast.LENGTH_LONG).show();
		init();		
	}
	
	
	@Override
	public void onDestroy() {
		if(mockLocationsThread != null){
			if(mockLocationsThread.isAlive())
				mockLocationRunnable.setStopped(true);
		}		
		Toast.makeText(this, "TracerService has been destroyed", Toast.LENGTH_LONG).show();
	}
	
	
	
	
	
	
		
	
	private void galleryAddPic(File newFile) {
		
		Log.i("TracerService", "galleryAddPic(): attempting to add the picture to the gallery...");
		
	    Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");		
	    Uri contentUri = Uri.fromFile(newFile);
	    
	    Log.i("TracerService", "galleryAddPic(): Picture URI: "+contentUri.toString());
	    
	    mediaScanIntent.setData(contentUri);
	    sendBroadcast(mediaScanIntent);
	}
		
	
	public File getAlbumStorageDir(String albumName) {
		// TODO Auto-generated method stub
		return new File(
		  Environment.getExternalStoragePublicDirectory(
		    Environment.DIRECTORY_PICTURES
		  ), 
		  albumName
		);
	}
	
	
	private File getAlbumDir() throws IOException {
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

	
	private File createImageFile() throws IOException {
		// Create an image file name
		
		Date newDate = new Date();
		
		
		
		String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
		String imageFileName = JPEG_FILE_PREFIX + timeStamp;
		
		Log.i("TracerService", "createImageFile(): imageFileName: "+imageFileName);
		
		
		File albumF = getAlbumDir();
		File imageF = new File(albumF+"/"+imageFileName+JPEG_FILE_SUFFIX);
		
		
		Log.i("TracerService", "createImageFile(): album name: "+albumF.getCanonicalPath());
		Log.i("TracerService", "createImageFile(): image name: "+imageF.getCanonicalPath());
		
		return imageF;
	}
	
	
	
	
	
	
	
	
	
	
	
	public boolean isPaused() {
		return paused;
	}

	public void setPaused(boolean pause) {
		this.paused = pause;
	}

	public boolean isStopped() {
		return stopped;
	}

	public void setStopped(boolean stop) {
		this.stopped = stop;
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
	
	
	
	private double[] getPositionRanges_DEFAULT_OFFSET_DEGREES(Location initialLocation){
		
		double [] ranges = new double[4];
		
		ranges[0] = initialLocation.getLatitude() - DEFAULT_OFFSET_DEGREES;
		ranges[1] = initialLocation.getLongitude() - DEFAULT_OFFSET_DEGREES;
				
		ranges[2] = initialLocation.getLatitude() + DEFAULT_OFFSET_DEGREES;
		ranges[3] = initialLocation.getLongitude() + DEFAULT_OFFSET_DEGREES;
		
		return ranges;
		
	}
	
	private double[] getPositionRanges(Location initialLocation, Integer offset){
		
		
		if(offset == null)		
			offset = DEFAULT_OFFSET;
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
	
	
	
	
	
	private String getResponseString(double minLatitude, double minLongitude, double maxLatitude, double maxLongitude){
		/* One minute approx= 1 mile*/
		
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
		"set=full&"+
		"from="+String.valueOf(PICTURES_FROM)+"&"+
		"to="+String.valueOf(PICTURES_TO)+"&"+
		"minx="+minLongitudeString+"&"+
		"miny="+minLatitudeString+"&"+
		"maxx="+maxLongitudeString+"&"+
		"maxy="+maxLatitudeString+"&"+
		"size=medium&mapfilter=true";
		
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
	
	private ArrayList<Picture> getPictures(String responseString){
		
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
						(Double)photo.get("longitude")
				));
			}
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
		
		Log.i("TracerService", "getFirstPicture(): location: "+
				String.valueOf(myLocation.getLatitude())+ ", "+
				String.valueOf(myLocation.getLongitude()));
		
		double[] ranges = getPositionRanges(myLocation, null);
		String responseString = getResponseString(
				ranges[0], ranges[1], 
				ranges[2], ranges[3]);
		
		return getFirstPicture(responseString);
		
	}
	
	private Picture getRandomPicture(String responseString){
		ArrayList<Picture> pictures = getPictures(responseString);		
		if(pictures.size() > 0){
			
			int index = (int)(Math.random()*pictures.size());			
			Log.i("Tracer", "getFirstPicture(): "+pictures.get(index).url);			
			return pictures.get(index);
		}
		Log.i("Tracer", "getRandomPicture(): NULL");
		return null;
	}
	
	private Picture getRandomPicture(Location myLocation){
		Log.i("TracerService", "getFirstPicture(): location: "+
				String.valueOf(myLocation.getLatitude())+ ", "+
				String.valueOf(myLocation.getLongitude()));
		
		double[] ranges = getPositionRanges(myLocation, null);
		String responseString = getResponseString(
				ranges[0], ranges[1], 
				ranges[2], ranges[3]);
		
		return getRandomPicture(responseString);		
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
