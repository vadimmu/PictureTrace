package ro.vadim.picturetrace.visuals;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.impl.client.TunnelRefusedException;

import ro.vadim.picturetrace.R;
import ro.vadim.picturetrace.trace.HttpRequester;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class TestFragment extends BoilerplateFragment{
	
	private static final String DEFAULT_ALBUM_NAME = "PictureTrace";
	private static final String JPEG_FILE_PREFIX = "PICTURETRACE_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	
	
	HttpRequester requester = null;
	Button buttonGetPicture = null;
	EditText textPictureURL = null;
	
	public TestFragment() {
		requester = new HttpRequester();
		
	}
	
	
	
	private void initTextPictureURL(View view){
		
		textPictureURL = (EditText) view.findViewById(R.id.textPictureURL);
		
		
	}
	
	
	
	private void getThePicture(){
		
		Log.i("TestFragment", "getting the picture");		
		
		Thread newThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				File pictureFile;
				try {
					pictureFile = createImageFile();
					requester.getPicture(textPictureURL.getText().toString(), pictureFile);	
					galleryAddPic(pictureFile);	
				} 
				
				catch (IOException e) {
					Log.e("TestFragment", "buttonGetPicture.onClick(): IOException: "+e.toString());
					e.printStackTrace();
				}				
			}
		});
		
		newThread.start();
		
	}
	
	private void initButtonGetPicture(View view){
		
		buttonGetPicture = (Button) view.findViewById(R.id.buttonGetPicture);
		buttonGetPicture.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getThePicture();
			}
		});
		
	}
	
	
	
	@Override
	public void initComponents(View view) {
		initTextPictureURL(view);
		initButtonGetPicture(view);
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	private void galleryAddPic(File newFile) {
		
		Log.i("TestFragment", "galleryAddPic(): attempting to add the picture to the gallery...");
		
	    Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");		
	    Uri contentUri = Uri.fromFile(newFile);
	    
	    Log.i("TestFragment", "galleryAddPic(): Picture URI: "+contentUri.toString());
	    
	    mediaScanIntent.setData(contentUri);
	    this.getActivity().sendBroadcast(mediaScanIntent);
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
			Log.i("TestFragment", "the external storage is mounted...");
			storageDir = getAlbumStorageDir(DEFAULT_ALBUM_NAME);
			if(!storageDir.exists())
				storageDir.mkdirs();
						
			Log.i("TestFragment", "the external storage directory: " + storageDir.getCanonicalPath());
			
			if (storageDir != null) {
				if (! storageDir.mkdirs()) {
					if (! storageDir.exists()){
						Log.d("TestFragment", "failed to create picture directory");
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
		
		Log.i("TestFragment", "createImageFile(): imageFileName: "+imageFileName);
		
		
		File albumF = getAlbumDir();
		File imageF = new File(albumF+"/"+imageFileName+JPEG_FILE_SUFFIX);
		
		
		Log.i("TestFragment", "createImageFile(): album name: "+albumF.getCanonicalPath());
		Log.i("TestFragment", "createImageFile(): image name: "+imageF.getCanonicalPath());
		
		return imageF;
	}
	
	
	
}
