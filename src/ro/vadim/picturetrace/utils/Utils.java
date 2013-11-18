package ro.vadim.picturetrace.utils;

import java.util.HashMap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {

	
	
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
	
	
	
	
	

	
	
}
