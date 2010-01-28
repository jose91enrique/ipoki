/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ipoki.xacoveo.javame;

import com.ipoki.xacoveo.javame.iu.LocationScreen;
import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;
import javax.microedition.location.QualifiedCoordinates;

/**
 *
 * @author Xavi
 */
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
            if (XacoveoSettings.Connected) {
                long updateFreq = Long.parseLong(XacoveoSettings.UpdateFreq) * 1000;
                if (System.currentTimeMillis() - XacoveoSettings.Lapse > updateFreq) {
                    XacoveoSettings.Lapse = System.currentTimeMillis();
                    String url = "http://www.ipoki.com/ear.php?iduser=" + XacoveoSettings.UserKey +
                            "&lat=" + String.valueOf(coord.getLatitude()) +  "&lon=" + String.valueOf(coord.getLongitude())+
                            "&h=" + String.valueOf(coord.getAltitude()) + "&speed=" + String.valueOf(location.getSpeed());
                    HttpRequestHelper helper = new HttpRequestHelper(url, this);
                    helper.start();
                }
            }
        }
    }

    public void providerStateChanged(LocationProvider arg0, int arg1) {
    }

    public void requestFailed(String message) {
    }

    public void requestSucceeded(byte[] result, String contentType) {
    }
}
