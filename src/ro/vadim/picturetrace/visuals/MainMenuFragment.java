package ro.vadim.picturetrace.visuals;


import ro.vadim.picturetrace.R;
import ro.vadim.picturetrace.trace.TracerService;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ToggleButton;

public class MainMenuFragment extends BoilerplateFragment{

	ToggleButton buttonTrace = null;
	
	
	
	
	

	public void startNewService(View view){
		getActivity().startService(new Intent(getActivity(), TracerService.class));
	}
	   	 
	
	public void stopNewService(View view){
		getActivity().stopService(new Intent(getActivity(), TracerService.class));
	}
	
	
	
	private void initButtonTrace(View view){
		
		final View thisView = view;
		buttonTrace = (ToggleButton)view.findViewById(R.id.buttonTrace);		
		buttonTrace.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(buttonTrace.isChecked())
					startNewService(thisView);
				else
					stopNewService(thisView);
			}
		});
	}
	
	
	@Override
	public void initComponents(View view){
		initButtonTrace(view);
		
	}

	
	
	
	
}
