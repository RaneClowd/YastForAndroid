package com.yast.android.yastlib;

import org.w3c.dom.Element;

import com.yast.android.yastlib.exceptions.YastLibBadResponseException;

public class YastResponseUserInfo extends YastResponse {
	private YastUserInfo info;
	
	public YastUserInfo getUserInfo(){
		return info;
	}
	
	@Override
	public void processResponse(Element response) throws YastLibBadResponseException {
		info = new YastUserInfo(response);
	}

}
