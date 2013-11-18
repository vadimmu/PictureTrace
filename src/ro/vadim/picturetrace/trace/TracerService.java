package ro.vadim.picturetrace.trace;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;



import ro.vadim.picturetrace.utils.Picture;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

public class TracerService extends Service{
	
	
	private static final String DEFAULT_ALBUM_NAME = "PictureTrace";
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	
	
	private Tracer tracer = null;
	private HttpRequester httpRequester = null;
	private Runnable traceRunnable = null;
	private Thread traceThread = null;
	private boolean pause = false;
	private boolean stop = false;
	private LinkedList<Picture> pictures = null;
	
	
	private void initTraceRunnable(){
		
		
		
		traceRunnable = new Runnable() {
			
			@Override
			public void run() {
				
				
				
				
				while(!stop){
					
					try {
						wait();
						Picture lastPicture = tracer.getLastPicture();
						
						if(lastPicture == null)
							continue;
						
						pictures.add(lastPicture);
						File pictureFile = createImageFile();							
						httpRequester.getPicture(lastPicture.url, pictureFile);	
						galleryAddPic(pictureFile);						
					}
					
					catch (InterruptedException e) {
						Log.i("TracerService", "traceRunnable.wait(): InterruptedException: "+e.toString());
						e.printStackTrace();
					} 
					
					catch (IOException e) {
						Log.i("TracerService", "traceRunnable.createImageFile(): IOException: "+e.toString());
						e.printStackTrace();
					}
				}				
			}
		};
	}
	
	private void init(){		
		pictures = new LinkedList<Picture>();
		httpRequester = new HttpRequester();		
		initTraceRunnable();
		setTraceThread(new Thread(traceRunnable));
	}
	
	public TracerService() {
		init();
	}
	
	
	
	private void run(){
			
		
		tracer = new Tracer(this);
		traceThread.start();
		
	}
	
	

	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		run();
		return super.onStartCommand(intent, flags, startId);
	}
	
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	
	
	
	
	
	
	
	private void galleryAddPic(File newFile) {
	    Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");		
	    Uri contentUri = Uri.fromFile(newFile);
	    mediaScanIntent.setData(contentUri);
	    this.sendBroadcast(mediaScanIntent);
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
	
	private File getAlbumDir() {
		File storageDir = null;

		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			
			storageDir = getAlbumStorageDir(DEFAULT_ALBUM_NAME);

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
		String timeStamp = new SimpleDateFormat("dd.MM.yyyy_HH:mm:ss").format(new Date());
		String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
		File albumF = getAlbumDir();		
		File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
		return imageF;
	}
	
	
	
	
	
	
	
	
	
	
	public Thread getTraceThread() {
		return traceThread;
	}

	public void setTraceThread(Thread traceThread) {
		this.traceThread = traceThread;
	}
	
	

}
