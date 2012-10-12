package com.yast.android.yastlib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.os.AsyncTask;

import com.yast.android.yastlib.exceptions.YastLibBadRequestException;
import com.yast.android.yastlib.exceptions.YastLibBadResponseException;
import com.yast.android.yastlib.exceptions.YastLibNotLoggedInException;

public class Yast {
	private static final String apiUrl = "https://www.yast.com/1.0/";
	//private static final String apiUrl = "http://fjas.com/1.0/";
	//private static final int requestTimeout = 600;

	private String username;
	private String hash;

	private static Yast _singelton;
	public static Yast get(){
		if(_singelton == null){
			_singelton = new Yast();
		}
		return _singelton;
	}

	public void setAuth(final String username, final String hash){
		this.username = username;
		this.hash = hash;
	}

	public void clearAuth(){
		this.username = null;
		this.hash = null;
	}

	public String getUsername(){
		return username;
	}

	public String getHash(){
		return hash;
	}

	public boolean hasCredentials(){
		return username != null && hash != null;
	}

	/*
	 * TODO: GetUserSettings, SetUserSettings, GetReport
	 * 
	 */

	public void login(final String username, final String password, final Callback callback) {
		YastResponseLogin resp = new YastResponseLogin();

		request(new YastRequestLogin(username, password), resp, new Callback() {
			@Override
			public void execute(String error, YastResponse response) {
				YastResponseLogin loginResp = (YastResponseLogin)response;
				
				Yast.this.username = username;
				Yast.this.hash = loginResp.getHash();
				
				callback.execute(error, response);
			}
		});
	}

	public YastUserInfo getUserInfo(Callback callback) {
		if (!hasCredentials()){ throw new YastLibNotLoggedInException(); };

		YastResponseUserInfo resp = new YastResponseUserInfo();
		request(new YastRequestUserInfo(username, hash), resp, callback);

		return resp.getUserInfo();
	}

	public void add(final YastDataObject object, Callback callback) {
		ArrayList<YastDataObject> list = new ArrayList<YastDataObject>();
		list.add(object);
		add(list, callback);
	}

	public void add(final ArrayList<? extends YastDataObject> objects, Callback callback) {
		if (!hasCredentials()){ throw new YastLibNotLoggedInException(); };

		// Check that objects really should be added, not just changed. This is done by checking they have a id (should be assigned by Yast.com)
		Iterator<? extends YastDataObject> it = objects.iterator();
		while(it.hasNext()){
			YastDataObject obj = it.next();

			if (obj.getId() > 0){
				// This object shold be changed instead.
				throw new YastLibBadRequestException("Trying to add object with an assigned id. This object should probably be changed instead.  Type: " + obj.getClass().getName() + ", id: " + obj.getId());

			}
		}

		// Check size of collection
		if (objects.size() == 0){
			Utilities.w("Yast.add(): Collection is empty");
			return;
		}

		YastResponseUpdatedObjects resp = new YastResponseUpdatedObjects();
		request(new YastRequestAdd(username, hash, objects), resp, callback);


		ArrayList<YastDataObject> updatedObjects = resp.getObjects();

		// Update objects
		if (updatedObjects.size() != objects.size()){
			Utilities.e("Size of returned objects are different then initial count, initial count: " + objects.size() + ", returned size: " + updatedObjects.size());
		} else {
			for (int i = 0; i < objects.size(); i++){
				objects.get(i).updateFromObject(updatedObjects.get(i));
			}
		}
	}

	public void change(final YastDataObject object, Callback callback) {
		ArrayList<YastDataObject> list = new ArrayList<YastDataObject>();
		list.add(object);
		change(list, callback);
	}

	public void change(final ArrayList<? extends YastDataObject> objects, Callback callback) {
		if (!hasCredentials()){ throw new YastLibNotLoggedInException(); };

		// Check that objects really should be added, not just changed. This is done by checking they have a id (should be assigned by Yast.com)
		Iterator<? extends YastDataObject> it = objects.iterator();
		while(it.hasNext()){
			YastDataObject obj = it.next();

			if (obj.getId() <= 0){
				// This object shold be changed instead.
				throw new YastLibBadRequestException("Trying to change object without an assigned id. This object should probably be added instead. Type: " + obj.getClass().getName() + ", internalid: " + obj.getInternalId());

			}
		}

		// Check length of collection
		if (objects.size() == 0){
			Utilities.w("Yast.change(): Collection is empty");
			return;
		}

		YastResponseUpdatedObjects resp = new YastResponseUpdatedObjects();
		request(new YastRequestChange(username, hash, objects), resp, callback);

		ArrayList<YastDataObject> updatedObjects = resp.getObjects();

		// Update objects
		if (updatedObjects.size() != objects.size()){
			Utilities.e("Size of returned objects are different then initial count, initial count: " + objects.size() + ", returned size: " + updatedObjects.size());
		} else {
			for (int i = 0; i < objects.size(); i++){
				objects.get(i).updateFromObject(updatedObjects.get(i));
			}
		}
	}

	public void delete(final YastDataObject object, Callback callback) {
		ArrayList<YastDataObject> list = new ArrayList<YastDataObject>();
		list.add(object);
		delete(list, callback);
	}

	/*
	 * Delete DataObject on Yast.com service
	 * 
	 * @param objects to delete
	 * @return true if deleted false otherwise
	 * 
	 */
	public void delete(final ArrayList<? extends YastDataObject> objects, Callback callback) {
		if (!hasCredentials()){ throw new YastLibNotLoggedInException(); };

		// Check that all objects has an id (or else it will be impossible to delete them
		Iterator<? extends YastDataObject> it = objects.iterator();
		while(it.hasNext()){
			YastDataObject obj = it.next();
			if (obj.getId() <= 0){
				// Missing id, removing it from array
				throw new YastLibBadRequestException("Trying to delete YastDataObject without id. Type: " + obj.getClass().getName() + ", InternalId: " + obj.getInternalId());
			}
		}

		// Check length of objects collection
		if (objects.size() == 0){
			Utilities.w("Yast.delete(): Collection is empty");
			return;
		}

		YastResponseDelete resp = new YastResponseDelete();
		request(new YastRequestDelete(username, hash, objects), resp, callback);
	}

	public ArrayList<YastProject> getProjects(Callback callback) {
		if (!hasCredentials()){ throw new YastLibNotLoggedInException(); };

		YastResponseProjects resp = new YastResponseProjects();
		request(new YastRequestProjects(username, hash), resp, callback);

		return resp.getProjects();
	}

	public ArrayList<YastFolder> getFolders(Callback callback) {
		if (!hasCredentials()){ throw new YastLibNotLoggedInException(); };

		YastResponseFolders resp = new YastResponseFolders();
		request(new YastRequestFolders(username, hash), resp, callback);

		return resp.getFolders();
	}

	public ArrayList<YastRecord> getRecords(final int timeFrom, Callback callback) { return getRecords(null, null, null, timeFrom, 0, callback);}
	public ArrayList<YastRecord> getRecords(final int timeFrom, final int timeTo, Callback callback) { return getRecords(null, null, null, timeFrom, timeTo, callback);}
	public ArrayList<YastRecord> getRecords(final ArrayList<Integer> typeIds, final ArrayList<Integer> ids, final ArrayList<Integer> parentIds, final int timeFrom, final int timeTo, Callback callback) {
		YastResponseRecords resp = new YastResponseRecords();
		request(new YastRequestRecords(username, hash, typeIds, ids, parentIds, timeFrom, timeTo), resp, callback);

		return resp.getRecords();
	}

	private void request(final YastRequest req, final YastResponse resp, Callback callback) {
		Request requester = new Request();
		requester.callback = callback;
		requester.execute(req, resp);
	}
	
	private class Request extends AsyncTask<Object, Void, YastResponse>
	{
		public Callback callback;
		private String error;
		
		@Override
		protected YastResponse doInBackground(Object... params) {
			YastRequest req = (YastRequest)params[0];
			YastResponse resp = (YastResponse)params[1];
			
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(apiUrl);

			try {
				httppost.setEntity(req.getEntity());

				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httppost);

				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

				//try {
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document dom = builder.parse(response.getEntity().getContent());
				

				NodeList responseItems = dom.getElementsByTagName("response");

				if (responseItems.getLength() > 0){
					Element responseElement = (Element)responseItems.item(0);


					NamedNodeMap attributes = responseElement.getAttributes();
					Node statusNode = attributes.getNamedItem("status");

					if (statusNode != null){
						String strStatus = statusNode.getNodeValue();
						int status = Integer.parseInt(strStatus);

						resp.setStatus(status);

						if (status == 0){
							resp.processResponse(responseElement);
						} else {
							this.error = "error: status of response " + status;
						}
					} else {
						this.error = "error: bad response";
					}
				}
			} catch (ClientProtocolException e) {
				this.error = "Failed to get response (ClientProtocolException), check cause for details";
			} catch (IOException e) {
				this.error = "Failed to get response (IOException), check cause for details";
			} catch (ParserConfigurationException e) {
				this.error = "Failed to parse API response (ParserConfigurationException), check cause for details";
			} catch (SAXException e) {
				this.error = "Failed to parse API response (SAXException), check cause for details";
			} catch (YastLibBadResponseException e) {
				this.error = "error: bad response";
			}
			return resp;
		}

		@Override
		protected void onPostExecute(YastResponse result) {
			this.callback.execute(this.error, result);
		}
	}

}
