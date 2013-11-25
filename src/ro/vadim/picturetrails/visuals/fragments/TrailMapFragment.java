package ro.vadim.picturetrails.visuals.fragments;



import java.util.HashMap;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import ro.vadim.picturetrails.R;
import ro.vadim.picturetrails.visuals.BoilerplateFragment;

public class TrailMapFragment extends MapFragment{
	
	
	RelativeLayout mapContainer = null;
	View mapFragmentView = null;	
	GoogleMap googleMap = null;
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		container.removeAllViews();		
		mapFragmentView = super.onCreateView(inflater, container, savedInstanceState);
		googleMap = getMap();
		mapContainer = new RelativeLayout(getActivity());
		mapContainer.addView(mapFragmentView, new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		return mapContainer;
	}
	
	
}
