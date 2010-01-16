package com.ipoki.xacoveo.bb.screens;

import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.api.ui.container.MainScreen;

import com.ipoki.xacoveo.bb.EPeregrinoSettings;

public class ConfigurationScreen extends MainScreen{
	EditField usernameField;
	PasswordEditField passwordField;
	EditField freqField;

	public ConfigurationScreen() {
		usernameField = new EditField("Username: ", EPeregrinoSettings.UserName);
		passwordField = new PasswordEditField("Password: ", EPeregrinoSettings.UserPassword);
		freqField = new EditField("Frequency: ", EPeregrinoSettings.UpdateFreq, 5, EditField.FILTER_NUMERIC);
		add(usernameField);
		add(passwordField);
		add(freqField);
	}
	
	protected void makeMenu(Menu menu, int instance) {
		super.makeMenu(menu, instance);
		menu.add(new MenuItem("Guardar",10, 10) {
			public void run() {
				EPeregrinoSettings.UserName = usernameField.getText().trim();
				EPeregrinoSettings.UserPassword = passwordField.getText().trim();
				EPeregrinoSettings.UpdateFreq = freqField.getText().trim();
				PersistentObject persistentObject = PersistentStore.getPersistentObject(EPeregrinoSettings.KEY);
				persistentObject.setContents(EPeregrinoSettings.getSettings());
			}
		});
	}
}
