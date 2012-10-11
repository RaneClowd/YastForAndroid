package com.yast.android;

import java.util.ArrayList;

public class XmlNode {
	
	private ArrayList<String> keys;
	private ArrayList<Object> values;
	
	private int position;
	
	public XmlNode()
	{
		this.keys = new ArrayList<String>();
		this.values = new ArrayList<Object>();
		
		this.position = 0;
	}

	public void addPair(String key, Object value)
	{
		keys.add(key);
		values.add(value);
	}
	
	public String getKey()
	{
		return this.keys.get(this.position);
	}
	
	public Object getValue()
	{
		return this.values.get(this.position);
	}
	
	public boolean moveToNext()
	{
		this.position += 1;
		return this.keys.size() <= this.position;
	}
}
