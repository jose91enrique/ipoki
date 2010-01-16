package com.ipoki.xacoveo.bb;

import java.util.Hashtable;

import net.rim.device.api.i18n.ResourceBundle;

import com.ipoki.xacoveo.bb.local.XacoveoLocalResource;

public class EPeregrinoSettings implements XacoveoLocalResource{
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

	
	public static void setSettings(Hashtable settings) {
		EPeregrinoSettings.UserName = (String) settings.get("username");
		EPeregrinoSettings.UserPassword = (String) settings.get("password");
		EPeregrinoSettings.UpdateFreq = (String) settings.get("updatefreq");
		String lang = (String)settings.get("language");
		if (lang != null)
			EPeregrinoSettings.Language = Integer.parseInt(lang);
	}
	
	public static Hashtable getSettings() {
		Hashtable settings = new Hashtable();
		settings.put("username", EPeregrinoSettings.UserName);
		settings.put("password", EPeregrinoSettings.UserPassword);
		settings.put("updatefreq", EPeregrinoSettings.UpdateFreq);
		settings.put("language", String.valueOf(EPeregrinoSettings.Language));
		
		return settings;
	}
}
