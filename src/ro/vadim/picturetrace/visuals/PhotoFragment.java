package ro.vadim.picturetrace.visuals;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import ro.vadim.picturetrace.R;
import ro.vadim.picturetrace.trace.PictureRetriever;
import ro.vadim.picturetrace.utils.GlobalData;
import ro.vadim.picturetrace.utils.Picture;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class PhotoFragment extends BoilerplateFragment{
	
	ListView picturesView = null;
	PhotoAdapter picturesAdapter = null;
	PictureRetriever pictureRetriever = null;
	
	
	
	public void getPictureIntoGallery(Picture picture) throws IOException{
		
		Log.i("PhotoAdapter", "getPictureIntoGallery()");
		File pictureFile = pictureRetriever.savePictureToFile(picture);
		picture.setFileName(pictureFile.getAbsolutePath());
		galleryAddPic(pictureFile);
	}
	
	private void galleryAddPic(File newFile) {
		
		if(newFile == null)
			return;
		
		Log.i("PhotoAdapter", "galleryAddPic(): attempting to add the picture to the gallery...");
		
	    Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");		
	    Uri contentUri = Uri.fromFile(newFile);
	    
	    Log.i("PhotoAdapter", "galleryAddPic(): Picture URI: "+contentUri.toString());
	    
	    mediaScanIntent.setData(contentUri);
	    	    
	    GlobalData.getActivity().sendBroadcast(mediaScanIntent);
	}
	
	
	private void savePicturesToFiles(final View view){
		
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
				
				GlobalData.getActivity().runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						initPicturesView(view);
						
					}
				});
			}
		});
		
		retrievalThread.start();
		
	}
	
	private void initPicturesView(View view){
		
		
		
		if(GlobalData.getPictures() == null)			
			GlobalData.setPictures(new LinkedList<Picture>());
		
		picturesAdapter = new PhotoAdapter(getActivity());
		
		picturesView = (ListView) view.findViewById(R.id.listPictures);
		if(picturesView == null)
			Log.i("PhotoFragment", "picturesView = null !");
		
		if(picturesAdapter == null)
			Log.i("PhotoFragment", "picturesAdapter = null !");
		
		picturesView.setAdapter(picturesAdapter);
		
	}
	
	
	
	@Override
	public void initComponents(View view) {		
		savePicturesToFiles(view);
	}

}
