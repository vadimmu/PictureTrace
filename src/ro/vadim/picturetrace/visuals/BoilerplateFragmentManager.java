package ro.vadim.picturetrace.visuals;

import java.util.HashMap;

import ro.vadim.picturetrace.R;
import ro.vadim.picturetrace.R.id;
import ro.vadim.picturetrace.R.layout;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;



/**
 * @author Vadim
 *
 *
 *
 */
public abstract class BoilerplateFragmentManager{
	
	private FragmentActivity activity = null;
	private Fragment currentFragment = null;
	private final int DEFAULT_NUMBER_OF_LAYOUTS = 10;	
	private HashMap<String, Integer> fragmentLayouts = null;
	
	
	
	
	
	public abstract boolean registerFragmentLayouts();
	public abstract boolean loadSettingsFragment();		
	public abstract boolean loadHelpFragment();
	
	
	
	
	
	
	public BoilerplateFragmentManager() {
		setFragmentLayouts(new HashMap<String, Integer>(DEFAULT_NUMBER_OF_LAYOUTS));
		registerFragmentLayouts();		
	}
	
	
	
	
	
	/**
	 * Assumes that the MainActivity has a container 
	 * with the ID: topContainer 
	 * 
	 * if either exitAnimation or enterAnimation are null, 
	 * no animation will be added to the transition 
	 * **/
	public boolean loadFragment(String fragmentClassName, Integer exitAnimation, Integer enterAnimation) {
				
		Class currentClass = null;
		
		try {
			currentClass = Class.forName(fragmentClassName);
		} 
		
		catch (ClassNotFoundException e) {
			Log.e("BoilerplateFragmentManager", "loadFragment: class \""+fragmentClassName+"\" could not be found !");
			Log.e("BoilerplateFragmentManager", "loadFragment: "+e.toString());
			return false;
		}
		
		
		Class superClass = currentClass.getSuperclass();
		if( ! superClass.equals(BoilerplateFragment.class)){
			Log.e("BoilerplateFragmentManager", "loadFragment: class \""+fragmentClassName+"\" is not a valid subclass of BoilerplateFragment !");			
			return false;
		}
		
		
		BoilerplateFragment newFragment = null;
		
		try {
			newFragment = (BoilerplateFragment)currentClass.newInstance();
			Log.i("BoilerplateFragmentManager", "loadFragment: loadFragment: newFragment class: "+newFragment.getClass().getCanonicalName());
			FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
			
			if( (exitAnimation != null) && (enterAnimation != null) )
				transaction.setCustomAnimations(exitAnimation, enterAnimation);
			
			transaction.replace(R.id.topContainer, newFragment, newFragment.getClass().toString());
			
			transaction.replace(R.id.topContainer, newFragment);
			transaction.addToBackStack(newFragment.getTag());
			transaction.commit();
						
			return true;
		}
		catch (InstantiationException e) {
			Log.e("BoilerplateFragmentManager", "loadFragment: class \""+fragmentClassName+"\" could not be instantiated !");
			Log.e("BoilerplateFragmentManager", "loadFragment: "+e.toString());
			return false;
		}
		catch (IllegalAccessException e) {
			Log.e("BoilerplateFragmentManager", "loadFragment: "+e.toString());
			return false;
		}
		
	}
	
	
	
	
	
	public Fragment getCurrentFragment() {
		return currentFragment;
	}

	public void setCurrentFragment(Fragment currentFragment) {
		this.currentFragment = currentFragment;
	}
	
	public HashMap<String, Integer> getFragmentLayouts() {
		return fragmentLayouts;
	}

	public void setFragmentLayouts(HashMap<String, Integer> fragmentLayouts) {
		this.fragmentLayouts = fragmentLayouts;
	}
	
	public int getLayoutForFragment(String fragmentClassName){
		
		Integer resourceID = fragmentLayouts.get(fragmentClassName); 
		
		if(resourceID != null){			
			return resourceID.intValue();
		}
		
		Log.i("BoilerplateFragmentManager", "getLayoutForFragment: Could not get layout. Providing default.");
		
		return R.layout.layout_default;
	}
	
	public void setLayoutForFragment(String fragmentClassName, int layoutResourceID){
		fragmentLayouts.put(fragmentClassName, new Integer(layoutResourceID));		
	}
	
	public FragmentActivity getActivity() {
		return activity;
	}
	
	public void setActivity(FragmentActivity activity) {
		this.activity = activity;
	}



	
	
	
}
