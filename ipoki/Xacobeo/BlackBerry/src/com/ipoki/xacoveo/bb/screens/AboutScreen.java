package com.ipoki.xacoveo.bb.screens;

import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;

import com.ipoki.xacoveo.bb.EPeregrinoSettings;
import com.ipoki.xacoveo.bb.local.XacoveoLocalResource;

public class AboutScreen extends MainScreen implements XacoveoLocalResource {
	
	public AboutScreen() {
		add(new LabelField(EPeregrinoSettings.XacoveoResource.getString(ABOUT_SCR_ABOUT)));
	}
}
