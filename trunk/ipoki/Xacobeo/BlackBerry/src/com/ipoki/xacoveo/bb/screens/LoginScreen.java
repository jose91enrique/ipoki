package com.ipoki.xacoveo.bb.screens;

import java.util.Hashtable;
import java.util.Vector;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;

import com.ipoki.xacoveo.bb.EPeregrinoSettings;
import com.ipoki.xacoveo.bb.HttpRequestHelper;
import com.ipoki.xacoveo.bb.HttpRequester;
import com.ipoki.xacoveo.bb.Utils;
import com.ipoki.xacoveo.bb.local.XacoveoLocalResource;

public class LoginScreen extends MainScreen implements FieldChangeListener, HttpRequester, XacoveoLocalResource {
	BitmapField logoBitmapField;
	EditField usernameField;
	PasswordEditField passwordField;
	ButtonField loginButton;
	ButtonField exitButton;
	
	public LoginScreen() {
		PersistentObject persistentObject = PersistentStore.getPersistentObject(EPeregrinoSettings.KEY);
		Hashtable settings;
		if ((settings = (Hashtable) persistentObject.getContents()) != null) {
			 EPeregrinoSettings.setSettings(settings);
		}
		
		Bitmap logoBitmap = Bitmap.getBitmapResource("res/logo.png");
		logoBitmapField = new BitmapField(logoBitmap, Field.FIELD_HCENTER);
		add(logoBitmapField);
		
		add(new SeparatorField());

		usernameField = new EditField("", EPeregrinoSettings.UserName);
		passwordField = new PasswordEditField("", EPeregrinoSettings.UserPassword);
		add(new LabelField(EPeregrinoSettings.XacoveoResource.getString(LOG_SCR_USER)));
		add(usernameField);
		add(new LabelField(EPeregrinoSettings.XacoveoResource.getString(LOG_SCR_PASS)));
		add(passwordField);

		add(new SeparatorField());

		loginButton = new ButtonField(EPeregrinoSettings.XacoveoResource.getString(LOG_SCR_LOGIN), ButtonField.CONSUME_CLICK);
		loginButton.setChangeListener(this);
		exitButton = new ButtonField(EPeregrinoSettings.XacoveoResource.getString(LOG_SCR_QUIT), ButtonField.CONSUME_CLICK);
		exitButton.setChangeListener(this);
		HorizontalFieldManager buttonManager = new HorizontalFieldManager(Field.FIELD_RIGHT);
		buttonManager.add(loginButton);
		buttonManager.add(exitButton);
		add(buttonManager);
	}
	
	private void makeLoginRequest() {
		String url = "http://www.ipoki.com/signin.php?user=" + usernameField.getText() + "&pass=" + passwordField.getText();
		HttpRequestHelper helper = new HttpRequestHelper(url, this);
		helper.start();
	}

	public void fieldChanged(Field field, int context) {
		if (field == loginButton) {
			makeLoginRequest();
		}
		else if (field == exitButton) {
			this.close();
		}
	}

	public void requestFailed(final String message) {
		UiApplication.getUiApplication().invokeLater(new Runnable() {
			public void run() {
				Dialog.alert(EPeregrinoSettings.XacoveoResource.getString(LOG_SCR_CONNECTION_FAILURE) + message);
			}
		});
	}
	
	public void requestSucceeded(byte[] result, String contentType) {
		String message = new String(result, 0, result.length);
		System.out.println(message);
		Vector tokens = Utils.parseMessage(message);
    	if (tokens.size() >= 6) {
	 		EPeregrinoSettings.UserKey = (String) tokens.elementAt(1);
	 		EPeregrinoSettings.Recording = (String) tokens.elementAt(4);
	 		EPeregrinoSettings.Private = (String) tokens.elementAt(5);
    	}
    	if (EPeregrinoSettings.UserName == "") {
			EPeregrinoSettings.UserName = usernameField.getText();
			EPeregrinoSettings.UserPassword = passwordField.getText();
			PersistentObject persistentObject = PersistentStore.getPersistentObject(EPeregrinoSettings.KEY);
			persistentObject.setContents(EPeregrinoSettings.getSettings());
    	}

		UiApplication.getUiApplication().invokeLater(new Runnable() {
			public void run() {
				UiApplication.getUiApplication().popScreen(LoginScreen.this);
				UiApplication.getUiApplication().pushScreen(new LocationScreen());
			}
		});
	}
}
