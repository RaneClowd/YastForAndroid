package com.yast.android.yastlib;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.telephony.PhoneNumberUtils;
import android.util.Log;

/**
 * 
 * @author jorgen
 * Class providing useful tools that don't belong anywhere else.
 */
public class Utilities {
	// Logging functions
	private static String TAG = "YastAndroidLib";	
	public static void d(String s){
		Log.d(Utilities.TAG, s);
	}
	
	public static void i(String s){
		Log.i(Utilities.TAG, s);
	}
	
	public static void w(String s){
		Log.w(Utilities.TAG, s);
	}
	
	public static void e(String s){
		Log.e(Utilities.TAG, s);
	}
	
	public static String getIndexPhoneNumber(String nr){
		nr = PhoneNumberUtils.stripSeparators(nr);
		if (nr.length() >= 6){
			return nr.substring(nr.length() - 6);
		} else {
			return nr;
		}
	}
	
	public static String getDeviceInfo(){
		String res = "Android";
		if (android.os.Build.VERSION.SDK != null){
			res += " " + android.os.Build.VERSION.SDK;
		}
		if (android.os.Build.MODEL != null){
			res += " " + android.os.Build.MODEL;
		}
		return res;
	}
}

