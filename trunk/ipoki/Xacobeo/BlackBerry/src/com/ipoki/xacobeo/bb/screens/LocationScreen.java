package com.ipoki.xacobeo.bb.screens;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;

public class LocationScreen extends MainScreen implements FieldChangeListener {
	LabelField statusLabel;
	EditField latitudeField;
	EditField longitudeField;
	EditField speedField;
	EditField heightField;
	LabelField privacyLabel;
	LabelField recLabel;
	ButtonField connectButton;

	public LocationScreen() {
		statusLabel = new LabelField("Disconnected");
		add(statusLabel);
		
		add(new SeparatorField());
		
		latitudeField = new EditField("Latitude: ", "");
		longitudeField = new EditField("Longitude", "");
		add(latitudeField);
		add(longitudeField);
		
		add(new SeparatorField());
		
		speedField = new EditField("Speed: ", "");
		heightField = new EditField("Height: ", "");
		add(speedField);
		add(heightField);

		add(new SeparatorField());
		
		privacyLabel = new LabelField("Posición pública");
		recLabel = new LabelField("Posición pública");
		add(privacyLabel);
		add(recLabel);

		connectButton = new ButtonField("Conectar", ButtonField.CONSUME_CLICK);
		connectButton.setChangeListener(this);
	}

	public void fieldChanged(Field field, int context) {
		// TODO Auto-generated method stub
		
	}
}
