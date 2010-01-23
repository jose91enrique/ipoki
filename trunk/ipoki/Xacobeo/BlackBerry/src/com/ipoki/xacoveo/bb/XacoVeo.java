package com.ipoki.xacoveo.bb;

import net.rim.device.api.ui.UiApplication;

import com.ipoki.xacoveo.bb.screens.LoginScreen;

public class XacoVeo extends UiApplication {
	public XacoVeo() {
		LoginScreen loginScreen = new LoginScreen();
		pushScreen(loginScreen);
	}
	
	public static void main(String[] args) {
		XacoVeo app = new XacoVeo();
		app.enterEventDispatcher();
	}
}
