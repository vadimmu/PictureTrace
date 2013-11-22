package ro.vadim.picturetrace.visuals;

import java.util.ArrayList;
import java.util.LinkedList;

import ro.vadim.picturetrace.R;
import ro.vadim.picturetrace.utils.GlobalData;
import ro.vadim.picturetrace.utils.Picture;
import ro.vadim.picturetrace.utils.ToDo;
import ro.vadim.picturetrace.utils.Utils;

import android.content.Context;
import android.database.DataSetObserver;
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

public class PhotoAdapter extends BaseAdapter{
	
    Context context = null;
    int layoutResourceId = R.layout.listview_item_row;  
    ArrayList<View> rows = null;
    
    public PhotoAdapter(Context context) {
        super();
        rows = new ArrayList<View>(GlobalData.getPictures().size());        
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
            
            Picture picture = (Picture)getItem(position);
            holder = new PhotoHolder(row, picture);
            
            
            Log.i("PhotoAdapter", "loading image URL: "+picture.getFileName());            
            holder.photoView.loadUrl("file:///"+picture.getFileName());
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
						
					
					Utils.buildAlertMessageDeletePicture(GlobalData.getActivity(), thisPicture, new ToDo() {
						
						@Override
						public void doJob() {
							ListView list = (ListView)photoView.getParent().getParent();
							list.setAdapter(new PhotoAdapter(GlobalData.getActivity()));
							list.invalidate();
						}
					});										
					return false;
				}
			});
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
