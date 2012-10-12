package com.yast.android.yastlib;

public class YastRequestFolders extends YastRequest {
	public YastRequestFolders(String username, String hash) {
		super("data.getFolders");
		
		content = "<user>" + username + "</user><hash>" + hash + "</hash>";
	}
}
