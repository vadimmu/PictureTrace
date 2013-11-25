package ro.vadim.picturetrails.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class TracerServiceBroadcastReceiver extends BroadcastReceiver{
	
	
	public TracerServiceBroadcastReceiver() {
		super();
		Log.i("TracerServiceBroadcastReceiver", "broadcastReceiver started !");
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		Bundle extras = intent.getExtras();
		if(intent.getAction().equals("ro.vadim.picturetrace.NewPicture")){
			
			Toast.makeText(GlobalData.getActivity(), (String)extras.get("url"), Toast.LENGTH_LONG).show();
			
			synchronized (GlobalData.getPictures()) {
				GlobalData.getPictures().add((Picture.fromJson((String)extras.get("picture"))));
			}
		}
	}

}
