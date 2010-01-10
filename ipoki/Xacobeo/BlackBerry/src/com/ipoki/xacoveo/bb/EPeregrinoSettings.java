package com.ipoki.xacoveo.bb;

import java.util.Hashtable;

public class EPeregrinoSettings {
	public static final long KEY = 0xe57fdf6ef36bdfe1L;

	public static String UserName = "";
	public static String UserPassword = "";
	public static String UpdateFreq = "15";
	public static String UserKey;
	public static String Recording;
	public static String Private;
	public static boolean Connected = false;
	public static long Lapse;

	
	public static void setSettings(Hashtable settings) {
		EPeregrinoSettings.UserName = (String) settings.get("username");
		EPeregrinoSettings.UserPassword = (String) settings.get("password");
		EPeregrinoSettings.UpdateFreq = (String) settings.get("updatefreq");
	}
	
	public static Hashtable getSettings() {
		Hashtable settings = new Hashtable();
		settings.put("username", EPeregrinoSettings.UserName);
		settings.put("password", EPeregrinoSettings.UserPassword);
		settings.put("updatefreq", EPeregrinoSettings.UpdateFreq);
		
		return settings;
	}
}
