package ro.vadim.picturetrails.trace;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.Environment;
import android.util.Log;

public class HttpRequester {
        
    private static final String DEFAULT_USER_AGENT = "Mozilla/5.0";
    
    private HttpClient httpClient = null;
    private HttpGet httpGet = null;
    
    public String sendGet(String url){        
        
        String returnMessage = "";
        
        try {
        	
            if(httpClient == null)
                    httpClient = new DefaultHttpClient();
                            
            httpGet = new HttpGet(url);
            
            HttpResponse response = httpClient.execute(httpGet);
            
            int responseCode = response.getStatusLine().getStatusCode();
            
            returnMessage = EntityUtils.toString(response.getEntity());
            
        }
        
        catch(ClientProtocolException e) {
            Log.e("HttpRequester", "sendGet() error: "+e.toString());
        }
        
        catch(IOException e) {
            Log.e("HttpRequester", "sendGet() error: "+e.toString());
        }
        
        catch(Exception e){
            Log.e("HttpRequester", "sendGet() error: "+e.toString());
        }
        
        finally{
        	return returnMessage;
        }         
    }
    
    
    
    public void getPicture(String url, File pictureFile){
    	String returnMessage = "";
        
        try {
        	
            if(httpClient == null)
                    httpClient = new DefaultHttpClient();
            
            httpGet = new HttpGet(url);
            
            HttpResponse response = httpClient.execute(httpGet);
            
            int responseCode = response.getStatusLine().getStatusCode();
            
            Log.i("HttpRequester", "getPicture: response code: "+String.valueOf(responseCode));
            
            
    		FileOutputStream out = new FileOutputStream(pictureFile);
    		response.getEntity().writeTo(out);
    		
    		out.close();
        }
        
        catch(ClientProtocolException e) {
            Log.e("HttpRequester", "sendGet() error: "+e.toString());
        }
        
        catch(IOException e) {
            Log.e("HttpRequester", "sendGet() error: "+e.toString());
        }
        
        catch(Exception e){
            Log.e("HttpRequester", "sendGet() error: "+e.toString());
        }
    }
    
    
	public void getPicture_Aux(String url, File pictureFile) throws IOException{
		
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		
		String[] urlComponents = url.split("/"); 
		
		String fileName = urlComponents[urlComponents.length - 1];
		
		// optional default is GET
		con.setRequestMethod("GET");
 
		//add request header
		con.setRequestProperty("User-Agent", HttpRequester.DEFAULT_USER_AGENT);
		
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);
		
		
		byte[] buffer = new byte[512];
		BufferedInputStream in = new BufferedInputStream(con.getInputStream());
		int bytesRead = in.read(buffer);
		
		
		FileOutputStream out = new FileOutputStream(pictureFile);
		
		while (bytesRead != -1) {
			out.write(buffer);
			buffer = new byte[512];
			bytesRead = in.read(buffer);
		}
		
		
		in.close();
		out.close(); 
	}
	
    
    
    
    
    
    
}