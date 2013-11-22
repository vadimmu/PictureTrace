package ro.vadim.picturetrace.visuals;

import java.util.LinkedList;

import ro.vadim.picturetrace.R;
import ro.vadim.picturetrace.utils.GlobalData;
import ro.vadim.picturetrace.utils.Picture;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class PhotoFragment extends BoilerplateFragment{
	
	ListView picturesView = null;
	PhotoAdapter picturesAdapter = null;
	
	private void initPicturesView(View view){
		
		if(GlobalData.getPictures() == null)			
			GlobalData.setPictures(new LinkedList<Picture>());
		
		picturesAdapter = new PhotoAdapter(getActivity(), R.layout.listview_item_row);
		
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
