package com.ipoki.xacoveo.bb.screens;

import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.api.ui.container.MainScreen;

import com.ipoki.xacoveo.bb.EPeregrinoSettings;
import com.ipoki.xacoveo.bb.local.XacoveoLocalResource;

public class ConfigurationScreen extends MainScreen implements XacoveoLocalResource{
	EditField usernameField;
	PasswordEditField passwordField;
	EditField freqField;
	ObjectChoiceField idiomaField;

	public ConfigurationScreen() {
		usernameField = new EditField(EPeregrinoSettings.XacoveoResource.getString(CONF_SCR_USERNAME), EPeregrinoSettings.UserName);
		passwordField = new PasswordEditField(EPeregrinoSettings.XacoveoResource.getString(CONF_SCR_PASSWORD), EPeregrinoSettings.UserPassword);
		freqField = new EditField(EPeregrinoSettings.XacoveoResource.getString(CONF_SCR_UPDATE), EPeregrinoSettings.UpdateFreq, 5, EditField.FILTER_NUMERIC);
		
		add(usernameField);
		add(passwordField);
		add(freqField);
		add(idiomaField);
	}
	
	protected void makeMenu(Menu menu, int instance) {
		super.makeMenu(menu, instance);
		menu.add(new MenuItem(EPeregrinoSettings.XacoveoResource.getString(CONF_SCR_SAVE),10, 10) {
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
