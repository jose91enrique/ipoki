package com.ipoki.xacoveo.bb;

import java.util.Hashtable;

import net.rim.device.api.i18n.ResourceBundle;

import com.ipoki.xacoveo.bb.local.XacoveoLocalResource;

public class XacoVeoSettings implements XacoveoLocalResource{
	public static final long KEY = 0xe57fdf6ef36bdfe1L;
	public static ResourceBundle XacoveoResource = ResourceBundle.getBundle(BUNDLE_ID, BUNDLE_NAME);

	public static String UserName = "";
	public static String UserPassword = "";
	public static String UpdateFreq = "15";
	public static String UserKey;
	public static String Recording;
	public static String Private;
	public static boolean Connected = false;
	public static long Lapse;
	public static int Language = 0;
	public static String ServerUrl = "http://www.xacoveo.com/";
	
	public static String getPositionUrl(String lat, String lon) {
		return ServerUrl + "geoserver/ear.php?iduser=" + UserKey +
		"&lat=" + lat + "&lon=" + lon;
	}

	public static String getFriendsUrl() {
		return ServerUrl + "myfriends2.php?iduser=" + UserKey;
	}
	
	public static String getLoginUrl(String user, String password) {
		return ServerUrl + "signin.php?user=" + user + "&pass=" + password;
	}
	
	public static String getRecordOnUrl() {
		return ServerUrl + "set_h_on.php?iduser=" + UserKey;
	}
	
	public static String getRecordOffUrl() {
		return ServerUrl + "set_h_off.php?iduser=" + UserKey;
	}
	
	public static String getPublicOnUrl() {
		return ServerUrl + "set_p_on.php?iduser=" + UserKey;
	}

	public static String getPublicOffUrl() {
		return ServerUrl + "set_p_off.php?iduser=" + UserKey;
	}

	public static void setSettings(Hashtable settings) {
		XacoVeoSettings.UserName = (String) settings.get("username");
		XacoVeoSettings.UserPassword = (String) settings.get("password");
		XacoVeoSettings.UpdateFreq = (String) settings.get("updatefreq");
		String lang = (String)settings.get("language");
		if (lang != null)
			XacoVeoSettings.Language = Integer.parseInt(lang);
	}
	
	public static Hashtable getSettings() {
		Hashtable settings = new Hashtable();
		settings.put("username", XacoVeoSettings.UserName);
		settings.put("password", XacoVeoSettings.UserPassword);
		settings.put("updatefreq", XacoVeoSettings.UpdateFreq);
		settings.put("language", String.valueOf(XacoVeoSettings.Language));
		
		return settings;
	}
}
