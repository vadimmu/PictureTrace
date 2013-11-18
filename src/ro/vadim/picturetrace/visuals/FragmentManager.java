package ro.vadim.picturetrace.visuals;

import ro.vadim.picturetrace.R;
import ro.vadim.picturetrace.R.layout;
import android.util.Log;



public class FragmentManager extends BoilerplateFragmentManager{

	@Override
	public boolean registerFragmentLayouts() {		
		setLayoutForFragment(MainMenuFragment.class.getCanonicalName(), R.layout.layout_main_menu);
		setLayoutForFragment(PictureFragment.class.getCanonicalName(), R.layout.layout_pictures);
		setLayoutForFragment(TestFragment.class.getCanonicalName(), R.layout.layout_test);
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
