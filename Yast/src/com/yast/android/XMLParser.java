package com.yast.android;

import java.io.IOException;
import java.io.StringReader;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class XMLParser {

	public static Dictionary<String, Object> parseXML(String xml)
	{
		XmlPullParser parser = Xml.newPullParser();
        try {
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(new StringReader(xml));
        
			XmlNode parsedResponse = readXML(parser);
			
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private static XmlNode readXML(XmlPullParser parser) throws XmlPullParserException, IOException {
	    XmlNode response = new XmlNode();
		
		for (int i=0; i<parser.getAttributeCount(); i++) {
	    	response.addPair(parser.getAttributeName(i), parser.getAttributeValue(i));
	    }
		
		int state = parser.next();
	    while (state != XmlPullParser.END_TAG) {
		    if (state == XmlPullParser.TEXT) {
		        response.addPair("value", parser.getText());
		    }
		    else if (state == XmlPullParser.START_TAG) {
				response.addPair(parser.getName(), readXML(parser));
			}

		    state = parser.nextTag();
	    }
	    return response;
	}
}
