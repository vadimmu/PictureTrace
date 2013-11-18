package ro.vadim.picturetrace.visuals;

import ro.vadim.picturetrace.utils.GlobalData;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BoilerplateFragment extends Fragment{
	
	private int layout = -1;
	private String backFragment = null;
	
	
	
	
	
	public abstract void initComponents(View view);
	
	
	
	
	
	public BoilerplateFragment(){
		super();		
	}
	
	
	
	
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
    	
    	Log.i("BoilerplateFragment", "getting resourceID");
    	int resourceID = GlobalData.getFragmentManager().getLayoutForFragment(this.getClass().getCanonicalName());
    	Log.i("BoilerplateFragment", "getting resourceID: "+String.valueOf(resourceID));
    	
    	View view = inflater.inflate(resourceID, container, false);
        initComponents(view);                
        return view;
    	
    }
    
    
    
    
    
	public String getBackFragment() {
		return backFragment;
	}


	public void setBackFragment(String backFragment) {
		
		try {
			Class.forName(backFragment);
			this.backFragment = backFragment;
		} 
		
		catch (ClassNotFoundException e) {
			Log.e("BoilerplateFragment", "setBackFragment: could not set the back fragment from this fragment.");
			Log.e("BoilerplateFragment", "setBackFragment: "+e.toString());
			
			this.backFragment = null;
		}
		
		
	}
	
	
	
	
	
	
	
	
}
