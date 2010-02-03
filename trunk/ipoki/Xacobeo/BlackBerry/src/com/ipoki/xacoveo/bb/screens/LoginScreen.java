package com.ipoki.xacoveo.bb.screens;

import java.util.Hashtable;
import java.util.Vector;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

import com.ipoki.xacoveo.bb.HttpRequestHelper;
import com.ipoki.xacoveo.bb.HttpRequester;
import com.ipoki.xacoveo.bb.Utils;
import com.ipoki.xacoveo.bb.XacoVeoSettings;
import com.ipoki.xacoveo.bb.local.XacoveoLocalResource;

public class LoginScreen extends MainScreen implements FieldChangeListener, HttpRequester, XacoveoLocalResource {
	EditField usernameField;
	PasswordEditField passwordField;
	ButtonField loginButton;
	ButtonField exitButton;
	
	public LoginScreen() {
		super(NO_VERTICAL_SCROLL);
		
		PersistentObject persistentObject = PersistentStore.getPersistentObject(XacoVeoSettings.KEY);
		Hashtable settings;
		if ((settings = (Hashtable) persistentObject.getContents()) != null) {
			 XacoVeoSettings.setSettings(settings);
		}

		Bitmap tmpLogo = Bitmap.getBitmapResource("res/logo_b.png");
		final int displayWidth = Display.getWidth();
		final int displayHeight = Display.getHeight();
		int spaceWidth = 180;
		int topSpacerHeight = 85;
		int bottonSpacerHeight = 20;
		Font appFont;
		try {
			FontFamily fontFam = FontFamily.forName("BBAlpha Sans");
			if (displayWidth < 320) {
				tmpLogo = Bitmap.getBitmapResource("res/logo_s.png");
				appFont = fontFam.getFont(Font.PLAIN, 8, Ui.UNITS_pt);
				spaceWidth = 100;
				topSpacerHeight = 50;
				bottonSpacerHeight = 35;
			}
			else if (displayWidth < 370) {
				tmpLogo = Bitmap.getBitmapResource("res/logo_m.png");
				appFont = fontFam.getFont(Font.PLAIN, 8, Ui.UNITS_pt);
				spaceWidth = 120;
				topSpacerHeight = 60;
				bottonSpacerHeight = 0;
			}
			else {
				appFont = fontFam.getFont(Font.PLAIN, 9, Ui.UNITS_pt);
			}
			Font.setDefaultFont(appFont);
		} catch (ClassNotFoundException e) {
		}
		final Bitmap logoBitmap = tmpLogo;
		
		
		VerticalFieldManager mainManager = new VerticalFieldManager(
				VerticalFieldManager.USE_ALL_WIDTH | VerticalFieldManager.USE_ALL_HEIGHT | VerticalFieldManager.NO_VERTICAL_SCROLL){
		    // override pain method to create the background image
		    public void paint(Graphics graphics) {
		        // draw the background image
		        graphics.drawBitmap(0, 0, displayWidth, displayHeight, logoBitmap, 0, 0);
		        super.paint(graphics);
		    }            
		};
		mainManager.add(new SpacerField(displayWidth, topSpacerHeight));

		LabelField labelUser = new LabelField(XacoVeoSettings.XacoveoResource.getString(LOG_SCR_USER));
		usernameField = new EditField("", XacoVeoSettings.UserName);
		LabelField labelPass = new LabelField(XacoVeoSettings.XacoveoResource.getString(LOG_SCR_PASS));
		passwordField = new PasswordEditField("", XacoVeoSettings.UserPassword);
		
		VerticalFieldManager spaceManager = new VerticalFieldManager(VerticalFieldManager.NO_VERTICAL_SCROLL);
		SpacerField sfLeft = new SpacerField(spaceWidth, 20);
		spaceManager.add(sfLeft);
		VerticalFieldManager vertManager = new VerticalFieldManager();
		vertManager.add(labelUser);
		vertManager.add(usernameField);
		vertManager.add(labelPass);
		vertManager.add(passwordField);

		HorizontalFieldManager loginPassManager = new HorizontalFieldManager(Field.FIELD_RIGHT);
		loginPassManager.add(spaceManager);
		loginPassManager.add(vertManager);
		
		mainManager.add(loginPassManager);

		mainManager.add( new SpacerField(displayWidth, bottonSpacerHeight));
		
		loginButton = new ButtonField(XacoVeoSettings.XacoveoResource.getString(LOG_SCR_LOGIN), ButtonField.CONSUME_CLICK);
		loginButton.setChangeListener(this);
		exitButton = new ButtonField(XacoVeoSettings.XacoveoResource.getString(LOG_SCR_QUIT), ButtonField.CONSUME_CLICK);
		exitButton.setChangeListener(this);
		HorizontalFieldManager buttonManager = new HorizontalFieldManager(Field.FIELD_RIGHT);
		buttonManager.add(loginButton);
		buttonManager.add(exitButton);
		mainManager.add(buttonManager);
		
		add(mainManager);
	}
	
	private void makeLoginRequest() {
		String url = XacoVeoSettings.getLoginUrl(usernameField.getText(), passwordField.getText());
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
				Dialog.alert(XacoVeoSettings.XacoveoResource.getString(LOG_SCR_CONNECTION_FAILURE) + message);
			}
		});
	}
	
	public void requestSucceeded(byte[] result, String contentType) {
		String message = new String(result, 0, result.length);
		System.out.println(message);
		Vector tokens = Utils.parseMessage(message);
    	if (tokens.size() >= 6) {
	 		XacoVeoSettings.UserKey = (String) tokens.elementAt(1);
	 		XacoVeoSettings.Recording = (String) tokens.elementAt(4);
	 		XacoVeoSettings.Private = (String) tokens.elementAt(5);
    	}
    	if (XacoVeoSettings.UserName == "") {
			XacoVeoSettings.UserName = usernameField.getText();
			XacoVeoSettings.UserPassword = passwordField.getText();
			PersistentObject persistentObject = PersistentStore.getPersistentObject(XacoVeoSettings.KEY);
			persistentObject.setContents(XacoVeoSettings.getSettings());
    	}

		UiApplication.getUiApplication().invokeLater(new Runnable() {
			public void run() {
				UiApplication.getUiApplication().popScreen(LoginScreen.this);
				UiApplication.getUiApplication().pushScreen(new LocationScreen());
			}
		});
	}
}
