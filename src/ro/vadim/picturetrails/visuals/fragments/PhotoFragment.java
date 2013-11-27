package ro.vadim.picturetrails.visuals.fragments;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import ro.vadim.picturetrails.R;
import ro.vadim.picturetrails.trace.PictureRetriever;
import ro.vadim.picturetrails.utils.GlobalData;
import ro.vadim.picturetrails.utils.Picture;
import ro.vadim.picturetrails.visuals.BoilerplateFragment;
import ro.vadim.picturetrails.visuals.PictureAdapter;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class PhotoFragment extends BoilerplateFragment{
	
	ListView picturesView = null;
	PictureAdapter picturesAdapter = null;
	
	
	private void initPicturesView(View view){
		
		if(GlobalData.getPictures() == null)			
			GlobalData.setPictures(new LinkedList<Picture>());
		
		picturesAdapter = new PictureAdapter(getActivity());
		
		picturesView = (ListView) view.findViewById(R.id.listPictures);
		if(picturesView == null)
			Log.i("PhotoFragment", "picturesView = null !");
		
		if(picturesAdapter == null)
			Log.i("PhotoFragment", "picturesAdapter = null !");
		
		picturesView.setAdapter(picturesAdapter);
		
	}
	
	
	
	@Override
	public void initComponents(View view) {		
		//savePicturesToFiles(view);
		initPicturesView(view);
		
	}

}
