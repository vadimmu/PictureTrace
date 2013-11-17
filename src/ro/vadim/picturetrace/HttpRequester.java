package ro.vadim.picturetrace;

import java.io.BufferedReader;
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

import android.util.Log;

public class HttpRequester {
        
    private static final String DEFAULT_USER_AGENT = "Mozilla/5.0";
    
    private static HttpClient httpClient = null;
    private static HttpGet httpGet = null;
    
    public static String sendGet(String url){        
        
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
}