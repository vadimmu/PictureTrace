package ro.vadim.picturetrails.visuals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import ro.vadim.picturetrails.R;
import ro.vadim.picturetrails.database.DatabaseHelper;
import ro.vadim.picturetrails.trace.PictureRetriever;
import ro.vadim.picturetrails.utils.GlobalData;
import ro.vadim.picturetrails.utils.Picture;
import ro.vadim.picturetrails.utils.ToDo;
import ro.vadim.picturetrails.utils.Utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.location.Location;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnLongClickListener;
import android.webkit.WebView;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class PictureAdapter extends BaseAdapter{
	
	private static String TAG = "PictureAdapter";
	
    Context context = null;
    int layoutResourceId = R.layout.listview_item_row;  
    ArrayList<View> rows = null;
    DatabaseHelper db = null;
    Cursor cursor = null;
    
    public PictureAdapter(Context context) {
        super();
        db = new DatabaseHelper(context);
        cursor = db.getAllPictures_Cursor();
        if(cursor != null)
        	rows = new ArrayList<View>(cursor.getCount());
        else 
        	rows = new ArrayList<View>(0);
        
        this.context = context;
    }
    
    public void addPicture(Picture newPicture){
    	if(GlobalData.getPictures() == null)
    		GlobalData.setPictures(new LinkedList<Picture>());
    	
    	synchronized (GlobalData.getPictures()) {
    		GlobalData.getPictures().add(newPicture);
		}    	
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	    	
    	View row = convertView;
    	
    	if(position < rows.size())
    		row = rows.get(rows.size() - 1 - position); 
    		
        PhotoHolder holder = null;        
        if(row == null){        	
            LayoutInflater inflater = GlobalData.getActivity().getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);                        
                        
            cursor.moveToPosition(position);
            Picture picture = DatabaseHelper.getPictureFromCursor(cursor);                     
            
            holder = new PhotoHolder(row, picture);
                        
            Log.i(TAG, "loading image URL: "+picture.getUrl());            
            holder.photoView.loadUrl(picture.getUrl());            	
                        
            row.setTag(holder);
        }
        
        else{
            holder = (PhotoHolder)row.getTag();
        }
        
        return row;
    }
    
    
    
    
    
    
    static class PhotoHolder{
    	WebView photoView = null;
    	Picture picture = null;
    	
    	public PhotoHolder(View view, Picture picture){
    		
    		this.picture = picture;    		
    		photoView = (WebView)view.findViewById(R.id.photoView);
    		photoView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
    		
    		final Picture thisPicture = this.picture;
    		photoView.setOnLongClickListener(new OnLongClickListener() {				
				@Override
				public boolean onLongClick(View v) {
					
					Utils.buildAlertMessageSavePicture(GlobalData.getActivity(), thisPicture, new ToDo() {
						
						@Override
						public void doJob() {
							Log.i(TAG, "saving picture to gallery: " + thisPicture.getUrl());
						}
					});
															
					return false;
				}
			});
    	}
    }

	@Override
	public int getCount() {		
		return cursor.getCount();
	}

	@Override
	public Object getItem(int position) {
		cursor.moveToPosition(position);
		return DatabaseHelper.getPictureFromCursor(cursor);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	
	
}
