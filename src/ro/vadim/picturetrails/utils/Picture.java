package ro.vadim.picturetrails.utils;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import ro.vadim.picturetrails.trace.JsonParser;

public class Picture{
	
	private static String TAG = "Picture";
	
	private String url = "";
	private String description = "";
	private double latitude = 0.0;
	private double longitude = 0.0;	
	private Date timestamp = null;
	
	
	public Picture(String newUrl, String newDescription, double newLatitude, double newLongitude){		
		this.setUrl(newUrl);
		this.setDescription(newDescription);
		this.setLatitude(newLatitude);
		this.setLongitude(newLongitude);
				
		setTimestamp(new Date());
	}
	
	@JsonIgnore
	public String toJson(){
		try {
			
			HashMap<String, String> pictureRepresentation = new HashMap<String, String>();
			pictureRepresentation.put("url", getUrl());
			pictureRepresentation.put("description", getDescription());
			pictureRepresentation.put("latitude", String.valueOf(getLatitude()));
			pictureRepresentation.put("longitude", String.valueOf(getLongitude()));
			pictureRepresentation.put("timestamp", String.valueOf(getTimestamp()));
						
						
			return JsonParser.getMapper().writeValueAsString(pictureRepresentation);			
		} 
		
		catch (JsonProcessingException e) {
			Log.e(TAG, "toJson(): cannot serialize: "+e.toString());
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings({ "finally", "unchecked" })
	public static Picture fromJson(String serializedPicture){
		
		HashMap<String, String> pictureRepresentation = null; 
		Picture newPicture = null;
		
		try {
			pictureRepresentation = JsonParser.getMapper().readValue(serializedPicture, HashMap.class);
			
			newPicture = new Picture(pictureRepresentation.get("url"), 
					pictureRepresentation.get("description"),
					Double.valueOf(pictureRepresentation.get("latitude")),
					Double.valueOf(pictureRepresentation.get("longitude")));
			
			newPicture.setTimestamp(new Date(Long.valueOf(pictureRepresentation.get("timestamp"))));
			
		}
		
		catch (JsonParseException e) {
			Log.e(TAG, "fromJson(): cannot deserialize: "+e.toString());
			e.printStackTrace();
		} 
		
		catch (JsonMappingException e) {
			Log.e(TAG, "fromJson(): cannot deserialize: "+e.toString());
			e.printStackTrace();
		}
		
		catch (IOException e) {
			Log.e(TAG, "fromJson(): cannot deserialize: "+e.toString());
			e.printStackTrace();
		}
		
		finally{
			return newPicture;			
		}
	}
	
	
	

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
}
