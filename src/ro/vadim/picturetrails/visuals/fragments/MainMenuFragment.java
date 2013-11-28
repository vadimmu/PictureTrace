package ro.vadim.picturetrails.visuals.fragments;


import ro.vadim.picturetrails.R;
import ro.vadim.picturetrails.test.MockLocationRunnable;
import ro.vadim.picturetrails.trace.TracerService;
import ro.vadim.picturetrails.utils.GlobalData;
import ro.vadim.picturetrails.utils.ToDo;
import ro.vadim.picturetrails.utils.Utils;
import ro.vadim.picturetrails.visuals.BoilerplateFragment;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ToggleButton;

public class MainMenuFragment extends BoilerplateFragment{

	
	
	
	ToggleButton buttonTrace = null;
	Button buttonCheckTracerService = null;
	Button buttonCheckPhotos = null;
	Button buttonClearPictures = null;
	Button buttonLastImpression = null;
	
	
	
	
	
	private void initButtonLastImpression(View view){
		buttonLastImpression = (Button) view.findViewById(R.id.buttonLastImpression);
		buttonLastImpression.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {				
				GlobalData.getFragmentManager().loadFragment(LastImpressionFragment.class.getCanonicalName(), null, null);				
			}
		});
	}
	
	
	private void initButtonClearPictures(View view){
		buttonClearPictures = (Button)view.findViewById(R.id.buttonClearPictures);
		buttonClearPictures.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Utils.buildAlertMessageAreYouSure(getActivity(), "Delete all pictures ?", new ToDo() {
					
					@Override
					public void doJob() {
						GlobalData.getDatabase().clearPictures();						
					}
				});
			}
		});
		
	}
	
	private void initButtonCheckPhotos(View view){
		
		
		buttonCheckPhotos = (Button)view.findViewById(R.id.buttonViewPictures);
		buttonCheckPhotos.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				GlobalData.getFragmentManager().loadFragment(PhotoFragment.class.getCanonicalName(), null, null);
				
			}
		});
	}
	
	private void initButtonCheckTracerService(View view){
		
		buttonCheckTracerService = (Button) view.findViewById(R.id.buttonChecktTracerService);
		buttonCheckTracerService.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(Utils.isTracerServiceRunning(getActivity()))
					buttonCheckTracerService.setText("It's running !");
				else
					buttonCheckTracerService.setText("Nope...");
			}
		});
	}
		
	private void initButtonTrace(View view){
				
		buttonTrace = (ToggleButton)view.findViewById(R.id.buttonTrace);
		
		if(!Utils.isTracerServiceRunning(getActivity())){
			Log.i(TAG, "TracerService is NOT running");
			buttonTrace.setChecked(false);
		}
		else{
			Log.i(TAG, "TracerService is already running !");
			buttonTrace.setChecked(true);
		}
		
		buttonTrace.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Log.i(TAG, "buttonTrace was clicked !");
				
				if(buttonTrace.isChecked())
					Utils.startTracerService(getActivity());
				else
					Utils.stopTracerService(getActivity());
			}
		});
	}
		
	@Override
	public void initComponents(View view){		
		initButtonTrace(view);
		initButtonCheckTracerService(view);
		initButtonLastImpression(view);
		initButtonCheckPhotos(view);
		initButtonClearPictures(view);
	}

	
	
	
	
}
