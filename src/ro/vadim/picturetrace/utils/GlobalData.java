package ro.vadim.picturetrace.utils;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import ro.vadim.picturetrace.trace.PictureRetriever;
import ro.vadim.picturetrace.visuals.BoilerplateFragmentManager;
import ro.vadim.picturetrace.visuals.FragmentManager;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class GlobalData {
	
	private static String traceFileName = "tracefile.txt";
	
	private static Activity activity = null;
	
	private static BoilerplateFragmentManager fragmentManager = null;
	private static TracerServiceBroadcastReceiver broadcastReceiver = null;
	
	private static IntentFilter intentFilter = null;
	private static ServiceConnection serviceConnection = null;
	private static boolean initialized = false;	
	private static LinkedList<Picture> pictures = new LinkedList<Picture>();
		
	
	
	public static void initGlobal(FragmentActivity activity){
		
		if(isInitialized()){
			Log.i("GlobalData", "the global data components are already initialized !");			
			return;
		}
		if(getFragmentManager() == null)
			setFragmentManager(new FragmentManager());
		
		getFragmentManager().setActivity(activity);
		setActivity(activity);		
		
		if(Utils.isTracerServiceRunning(getActivity())){
			Log.i("GlogalData", "TracerService is running !");
		}
		
		setInitialized(true);
	}
	
	public static void registerBroadcastReceiver(){
		broadcastReceiver = new TracerServiceBroadcastReceiver();
		getActivity().registerReceiver(broadcastReceiver, new IntentFilter("ro.vadim.picturetrace.NewPicture"));
		
	}
	
	public static void unregisterBroadcastReceiver(){
		if(broadcastReceiver != null)
			getActivity().unregisterReceiver(broadcastReceiver);
		
	}
	
	
	public static void saveTrace() throws IOException{
		
		Log.i("GlobalData", "saveTrace()");
		
		
		Log.i("GlobalData", "saveTrace(): traceFileName: "+traceFileName);
		
		File albumF = PictureRetriever.getAlbumDir();
		File imageF = new File(albumF+"/"+traceFileName);
				
		Log.i("GlobalData", "saveTrace(): album name: "+albumF.getCanonicalPath());
		Log.i("GlobalData", "saveTrace(): traceFileName: "+imageF.getCanonicalPath());
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(imageF));
		
		for(Picture picture : getPictures()){
			
			writer.write(picture.toJson()+"\n");
			
		}
		
		writer.close();		
	}
	
	public static void loadTrace() throws IOException{
		
		Log.i("GlobalData", "loadTrace()");
		
		File albumF = PictureRetriever.getAlbumDir();
		File imageF = new File(albumF+"/"+traceFileName);
		
		BufferedReader reader = new BufferedReader(new FileReader(imageF));
		
		String pictureJson = reader.readLine();
		
		while(pictureJson != null){
			
			Picture newPicture = Picture.fromJson(pictureJson);
			Log.i("GlobalData", "loadTrace(): got picture: "+newPicture.getFileName());			
			pictures.add(newPicture);
			pictureJson = reader.readLine();
		}
		
		reader.close();
	}
	
	public static void deletePicture(Picture picture){
		synchronized (getPictures()) {
			
			File pictureFile = new File(picture.getFileName());
			pictureFile.delete();			
			getPictures().remove(picture);
		}
	}
	
	
	public static Activity getActivity() {
		return activity;
	}

	public static void setActivity(Activity activity) {
		GlobalData.activity = activity;
	}
	
	public static BoilerplateFragmentManager getFragmentManager() {
		return fragmentManager;
	}

	public static void setFragmentManager(BoilerplateFragmentManager fragmentManager) {
		GlobalData.fragmentManager = fragmentManager;
	}

	public static IntentFilter getIntentFilter() {
		return intentFilter;
	}

	public static void setIntentFilter(IntentFilter intentFilter) {
		GlobalData.intentFilter = intentFilter;
	}

	public static boolean isInitialized() {
		return initialized;
	}

	public static void setInitialized(boolean initialized) {
		GlobalData.initialized = initialized;
	}
	
	public static LinkedList<Picture> getPictures() {
		return pictures;
	}
	
	public static void setPictures(LinkedList<Picture> pictures) {
		GlobalData.pictures = pictures;
	}

	
	

}
