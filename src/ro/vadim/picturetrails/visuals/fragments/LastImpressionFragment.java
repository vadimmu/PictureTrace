package ro.vadim.picturetrails.visuals.fragments;

import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.webkit.WebView;
import ro.vadim.picturetrails.R;
import ro.vadim.picturetrails.utils.GlobalData;
import ro.vadim.picturetrails.utils.Picture;
import ro.vadim.picturetrails.utils.Utils;
import ro.vadim.picturetrails.visuals.BoilerplateFragment;

public class LastImpressionFragment extends BoilerplateFragment{
		
	private WebView pictureWebView = null;
	private Picture currentPicture = null;
	
	public void initPictureWebView(View view){
		
		Log.i(TAG, "initializing pictureWebView");
		pictureWebView = (WebView) view.findViewById(R.id.webViewPicture);
		
		pictureWebView.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				
				Log.i(TAG, "long click performed");
								
				return true;
			}
		});
		
		Picture picture = GlobalData.getDatabase().getLastPicture();		
		if(picture != null)
			pictureWebView.loadUrl(picture.getUrl());
		
	}
	
	
	public synchronized void loadPictureIntoView(Picture picture){
		
		Log.i(TAG, "loading a picture into the web view");
		
		if(picture == null)
			return;
		
		setCurrentPicture(picture);
		pictureWebView.loadUrl(picture.getUrl());
	}
	
	@Override
	public void initComponents(View view) {
		GlobalData.registerBroadcastReceiver();
		initPictureWebView(view);
	}
	
	
	@Override
	public void onPause() {
		GlobalData.unregisterBroadcastReceiver();		
		super.onPause();
	}

	
	public Picture getCurrentPicture() {
		return currentPicture;
	}

	public void setCurrentPicture(Picture currentPicture) {
		this.currentPicture = currentPicture;
	}
	
	
}
