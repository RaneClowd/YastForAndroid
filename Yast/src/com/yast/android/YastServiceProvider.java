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
		        XmlPullParser parser = Xml.newPullParser();
	            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
	            parser.setInput(new StringReader(results));
	            parser.nextTag();
	            
	            Dictionary<String, Object> parsedResponse = readResponse(parser);
	            
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

		private Dictionary<String, Object> readResponse(XmlPullParser parser) throws XmlPullParserException, IOException {
		    Hashtable<String, Object> response = new Hashtable<String, Object>();
		    parser.require(XmlPullParser.START_TAG, "", "response");
			
			for (int i=0; i<parser.getAttributeCount(); i++) {
		    	response.put(parser.getAttributeName(i), parser.getAttributeValue(i));
		    }
			
		    while (parser.next() != XmlPullParser.END_TAG) {
		        if (parser.getEventType() != XmlPullParser.START_TAG) {
		            continue;
		        }
		        
		        response.put(parser.getName(), readNode(parser));
		    }
		    return response;
		}
		
		private Object readNode(XmlPullParser parser) throws IOException, XmlPullParserException {
			return readText(parser);
		}
		
		private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
		    String result = null;
		    if (parser.next() == XmlPullParser.TEXT) {
		        result = parser.getText();
		        parser.nextTag();
		    }
		    return result;
		}
	}
}
