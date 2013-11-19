package ro.vadim.picturetrace;

import ro.vadim.picturetrace.utils.GlobalData;
import ro.vadim.picturetrace.visuals.MainMenuFragment;
import ro.vadim.picturetrace.visuals.TestFragment;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;

public class MainActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		GlobalData.initGlobal(this);
		GlobalData.getFragmentManager().loadFragment(MainMenuFragment.class.getCanonicalName(), null, null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
