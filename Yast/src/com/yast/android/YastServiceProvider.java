package com.yast.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Dictionary;
import java.util.Hashtable;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.dragondevelopment.yast.Callback;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

public class YastServiceProvider {

	private static YastServiceProvider instance;
	
	public static YastServiceProvider getInstance()
	{
		if (YastServiceProvider.instance == null) {
			YastServiceProvider.instance = new YastServiceProvider();
		}
		
		return YastServiceProvider.instance;
	}
	
	private YastServiceProvider()
	{ }
	
	public void logIn(String username, String password, Callback command)
	{
		String request = "<request req=\"auth.login\" id=\"133\"> <user>" + username + "</user><password>" + password + "</password></request>";
	    SubmitRequest submitter = new SubmitRequest();
	    submitter.callback = command;
	    submitter.execute(request);
	}
	
	private class SubmitRequest extends AsyncTask<Object, Void, YastResponse> {
		public Callback callback;

		@Override
		protected YastResponse doInBackground(Object... params) {
	       	try {
	       		URL url = new URL("https://www.yast.com/1.0/?request=" + URLEncoder.encode((String)params[0]));
		    	HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		    	conn.setDoOutput(true);
		    	conn.setDoInput(true);
		    	conn.setRequestMethod("GET");
		    	conn.setUseCaches (false);
		    	
			    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			    StringBuilder builder = new StringBuilder();
			    String line;
			    while ((line = rd.readLine()) != null) {
			    	builder.append(line + "\n");
			    }
			   	rd.close();
			   
			   	String results = builder.toString();
			   	
			   	results = "<company><division><employee>Bob</employee><employee>George</employee><employee>Tom</employee></division><division><employee>Leslie</employee><employee>Tiffany</employee><employee>Sam</employee></division></company>";
			   	
		        XMLParser.parseXML(results);
	            
	            return null;
		        //return readResponse(parser);
			} catch (Exception e) {
			    Log.d("yast", "error: " + e.getMessage());
			}
	       	return null;
		}
		
		@Override
		protected void onPostExecute(YastResponse result) {
	        this.callback.execute("status: " + result.status + ", hash: " + result.hash);
		}
	}
}
