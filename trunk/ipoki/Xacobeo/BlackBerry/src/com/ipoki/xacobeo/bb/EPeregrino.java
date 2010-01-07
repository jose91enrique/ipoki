package com.ipoki.xacobeo.bb;

import net.rim.device.api.ui.UiApplication;

import com.ipoki.xacobeo.bb.screens.LoginScreen;

public class EPeregrino extends UiApplication {
	public EPeregrino() {
		LoginScreen loginScreen = new LoginScreen();
		pushScreen(loginScreen);
	}
	
	public static void main(String[] args) {
		EPeregrino app = new EPeregrino();
		app.enterEventDispatcher();
	}
}
