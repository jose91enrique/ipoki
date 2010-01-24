package com.ipoki.xacoveo.javame.gps;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.location.Criteria;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationProvider;

import com.ipoki.xacoveo.javame.utils.State;

public class ConfigurationProvider {
   private static ConfigurationProvider INSTANCE = null;
    private LocationProvider provider = null;

        public static ConfigurationProvider getInstance()
        {
            if (INSTANCE == null)
            {
                // Enable use of this class when Location API is supported.
                if (isLocationApiSupported())
                {
                    INSTANCE = new ConfigurationProvider();
                }
                else
                {
                    INSTANCE = null;
                }
            }

            return INSTANCE;
        }

    public void autoSearch(ProviderStatusListener listener)
    {
        try
        {
            Criteria criteria = new Criteria();
            criteria.setCostAllowed(false);

            provider = LocationProvider.getInstance(criteria);
            if (provider != null)
            {
                // Location provider found, send a selection event.
                listener.providerSelectedEvent();
                return;
            }
            else
            {
                Alert alert = new Alert("Error", "GPS unavailable", null, AlertType.ERROR);
                alert.setTimeout(Alert.FOREVER);
                State.display.setCurrent(alert);
            }
        }
        catch (LocationException le)
        {
            Alert alert = new Alert("Error", "Error looking for providers", null, AlertType.ERROR);
            alert.setTimeout(Alert.FOREVER);
            State.display.setCurrent(alert);
        }
    }

    public LocationProvider getSelectedProvider()
    {
        return provider;
    }

        public static boolean isLocationApiSupported()
    {
        String version = System.getProperty("microedition.location.version");
        return (version != null && !version.equals("")) ? true : false;
    }
}
