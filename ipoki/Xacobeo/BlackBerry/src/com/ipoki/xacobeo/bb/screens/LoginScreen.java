package com.ipoki.xacobeo.bb.screens;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;

import com.ipoki.xacobeo.bb.HttpRequestHelper;
import com.ipoki.xacobeo.bb.HttpRequester;

public class LoginScreen extends MainScreen implements FieldChangeListener, HttpRequester {
	BitmapField logoBitmapField;
	EditField usernameField;
	PasswordEditField passwordField;
	ButtonField loginButton;
	ButtonField exitButton;
	
	public LoginScreen() {
		Bitmap logoBitmap = Bitmap.getBitmapResource("res/logo.png");
		logoBitmapField = new BitmapField(logoBitmap, Field.FIELD_HCENTER);
		add(logoBitmapField);
		
		add(new SeparatorField());

		usernameField = new EditField("", "");
		passwordField = new PasswordEditField("", "");
		add(new LabelField("Usuario:"));
		add(usernameField);
		add(new LabelField(""));
		add(new LabelField("Password:"));
		add(passwordField);

		add(new SeparatorField());

		loginButton = new ButtonField("Entrar", ButtonField.CONSUME_CLICK);
		loginButton.setChangeListener(this);
		exitButton = new ButtonField("Salir", ButtonField.CONSUME_CLICK);
		exitButton.setChangeListener(this);
		HorizontalFieldManager buttonManager = new HorizontalFieldManager(Field.FIELD_RIGHT);
		buttonManager.add(loginButton);
		buttonManager.add(exitButton);
		add(buttonManager);
	}
	
	private void makeLoginRequest() {
		String url = "http://www.ipoki.com/signin.php?user=" + usernameField.getText() + "&pass=" + passwordField.getText();
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
				Dialog.alert("Fallo en la conexión. Motivo: " + message);
			}
		});
	}

	public void requestSucceeded(byte[] result, String contentType) {
		// TODO Auto-generated method stub
		
	}
}
