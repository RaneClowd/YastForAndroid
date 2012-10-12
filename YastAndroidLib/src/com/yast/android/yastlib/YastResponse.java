package com.yast.android.yastlib;

import org.w3c.dom.Element;

import com.yast.android.yastlib.exceptions.YastLibBadResponseException;

public abstract class YastResponse {
	protected int status;
	
	public int getStatus(){
		return status;
	}
	
	public void setStatus(int status){
		this.status = status;
	}
	
	public abstract void processResponse(Element response) throws YastLibBadResponseException;
}
