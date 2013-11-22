package ro.vadim.picturetrace.visuals;

import java.util.ArrayList;
import java.util.LinkedList;

import ro.vadim.picturetrace.R;
import ro.vadim.picturetrace.utils.GlobalData;

import android.content.Context;
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
    LinkedList<String> data = new LinkedList<String>();
    
    
    public PhotoAdapter(Context context, int layoutResourceId, LinkedList<String> incomingData) {
        super();
        
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        
        for(String url:incomingData){
        	data.addFirst(url);
        }
        
        this.data = data;
    }
    
    public void addURL(String newUrl){
    	data.add(newUrl);    	
    }
    
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        PhotoHolder holder = null;
        
        if(row == null){        	
            LayoutInflater inflater = GlobalData.getActivity().getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
                        
            holder = new PhotoHolder(row);
            
            row.setTag(holder);
        }
        
        else{
            holder = (PhotoHolder)row.getTag();
        }        
        
        String photoURL = data.get(position);
        if(photoURL != null){        	
        	holder.photoView.loadUrl(photoURL);        	
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
		return data.size();
	}

	@Override
	public Object getItem(int position) {		
		return data.get(getCount() - 1 - position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	
	
}
