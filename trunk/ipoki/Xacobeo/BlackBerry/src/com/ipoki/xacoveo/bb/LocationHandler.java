package com.ipoki.xacoveo.bb;

import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;
import javax.microedition.location.QualifiedCoordinates;

import com.ipoki.xacoveo.bb.screens.LocationScreen;

public class LocationHandler extends Thread implements LocationListener, HttpRequester {
	private LocationScreen screen;

	public LocationHandler(LocationScreen screen) {
		this.screen = screen;
	}
	
	private Criteria setGPSCriteria() {
		Criteria criteria = new Criteria(); 
		criteria.setHorizontalAccuracy(50); 
		criteria.setVerticalAccuracy(50); 
		criteria.setCostAllowed(true); 
		criteria.setPreferredPowerConsumption(Criteria.POWER_USAGE_HIGH);
		
		return criteria;
	}
	
	public void run() {
		Criteria criteria = setGPSCriteria();
		
		screen.gettingLocation();
		try {
			LocationProvider provider = LocationProvider.getInstance(criteria);
			Location location = provider.getLocation(-1);
			locationUpdated(provider, location);
			provider.setLocationListener(this, 30, 10, -1);
		} catch (LocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void locationUpdated(LocationProvider provider, Location location) {
		if (location.isValid()) {
			QualifiedCoordinates coord = location.getQualifiedCoordinates();
			screen.setLocationData(coord.getLongitude(), coord.getLatitude(), location.getSpeed(), coord.getAltitude());
			if (EPeregrinoSettings.Connected) {
				long updateFreq = Long.parseLong(EPeregrinoSettings.UpdateFreq) * 1000;
				if (System.currentTimeMillis() - EPeregrinoSettings.Lapse > updateFreq) {
					EPeregrinoSettings.Lapse = System.currentTimeMillis();
					String url = "http://www.ipoki.com/ear.php?iduser=" + EPeregrinoSettings.UserKey + 
						"&lat=" + String.valueOf(coord.getLatitude()) +  "&lon=" + String.valueOf(coord.getLongitude())+ 
						"&h=" + String.valueOf(coord.getAltitude()) + "&speed=" + String.valueOf(location.getSpeed());
					HttpRequestHelper helper = new HttpRequestHelper(url, this);
					helper.start();
				}
			}
		}
	}

	public void providerStateChanged(LocationProvider arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	public void requestFailed(String message) {
		// TODO Auto-generated method stub
		
	}

	public void requestSucceeded(byte[] result, String contentType) {
		// TODO Auto-generated method stub
		
	}
}
