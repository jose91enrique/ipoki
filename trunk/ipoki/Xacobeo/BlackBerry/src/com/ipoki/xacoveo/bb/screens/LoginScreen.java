package com.ipoki.xacobeo.bb.screens;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.container.MainScreen;

public class LoginScreen extends MainScreen {
	BitmapField logoBitmapField;
	
	public LoginScreen() {
		Bitmap logoBitmap = Bitmap.getBitmapResource("res/logo.png");
		logoBitmapField = new BitmapField(logoBitmap, Field.FIELD_HCENTER);
		add(logoBitmapField);
	}
}
