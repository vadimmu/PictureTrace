package ro.vadim.picturetrails.trace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonParser {
    
	private static String TAG = "JsonParser";
	
    private static JsonParser parser = null;
    
    private static ObjectMapper mapper = null;
    
    
    
    
    
    public JsonParser(){                
         if(getMapper() == null)
        	 setMapper(new ObjectMapper());
    }        
    
    
    
    public Map<String, Object> extractObject(String message){
    	Map<String, Object> jsonObject = null;
	    if(getMapper() == null)
	    	setMapper(new ObjectMapper());
	    
	    try{
	        jsonObject = getMapper().readValue(message, HashMap.class);
	    }
	    
	    catch (JsonParseException e) {
	    	Log.w(TAG, "JSON ERROR (JsonParseException) (original message) : "+message);
	        Log.w(TAG, "JSON ERROR (JsonParseException): "+e.toString());	        
	    }
	    
	    catch (JsonMappingException e) {
	    	Log.w(TAG, "JSON ERROR (JsonMappingException) (original message) : "+message);
	        Log.w(TAG, "JSON ERROR (JsonMappingException): "+e.toString());
	    }
	                    
	    finally{         
	            return jsonObject;
	    }
    }
    
    public ArrayList<Map> extractArrayOfObjects(String message){
	    ArrayList<Map> jsonObject = null;
	            
	    if(getMapper() == null)
	            setMapper(new ObjectMapper());
	    
	    try{
	        jsonObject = getMapper().readValue(message, ArrayList.class);
	    }
	    
	    catch (JsonParseException e) {
	    	Log.w(TAG, "JSON ERROR (JsonParseException) (original message) : "+message);
	        Log.w(TAG, "JSON ERROR (JsonParseException): "+e.toString());
	            
	    }
	    
	    catch (JsonMappingException e) {
	    	Log.w(TAG, "JSON ERROR (JsonMappingException) (original message) : "+message);
	        Log.w(TAG, "JSON ERROR (JsonMappingException): "+e.toString());
	    }
	                    
	    finally{
	            return jsonObject;
	    }                
    }
    
    public ArrayList<Map>extractArrayOfObjects(Map<String, Object> parentObject, String tag){
            
    	
    	if(parentObject == null)
    		return null;
    	
    	ArrayList<Map> childObjectList = (ArrayList<Map>)parentObject.get(tag);                
    	return childObjectList;                
    }
            
    public Map<String, Object> extractObjectFromObject(Map<String, Object> parentObject, String tag){                
    	if(parentObject == null)
    		return null;
    	
    	Map<String, Object> childObject = (Map<String, Object>) parentObject.get(tag);                
        return childObject;
    }



	public static ObjectMapper getMapper() {
		if(mapper == null)
			mapper = new ObjectMapper();
		
		return mapper;
	}



	public static void setMapper(ObjectMapper mapper) {
		JsonParser.mapper = mapper;
	}
    
    
    
    
    
    
    
    

}