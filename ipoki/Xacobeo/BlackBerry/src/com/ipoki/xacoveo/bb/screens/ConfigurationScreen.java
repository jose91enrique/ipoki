package com.ipoki.xacoveo.bb.screens;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
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
		idiomaField = new ObjectChoiceField(EPeregrinoSettings.XacoveoResource.getString(CONF_SCR_LANG), 
				new String[] {
						EPeregrinoSettings.XacoveoResource.getString(CONF_SCR_SPANISH), 
						EPeregrinoSettings.XacoveoResource.getString(CONF_SCR_GALICIAN), 
						EPeregrinoSettings.XacoveoResource.getString(CONF_SCR_ENGLISH)});
		idiomaField.setSelectedIndex(EPeregrinoSettings.Language);
		
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
				EPeregrinoSettings.Language = idiomaField.getSelectedIndex();
				switch(EPeregrinoSettings.Language) {
				case 0:
					Locale.setDefault(Locale.get(Locale.LOCALE_es));
					break;
				case 1:
					Locale.setDefault(Locale.get(Locale.LOCALE_es));
					break;
				case 2:
					Locale.setDefault(Locale.get(Locale.LOCALE_es));
				}
				PersistentObject persistentObject = PersistentStore.getPersistentObject(EPeregrinoSettings.KEY);
				persistentObject.setContents(EPeregrinoSettings.getSettings());
			}
		});
	}
}
