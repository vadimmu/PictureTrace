package ro.vadim.picturetrails.visuals;

import ro.vadim.picturetrails.R;
import ro.vadim.picturetrails.R.layout;
import ro.vadim.picturetrails.visuals.fragments.LastImpressionFragment;
import ro.vadim.picturetrails.visuals.fragments.MainMenuFragment;
import ro.vadim.picturetrails.visuals.fragments.PhotoFragment;
import android.util.Log;



public class FragmentManager extends BoilerplateFragmentManager{

	@Override
	public boolean registerFragmentLayouts() {		
		setLayoutForFragment(MainMenuFragment.class.getCanonicalName(), R.layout.layout_main_menu);
		setLayoutForFragment(PhotoFragment.class.getCanonicalName(), R.layout.layout_pictures);
		setLayoutForFragment(LastImpressionFragment.class.getCanonicalName(), R.layout.layout_last_impression);		
		return false;
	}
	
	@Override
	public boolean loadSettingsFragment() {		
		return false;
	}

	@Override
	public boolean loadHelpFragment() {
		return false;
	}
	
}
