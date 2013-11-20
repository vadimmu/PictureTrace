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
	ToggleButton buttonMockLocations = null;	
	Button buttonCheckTracerService = null;
	
	Thread mockLocationsThread = null;
	MockLocationRunnable mockLocationRunnable = null;
	
	

	public void startTracerService(View view){
		getActivity().startService(new Intent(getActivity(), TracerService.class));
	}
	   	 
	
	public void stopTracerService(View view){
		getActivity().stopService(new Intent(getActivity(), TracerService.class));
	}
	
	
	
	
	private void initButtonMockLocations(View view){
		final View thisView = view;
		buttonMockLocations = (ToggleButton) view.findViewById(R.id.buttonMockLocations);
		buttonMockLocations.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(buttonMockLocations.isChecked()){
					if(mockLocationsThread == null){
						mockLocationRunnable = new MockLocationRunnable();
						mockLocationsThread = new Thread(mockLocationRunnable);
						mockLocationsThread.start();
					}
					
					if(!mockLocationsThread.isAlive()){						
						mockLocationsThread.start();
					}
					else{
						Log.i("MainMenuFragment", "mockLocationsThread is already running !");						
					}
				}
					
				else{
					if(mockLocationsThread == null)
						return;
					
					if(mockLocationsThread.isAlive()){
						Log.i("MainMenuFragment", "stopping mockLocationRunnable");
						mockLocationRunnable.setStopped(true);
					}
					else{
						Log.i("MainMenuFragment", "mockLocationRunnable already stopped !");
					}
				}
					
				
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
		
		initButtonTrace(view);
		initButtonCheckTracerService(view);
		initButtonMockLocations(view);
	}

	
	
	
	
}
