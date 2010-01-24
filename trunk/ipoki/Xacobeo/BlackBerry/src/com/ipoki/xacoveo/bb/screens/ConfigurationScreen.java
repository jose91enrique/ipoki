package com.ipoki.xacoveo.bb.screens;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

import com.ipoki.xacoveo.bb.XacoVeoSettings;
import com.ipoki.xacoveo.bb.local.XacoveoLocalResource;

public class ConfigurationScreen extends MainScreen implements XacoveoLocalResource{
	EditField usernameField;
	PasswordEditField passwordField;
	EditField freqField;

	public ConfigurationScreen() {
		super(NO_VERTICAL_SCROLL);
		final Bitmap logoBitmap = Bitmap.getBitmapResource("res/fondo.png");
		final int displayWidth = Display.getWidth();
		final int displayHeight = Display.getHeight();

		VerticalFieldManager mainManager = new VerticalFieldManager(
				VerticalFieldManager.USE_ALL_WIDTH | VerticalFieldManager.USE_ALL_HEIGHT | VerticalFieldManager.NO_VERTICAL_SCROLL){
		    // override pain method to create the background image
		    public void paint(Graphics graphics) {
		        // draw the background image
		        graphics.drawBitmap(0, 0, displayWidth, displayHeight, logoBitmap, 0, 0);
		        super.paint(graphics);
		    }            
		};

		LabelField nameLabel = new LabelField(XacoVeoSettings.XacoveoResource.getString(CONF_SCR_USERNAME));
		nameLabel.setFont(getFont().derive(Font.BOLD));
		usernameField = new EditField("", XacoVeoSettings.UserName);
		LabelField passLabel = new LabelField(XacoVeoSettings.XacoveoResource.getString(CONF_SCR_PASSWORD));
		passLabel.setFont(getFont().derive(Font.BOLD));
		passwordField = new PasswordEditField("", XacoVeoSettings.UserPassword);
		LabelField freqLabel = new LabelField(XacoVeoSettings.XacoveoResource.getString(CONF_SCR_UPDATE));
		freqLabel.setFont(getFont().derive(Font.BOLD));
		freqField = new EditField("", XacoVeoSettings.UpdateFreq, 5, EditField.FILTER_NUMERIC);
		
		mainManager.add(nameLabel);
		mainManager.add(usernameField);
		mainManager.add(new SeparatorField());
		mainManager.add(passLabel);
		mainManager.add(passwordField);
		mainManager.add(new SeparatorField());
		mainManager.add(freqLabel);
		mainManager.add(freqField);
		
		add(mainManager);
	}
	
	protected void makeMenu(Menu menu, int instance) {
		super.makeMenu(menu, instance);
		menu.add(new MenuItem(XacoVeoSettings.XacoveoResource.getString(CONF_SCR_SAVE),10, 10) {
			public void run() {
				XacoVeoSettings.UserName = usernameField.getText().trim();
				XacoVeoSettings.UserPassword = passwordField.getText().trim();
				XacoVeoSettings.UpdateFreq = freqField.getText().trim();
				PersistentObject persistentObject = PersistentStore.getPersistentObject(XacoVeoSettings.KEY);
				persistentObject.setContents(XacoVeoSettings.getSettings());
			}
		});
	}
}
