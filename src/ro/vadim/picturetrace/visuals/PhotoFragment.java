package ro.vadim.picturetrace.visuals;

import java.util.LinkedList;

import ro.vadim.picturetrace.R;
import ro.vadim.picturetrace.utils.GlobalData;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class PhotoFragment extends BoilerplateFragment{
	
	ListView picturesView = null;
	PhotoAdapter picturesAdapter = null;
	
	private void initPicturesView(View view){
		
		if(GlobalData.getPictureURLs() == null)
			GlobalData.setPictureURLs(new LinkedList<String>());
		
		picturesAdapter = new PhotoAdapter(getActivity(), R.layout.listview_item_row, GlobalData.getPictureURLs());
		
		picturesView = (ListView) view.findViewById(R.id.listPictures);
		if(picturesView == null)
			Log.i("PhotoFragment", "picturesView = null !");
		
		if(picturesAdapter == null)
			Log.i("PhotoFragment", "picturesAdapter = null !");
		
		picturesView.setAdapter(picturesAdapter);
		
	}
	
	
	
	@Override
	public void initComponents(View view) {
		initPicturesView(view);
		
	}

}
