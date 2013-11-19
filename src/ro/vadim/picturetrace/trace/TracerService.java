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
import java.util.Observer;



import ro.vadim.picturetrace.utils.Picture;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class TracerService extends Service{
	
	
	private static final String DEFAULT_ALBUM_NAME = "PictureTrace";
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	
	
	private Tracer tracer = null;
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
	
	
	
	
	
	
	private void initTraceRunnable(){
		
		Log.i("TracerService", "setting up traceRunnable");
		traceRunnable = new Runnable() {
			
			@Override
			public void run() {
				
				Log.i("TracerService", "traceRunnable is running !");				
				
				while(!isStopped()){
					
					try {
						Log.i("TracerService", "traceRunnable.wait(): waiting one more round");						
						
						
						synchronized (this){
							
							this.wait();							
						}
						
						if(isPaused())
							continue;
						
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
		
		Log.i("TracerService", "init()");
		pictures = new LinkedList<Picture>();
		httpRequester = new HttpRequester();		
		initTraceRunnable();
		setTraceThread(new Thread(traceRunnable));
	}
	
	
	public TracerService() {
		
		Log.i("TracerService", "service object initialized");
		init();
	}
	
	private void run(){
		
		Log.i("TracerService", "run()");
		tracer = new Tracer(this);
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
		return super.onStartCommand(intent, flags, startId);
	}
	
	
	@Override
	public void onCreate() {
		run();
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
	
		
	
	
	
	
	
	
	
	
	public class TracerBinder extends Binder {
		public TracerService getTracerServiceInstance() {			
			return TracerService.this;
		}
	}
}
