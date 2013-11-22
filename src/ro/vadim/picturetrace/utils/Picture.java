package ro.vadim.picturetrace.utils;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import ro.vadim.picturetrace.trace.JsonParser;

public class Picture{
	
	private String url = "";
	private String description = "";
	private double latitude = 0.0;
	private double longitude = 0.0;
	private String fileName = null;
	private Date timestamp = null;
	
	public Picture(String newUrl, String newDescription, double newLatitude, double newLongitude, String newFileName){		
		this.setUrl(newUrl);
		this.setDescription(newDescription);
		this.setLatitude(newLatitude);
		this.setLongitude(newLongitude);
		if(newFileName != null)
			this.setFileName(newFileName);
		
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
			
			if(fileName != null)
				pictureRepresentation.put("fileName", getFileName());
						
			return JsonParser.getMapper().writeValueAsString(pictureRepresentation);			
		} 
		
		catch (JsonProcessingException e) {
			Log.e("Picture", "toJson(): cannot serialize: "+e.toString());
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
					Double.valueOf(pictureRepresentation.get("longitude")),
					null);
			
			newPicture.setTimestamp(new Date(Long.valueOf(pictureRepresentation.get("timestamp"))));
			
			String fileName = (String)pictureRepresentation.get("fileName");
			if(fileName != null){
				if(fileName.equals(""))
					newPicture.setFileName(null);
				else
					newPicture.setFileName(fileName);
			}
			
		}
		
		catch (JsonParseException e) {
			Log.e("Picture", "fromJson(): cannot deserialize: "+e.toString());
			e.printStackTrace();
		} 
		
		catch (JsonMappingException e) {
			Log.e("Picture", "fromJson(): cannot deserialize: "+e.toString());
			e.printStackTrace();
		}
		
		catch (IOException e) {
			Log.e("Picture", "fromJson(): cannot deserialize: "+e.toString());
			e.printStackTrace();
		}
		
		finally{
			return newPicture;			
		}
	}
	
	
	public boolean hasFile(){
		return (fileName != null);		
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


	public String getFileName() {
		if(fileName == null)
			return "";
		return fileName;
	}


	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
}
