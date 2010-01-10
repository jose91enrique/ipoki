package com.ipoki.xacoveo.bb.screens;

import net.rim.blackberry.api.invoke.Invoke;
import net.rim.blackberry.api.invoke.MapsArguments;
import net.rim.blackberry.api.maps.MapView;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;

import com.ipoki.xacoveo.bb.EPeregrinoSettings;
import com.ipoki.xacoveo.bb.LocationHandler;

public class LocationScreen extends MainScreen implements FieldChangeListener {
	LabelField statusLabel;
	EditField latitudeField;
	EditField longitudeField;
	EditField speedField;
	EditField heightField;
	LabelField privacyLabel;
	LabelField recLabel;
	ButtonField connectButton;
	double longitude;
	double latitude;

	public LocationScreen() {
		statusLabel = new LabelField("Disconnected");
		add(statusLabel);
		
		add(new SeparatorField());
		
		latitudeField = new EditField("Latitude: ", "");
		latitudeField.setEditable(false);
		longitudeField = new EditField("Longitude: ", "");
		longitudeField.setEditable(false);
		add(latitudeField);
		add(longitudeField);
		
		add(new SeparatorField());
		
		speedField = new EditField("Speed: ", "");
		speedField.setEditable(false);
		heightField = new EditField("Height: ", "");
		heightField.setEditable(false);
		add(speedField);
		add(heightField);

		add(new SeparatorField());
		
		privacyLabel = new LabelField("Posición pública");
		recLabel = new LabelField("Guardando ruta");
		add(privacyLabel);
		add(recLabel);

		connectButton = new ButtonField("Conectar", ButtonField.CONSUME_CLICK);
		connectButton.setChangeListener(this);
		add(connectButton);
		
		LocationHandler handler = new LocationHandler(this);
		handler.start();
	}

	public void gettingLocation() {
		synchronized(UiApplication.getEventLock()) {
			latitudeField.setText("Obteniendo posición...");
			longitudeField.setText("Obteniendo posición...");
		}
	}
	
	public void setLocationData(double longitude, double latitude, float speed, float height) {
		synchronized(UiApplication.getEventLock()) {
			this.longitude = longitude;
			this.latitude = latitude;
			latitudeField.setText(Double.toString(latitude));
			longitudeField.setText(Double.toString(longitude));
			speedField.setText(Float.toString(speed));
			heightField.setText(Float.toString(height));
		}
	}
	
	protected void makeMenu(Menu menu, int instance) {
		super.makeMenu(menu, instance);
		menu.add(new MenuItem("Ver mapa",10, 10) {
			public void run() {
				MapView mv = new MapView();
				mv.setLatitude(100000 * (int) LocationScreen.this.latitude);
				mv.setLongitude(100000 * (int) LocationScreen.this.longitude);
				mv.setZoom(3);
				MapsArguments args = new MapsArguments(mv);
				Invoke.invokeApplication(Invoke.APP_TYPE_MAPS, args);
			}
		});
	}
	
	public void fieldChanged(Field field, int context) {
		if (field == connectButton) {
			EPeregrinoSettings.Lapse = 0;
			if (EPeregrinoSettings.Connected) {
				EPeregrinoSettings.Connected = false;
				statusLabel.setText("Desconectado");
				connectButton.setLabel("Conectar");
			}
			else {
				EPeregrinoSettings.Connected = true;
				statusLabel.setText("Conectado");
				connectButton.setLabel("Desconectar");
			}
		}
	}
}
