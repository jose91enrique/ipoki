/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ipoki.xacoveo.javame;

import java.util.Hashtable;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

/**
 *
 * @author Xavi
 */
public class XacoveoSettings {
    	private final static XacoveoSettings INSTANCE = new XacoveoSettings();

    	public static String UserName = "";
	public static String UserPassword = "";
	public static String UpdateFreq = "15";
	public static String UserKey;
	public static String Recording;
	public static String Private;
	public static boolean Connected = false;
	public static long Lapse;
	public static String Language = "EN";
	public static String ServerUrl = "http://www.ipoki.com/";
      	public static Hashtable recordMaps = new Hashtable();


        private XacoveoSettings() {}

        public static XacoveoSettings getInstance()
	{
		return INSTANCE;
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

	public static void setSettings(RecordStore store) {
            XacoveoSettings settings = XacoveoSettings.getInstance();

            settings.createRecord(store, XacoveoSettings.UserName, RecordTypes.USER);
            settings.createRecord(store, XacoveoSettings.UserPassword, RecordTypes.PASSWORD);
            settings.createRecord(store, XacoveoSettings.UpdateFreq, RecordTypes.UPDATE_FREQ);
            settings.createRecord(store, XacoveoSettings.Language, RecordTypes.LANGUAGE);
	}

        private void createRecord(RecordStore recordStore, String data, int recordType)
	{
		int recordLength = 1;
		byte[] dataBytes = data.getBytes();

		if (data.length() > 0)
			recordLength = dataBytes.length + 1;

		byte[] record = new byte[recordLength];
		record[0] = (byte)recordType;
		for(int i = 1; i < recordLength; i++)
			record[i] = dataBytes[i-1];
		try
		{
			recordStore.addRecord(record, 0, recordLength);
		}
		catch(RecordStoreException rse)
		{
			System.out.println(rse.getMessage());
			rse.printStackTrace();
		}
	}


	public static void getSettings(RecordStore store)  throws Exception {
                XacoveoSettings settings = XacoveoSettings.getInstance();
         	// Enumerate records without filter nor order
		RecordEnumeration re = store.enumerateRecords(null, null, false);
		while(re.hasNextElement())
		{
			int id = re.nextRecordId();
			byte[] record = store.getRecord(id);
			int typeId = record[0];
			settings.processRecord(record, typeId);
			XacoveoSettings.recordMaps.put(new Integer(typeId), new Integer(id));
		}
	}

        private void processRecord(byte[] record, int typeId)
	{
		// Each record type has a different id
		switch(typeId)
		{
		case RecordTypes.USER:
			XacoveoSettings.UserName = new String(record, 1, record.length - 1);
			break;
		case RecordTypes.PASSWORD:
			XacoveoSettings.UserPassword = new String(record, 1, record.length - 1);
			break;
		case RecordTypes.UPDATE_FREQ:
			XacoveoSettings.UpdateFreq = new String(record, 1, record.length - 1);
			break;
		case RecordTypes.LANGUAGE:
			XacoveoSettings.Language = new String(record, 1, record.length - 1);
			break;
		}
	}
}
