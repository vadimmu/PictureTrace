package ro.vadim.picturetrace.visuals;


import ro.vadim.picturetrace.R;
import ro.vadim.picturetrace.test.MockLocationRunnable;
import ro.vadim.picturetrace.trace.TracerService;
import ro.vadim.picturetrace.utils.GlobalData;
import ro.vadim.picturetrace.utils.Utils;
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
	
	
	
	
	public void startTracerService(View view){
		if(Utils.isTracerServiceRunning(getActivity())){
			Log.i("MainMenuFragment", "startTracerService(): TracerService is already running");
			return;
		}
		Intent serviceIntent = new Intent(getActivity(), TracerService.class);	
		getActivity().startService(serviceIntent);
	}
		
	public void stopTracerService(View view){
		if(!Utils.isTracerServiceRunning(getActivity())){
			Log.i("MainMenuFragment", "stopTracerService(): TracerService is already stopped");
			return;
		}
		Intent serviceIntent = new Intent(getActivity(), TracerService.class);
		getActivity().stopService(serviceIntent);
	}
	
	
	
	
	private void initButtonCheckPhotos(View view){
		
		final View thisView = view;
		buttonCheckPhotos = (Button)view.findViewById(R.id.buttonViewPictures);
		buttonCheckPhotos.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				GlobalData.getFragmentManager().loadFragment(PhotoFragment.class.getCanonicalName(), null, null);
				
			}
		});
	}
	
	private void initButtonCheckTracerService(View view){
		final View thisView = view;
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
		
		final View thisView = view;
		buttonTrace = (ToggleButton)view.findViewById(R.id.buttonTrace);
		
		if(!Utils.isTracerServiceRunning(getActivity())){
			Log.i("MainMenuFragment", "TracerService is NOT running");
			buttonTrace.setChecked(false);
		}
		else{
			Log.i("MainMenuFragment", "TracerService is already running !");
			buttonTrace.setChecked(true);
		}
		
		buttonTrace.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Log.i("MainMenuFragment", "buttonTrace was clicked !");
				
				if(buttonTrace.isChecked())
					startTracerService(thisView);
				else
					stopTracerService(thisView);
			}
		});
	}
	
	
	@Override
	public void initComponents(View view){
		initButtonCheckPhotos(view);
		initButtonTrace(view);
		initButtonCheckTracerService(view);		
	}

	
	
	
	
}
