package ro.vadim.picturetrails.utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import ro.vadim.picturetrails.R;
import ro.vadim.picturetrails.trace.PictureRetriever;
import ro.vadim.picturetrails.trace.TracerService;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Utils {

	public static PictureRetriever pictureRetriever = new PictureRetriever(null);
	
	public static boolean isGpsOn(){		
		LocationManager manager = (LocationManager) GlobalData.getActivity().getSystemService(Context.LOCATION_SERVICE);
		return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}
	
	
	
	public static boolean isInternetOn(){		
		ConnectivityManager manager = (ConnectivityManager)	GlobalData.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		
		for(NetworkInfo info : manager.getAllNetworkInfo()){
			if(info.isConnected())
				return true;
		}
		
		return false;
	}
		
	
	public static void buildAlertMessageNoGps(final Context context) {
	    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
	    builder.setMessage("Your GPS seems to be disabled.")
	           .setCancelable(false)
	           .setPositiveButton("Enable GPS", new DialogInterface.OnClickListener() {
	               public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
	                   context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));	                   
	               }
	           });
	           
	    final AlertDialog alert = builder.create();
	    alert.show();
	}
	
	public static void buildAlertMessageNoInternet(final Context context) {
	    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
	    builder.setMessage("Your Internet connection seems to be disabled.")
	           .setCancelable(false)
	           .setPositiveButton("Go to connections", new DialogInterface.OnClickListener() {
	               public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
	                   context.startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));	                   
	               }
	           });
	           
	    final AlertDialog alert = builder.create();
	    alert.show();
	}
	
	public static void buildAlertMessageDeletePicture(final Context context, final Picture picture, final ToDo doAfterDelete) {
	    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
	    
	    builder.setMessage("Delete picture ?")
	           .setCancelable(false)
	           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	               public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
	            	   GlobalData.deletePicture(picture);
	            	   doAfterDelete.doJob();
	               }
	           })
	           .setNegativeButton("No", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();		
				}
			});
	    
	    final AlertDialog alert = builder.create();	    
	    alert.show();
	}
	
	public static void buildAlertMessagePictureOptions(final Context context, final Picture picture, final ToDo doAfterDelete, final ToDo doAfterSave){
		
		final Dialog optionsDialog = new Dialog(context);
		optionsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		optionsDialog.setContentView(R.layout.custom_alert);
		
		Button saveFileButton = (Button)GlobalData.getActivity().findViewById(R.id.buttonSaveFile);
		Button deleteFileButton = (Button)GlobalData.getActivity().findViewById(R.id.buttonDeleteFile);
		
		saveFileButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				try {
					getPictureIntoGallery(picture);
				} 
				
				catch (IOException e) {
					Log.e("Utils", "buildAlertMessagePictureOptions(): getPictureIntoGallery: error: "+e.toString());
					e.printStackTrace();
				}
				doAfterSave.doJob();
				optionsDialog.dismiss();
			}
		});
		
		deleteFileButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				GlobalData.deletePicture(picture);
         	   	doAfterDelete.doJob();
			}
		});
		
		optionsDialog.setCancelable(true);
		optionsDialog.show();
		
	}
	
	
	
	
	public static boolean isTracerServiceRunning(Activity activity) {
	    ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (TracerService.class.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
	
	

	
	
	
	
	
	
	
	public static void getPictureIntoGallery(Picture picture) throws IOException{
		
		Log.i("PhotoAdapter", "getPictureIntoGallery()");
		File pictureFile = pictureRetriever.savePictureToFile(picture);
		picture.setFileName(pictureFile.getAbsolutePath());
		galleryAddPic(pictureFile);
	}
	
	
	private static void galleryAddPic(File newFile) {
		
		if(newFile == null)
			return;
		
		Log.i("PhotoAdapter", "galleryAddPic(): attempting to add the picture to the gallery...");
		
	    Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");		
	    Uri contentUri = Uri.fromFile(newFile);
	    
	    Log.i("PhotoAdapter", "galleryAddPic(): Picture URI: "+contentUri.toString());
	    
	    mediaScanIntent.setData(contentUri);
	    	    
	    GlobalData.getActivity().sendBroadcast(mediaScanIntent);
	}
	
	
	
		
	private static void savePicturesToFiles(final View view){
		
		pictureRetriever = new PictureRetriever(null);
		
		Thread retrievalThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				synchronized (GlobalData.getPictures()) {
					for(Picture picture : GlobalData.getPictures()){				
						
						if(!picture.hasFile()){
				        	try {
				        		
				    			Log.e("PhotoAdapter", "saving the file into the gallery");
				    			getPictureIntoGallery(picture);
				    			
							}
				        	catch (IOException e) {
								Log.e("PhotoAdapter", "file could not be created for the retrieved picture");
								e.printStackTrace();
							}
				        }
					}
				}
			}
		});
		
		retrievalThread.start();
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
