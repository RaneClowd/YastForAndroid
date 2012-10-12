package com.yast.android.yastlib;

public class YastRequestUserInfo extends YastRequest {
	public YastRequestUserInfo(String username, String hash){
		super("user.getInfo");
		
		this.content = "<user>" + username + "</user><hash>" + hash + "</hash>";
	}
}
