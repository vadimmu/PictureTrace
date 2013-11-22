package ro.vadim.picturetrace.visuals;

import java.util.ArrayList;
import java.util.LinkedList;

import ro.vadim.picturetrace.R;
import ro.vadim.picturetrace.utils.GlobalData;
import ro.vadim.picturetrace.utils.Picture;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

public class PhotoAdapter extends BaseAdapter{
	
    Context context;
    int layoutResourceId = R.layout.listview_item_row;  
    ArrayList<View> rows = null;
    
    public PhotoAdapter(Context context, int layoutResourceId) {
        super();
        rows = new ArrayList<View>(GlobalData.getPictures().size());
        this.layoutResourceId = layoutResourceId;
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
            holder = new PhotoHolder(row);
            
            Picture photo = (Picture)getItem(position);        
            Log.i("PhotoAdapter", "loading image URL: "+photo.getFileName());            
            holder.photoView.loadUrl("file:///"+photo.getFileName());
            row.setTag(holder);            
        }
        
        else{
            holder = (PhotoHolder)row.getTag();
        }        
        
        
        return row;
    }
    
    static class PhotoHolder{
    	WebView photoView;
    	
    	public PhotoHolder(View view){
    		photoView = (WebView)view.findViewById(R.id.photoView);
    		photoView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
    	}
    }

	@Override
	public int getCount() {		
		return GlobalData.getPictures().size();
	}

	@Override
	public Object getItem(int position) {		
		return GlobalData.getPictures().get(getCount() - 1 - position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	
	
}
