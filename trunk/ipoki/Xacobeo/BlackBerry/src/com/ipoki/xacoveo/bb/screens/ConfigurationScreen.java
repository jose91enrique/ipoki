package com.ipoki.xacoveo.bb.screens;

import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.api.ui.container.MainScreen;

import com.ipoki.xacoveo.bb.XacoVeoSettings;
import com.ipoki.xacoveo.bb.local.XacoveoLocalResource;

public class ConfigurationScreen extends MainScreen implements XacoveoLocalResource{
	EditField usernameField;
	PasswordEditField passwordField;
	EditField freqField;

	public ConfigurationScreen() {
		usernameField = new EditField(XacoVeoSettings.XacoveoResource.getString(CONF_SCR_USERNAME), XacoVeoSettings.UserName);
		passwordField = new PasswordEditField(XacoVeoSettings.XacoveoResource.getString(CONF_SCR_PASSWORD), XacoVeoSettings.UserPassword);
		freqField = new EditField(XacoVeoSettings.XacoveoResource.getString(CONF_SCR_UPDATE), XacoVeoSettings.UpdateFreq, 5, EditField.FILTER_NUMERIC);
		
		add(usernameField);
		add(passwordField);
		add(freqField);
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
