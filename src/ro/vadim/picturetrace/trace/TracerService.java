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



import ro.vadim.picturetrace.utils.Picture;
import ro.vadim.picturetrace.utils.Utils;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
	private final int DEFAULT_PICTURE_TRACE_DISTANCE = 100; //meters
	private final int DEFAULT_OFFSET = 50; //meters
	private final int EARTH_RADIUS = 6378137; //meters
	private final int ONE_DEGREE = 111111; //meters	
	
	
	private final int PICTURES_FROM = 0;
	private final int PICTURES_TO = 20;
	
	
	private LocationManager locationManager = null;
	private LocationListener locationListener = null;
	private JsonParser parser = null;	
	
	
	private Location lastLocation = null;
	private Picture lastPicture = null;
	
	
		
	private HttpRequester httpRequester = null;
	private Runnable traceRunnable = null;
	private Thread traceThread = null;
	
	
	private boolean paused = false;
	private boolean stopped = false;
	IBinder binder = new TracerBinder();	
	
	private LinkedList<Picture> pictures = null;	
	
	
	private void broadcastIntentNewPicture(Picture picture){
				
		Intent intent = new Intent();
		intent.setAction("ro.vadim.picturetrace.NewPicture");
		intent.putExtra("url", picture.url);
		intent.putExtra("longitude", picture.longitude);
		intent.putExtra("latitude", picture.latitude);
		intent.putExtra("description", picture.description);		
		sendBroadcast(intent);
		
	}
	
	
	
	private void initPictureRetrieval(){
		
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
				
				Toast.makeText(thisService, "TracerService: location changed !", Toast.LENGTH_SHORT);
				
				if(getLastLocation() == null){
					setLastLocation(location);					
					setLastPicture(getFirstPicture(location));
				}
								
				else if(getDistanceBetweenPositions(location, getLastLocation()) >= DEFAULT_PICTURE_TRACE_DISTANCE){
					try {
						Log.i("TracerService", "LocationListener.onLocationChanged(): location changed and satisfies the distance crieria!");
						
						setLastLocation(location);
						setLastPicture(getFirstPicture(location));
						pictures.add(lastPicture);
						File pictureFile = createImageFile();							
						httpRequester.getPicture(lastPicture.url, pictureFile);	
						galleryAddPic(pictureFile);
					}
					
					catch (IOException e) {
						Log.i("TracerService", "traceRunnable.createImageFile(): IOException: "+e.toString());
						e.printStackTrace();
					}
					
				}				
			}
		};
		
		
	}
		
	private void initTraceRunnable(){
		
		Log.i("TracerService", "setting up traceRunnable");
		traceRunnable = new Runnable() {
			
			@Override
			public void run() {
				Log.i("TracerService", "setting up picture retrieval");
				initPictureRetrieval();					
			}
		};
	}
	
	
	private void init(){
		
		Log.i("TracerService", "init()");
		pictures = new LinkedList<Picture>();
		httpRequester = new HttpRequester();
		//initPictureRetrieval();
		initTraceRunnable();
		setTraceThread(new Thread(traceRunnable));
	}
	
	
	public TracerService() {
		
		Log.i("TracerService", "service object initialized");
		init();
	}
	
	private void run(){
		
		Log.i("TracerService", "run()");		
		traceThread.start();
		
	}
	
	public void stop(){
		setStopped(true);
		synchronized (traceThread) {
			traceThread.notify();
		}
			
	}
	
	
	
	
	
	
	
	
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {		
		Toast.makeText(this, " TracerService has started", Toast.LENGTH_LONG).show();
		run();
		return super.onStartCommand(intent, flags, startId);
	}
	
	
	@Override
	public void onCreate() {		
		Toast.makeText(this, "TracerService has been created", Toast.LENGTH_LONG).show();		
	}
	
	
	@Override
	public void onDestroy() {
		stop();
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
	
	
	
	
	
	
	
	
	
	
	public Thread getTraceThread() {
		return traceThread;
	}

	public void setTraceThread(Thread traceThread) {
		this.traceThread = traceThread;
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
	
	
	
	
	public class TracerBinder extends Binder {
		public TracerService getTracerServiceInstance() {			
			return TracerService.this;
		}
	}
}
