package com.ipoki.xacoveo.bb;

import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;
import javax.microedition.location.QualifiedCoordinates;

import com.ipoki.xacoveo.bb.screens.LocationScreen;

public class LocationHandler extends Thread implements LocationListener {
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
			QualifiedCoordinates coord = location.getQualifiedCoordinates();
			screen.setLocationData(coord.getLongitude(), coord.getLatitude(), location.getSpeed(), coord.getAltitude());
		} catch (LocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void locationUpdated(LocationProvider arg0, Location arg1) {
		// TODO Auto-generated method stub
		
	}

	public void providerStateChanged(LocationProvider arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
}
