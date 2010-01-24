package com.ipoki.xacoveo.javame.gps;

import javax.microedition.location.Coordinates;
import javax.microedition.location.Location;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;
import javax.microedition.location.ProximityListener;
import javax.microedition.location.QualifiedCoordinates;

import com.ipoki.xacoveo.javame.ui.MainForm;

public class XacoveoData implements LocationListener, ProximityListener
{
    private ProviderStatusListener statusListener = null;
    private boolean firstLocationUpdate = false;
    private MainForm mainForm = null;

    public XacoveoData(ProviderStatusListener listener)
    {
        statusListener = listener;

        ConfigurationProvider config = ConfigurationProvider.getInstance();

        // 1. Register LocationListener
        LocationProvider provider = config.getSelectedProvider();
        if (provider != null)
        {
            int interval = -1; // default interval of this provider
            int timeout = 0; // parameter has no effect.
            int maxage = 0; // parameter has no effect.

            provider.setLocationListener(this, interval, timeout, maxage);
        }
    }

    public void setMainForm(MainForm mf)
    {
        mainForm = mf;
    }

    public void locationUpdated(LocationProvider provider,
            final Location location)
    {
        // First location update arrived, so we may show the UI (TouristUI)
        if (!firstLocationUpdate)
        {
            firstLocationUpdate = true;
            statusListener.firstLocationUpdateEvent();
        }

        if (mainForm != null)
        {
            new Thread()
            {
                public void run()
                {
                    if (location != null && location.isValid())
                    {
                        QualifiedCoordinates coord = location.getQualifiedCoordinates();
                        mainForm.setLocation(coord.getLatitude(), coord.getLongitude(), true);
                    }
                    else
                    {
                        mainForm.setLocation(0, 0, false);
                    }
                }
            }.start();
        }
    }

    public void providerStateChanged(LocationProvider provider,
            final int newState)
    {
        if (mainForm != null)
        {
            new Thread()
            {
                public void run()
                {
                    switch (newState) {
                        case LocationProvider.AVAILABLE:
                        	mainForm.setLocation(0, 0, false);
                            break;
                        case LocationProvider.OUT_OF_SERVICE:
                        	mainForm.setLocation(0, 0, false);
                            break;
                        case LocationProvider.TEMPORARILY_UNAVAILABLE:
                        	mainForm.setLocation(0, 0, false);
                            break;
                        default:
                        	mainForm.setLocation(0, 0, false);
                            break;
                    }
                }
            }.start();
        }
    }

    public void proximityEvent(Coordinates coordinates, Location location) {
    }

    public void monitoringStateChanged(boolean isMonitoringActive) {
    }
}
