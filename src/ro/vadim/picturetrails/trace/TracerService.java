package ro.vadim.picturetrails.trace;

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



import ro.vadim.picturetrails.database.DatabaseHelper;
import ro.vadim.picturetrails.test.MockLocationProvider;
import ro.vadim.picturetrails.test.MockLocationRunnable;
import ro.vadim.picturetrails.utils.Picture;
import ro.vadim.picturetrails.utils.Utils;
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


public class TracerService extends Service{
	
	private static String TAG = "TracerService";
	
	private final int DEFAULT_PICTURE_SEARCH_RANGE = 50;//meters
	private final int DEFAULT_PICTURE_TRACE_DISTANCE = 100; //meters
	private int pictureTraceDistance = DEFAULT_PICTURE_TRACE_DISTANCE;
	
	
	private Location lastLocation = null;
	private Picture lastPicture = null;
	private LinkedList<Picture> pictures = new LinkedList<Picture>();
	private PictureRetriever pictureRetriever = null;
	private DatabaseHelper db = null;
	
	//Location management and retrieval
	private static long minTimeMillis = 2000;
	private static long minDistanceMeters = 1;
	private static float minAccuracyMeters = 200;
	
	private LocationManager locationManager = null;
	private LocationListener locationListener = null;
	
	//lifecycle management
	private boolean paused = false;
	private boolean stopped = false;
	private boolean testRun = false;
	
	//testing
	Thread mockLocationsThread = null;
	MockLocationRunnable mockLocationRunnable = null;
	
	
		
	
	
	private void initPictureRetrieval(boolean test){
		
		
		if(test){		
			mockLocationRunnable = new MockLocationRunnable(this);
			mockLocationsThread = new Thread(mockLocationRunnable);
			mockLocationsThread.start();
		}
			
		Log.i(TAG, "initPictureRetrieval()");
		
		
		
		
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
								
				Log.i(TAG, "LocationListener.onLocationChanged(): LOCATION CHANGED !");				
				
				if(getLastLocation() == null){
					Log.i(TAG, "LastLocation = null !");
					setLastLocation(location);
				}
				
				else if(getDistanceBetweenPositions(location, getLastLocation()) >= pictureTraceDistance){
					
					Log.i(TAG, "LocationListener.onLocationChanged(): location changed and satisfies the distance crieria!");					
					final Location thisLocation = location;
					
					Thread retrievePictureThread = new Thread(new Runnable() {
						
						@Override
						public void run() {
							
							
							Picture picture = pictureRetriever.getRandomPictureInfo(thisLocation);				
							
							Log.i(TAG, "inserting a picture into the database");
							db.insertPicture(picture);
							
							if(picture != null){									
								Intent pictureIntent = new Intent("ro.vadim.picturetrace.NewPicture");
								sendBroadcast(pictureIntent);
							}
							
						}
					});
					
					retrievePictureThread.start();
					setLastLocation(location);
				}
			}
		};
		
		
		if(test){
			// MockLocationProvider is set as LocationManager.NETWORK_PROVIDER
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 
					minTimeMillis, 
					minDistanceMeters,
					locationListener);	
		}
		
		else{
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 
					minTimeMillis, 
					minDistanceMeters,
					locationListener);
			
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 
					minTimeMillis, 
					minDistanceMeters,
					locationListener);
		}
	}
	
	
	
	
	
	public TracerService() {
		
		pictureRetriever = new PictureRetriever(DEFAULT_PICTURE_SEARCH_RANGE);
		db = new DatabaseHelper(this);		
		Log.i(TAG, "TracerService object initialized");
		
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
		
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {		
		return START_STICKY;
	}
		
	@Override
	public void onCreate() {
		super.onCreate();		
		Log.i(TAG, "initPictureRetrieval()");
		initPictureRetrieval(testRun);
	}
		
	@Override
	public void onDestroy() {
		if(mockLocationsThread != null){
			if(mockLocationsThread.isAlive())
				mockLocationRunnable.setStopped(true);
		}
		
		locationManager.removeUpdates(locationListener);		
		
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
