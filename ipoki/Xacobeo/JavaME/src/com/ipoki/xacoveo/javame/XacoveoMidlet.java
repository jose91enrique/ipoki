package com.ipoki.xacoveo.javame;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Gauge;
import javax.microedition.midlet.MIDlet;

import com.ipoki.xacoveo.javame.gps.ConfigurationProvider;
import com.ipoki.xacoveo.javame.gps.ProviderStatusListener;
import com.ipoki.xacoveo.javame.gps.XacoveoData;
import com.ipoki.xacoveo.javame.ui.MainForm;
import com.ipoki.xacoveo.javame.utils.MIDletExiter;
import com.ipoki.xacoveo.javame.utils.State;

public class XacoveoMidlet extends MIDlet implements ProviderStatusListener, MIDletExiter
{
    private Object mutex = new Object();
    private XacoveoData data = null;

	public XacoveoMidlet() throws Exception
	{
		State.display = Display.getDisplay(this);
	}

	public void startApp()
	{
		try
		{
			// Initialize any exisitng security info
			// that might be stored in RMS stores.
			State.getInstance().loadConfiguration();
	    }
		catch (Exception e)
	    {
	      e.printStackTrace();
	    }

		nextDisplay();
	}

	public void exit()
	{
		destroyApp(false);
	    notifyDestroyed();
	}

	public void pauseApp()
	{
	}

	public void destroyApp(boolean unconditional)
	{
		try
		{
			State.recordStore.closeRecordStore();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void nextDisplay()
	{
        if (ConfigurationProvider.isLocationApiSupported())
        {
            ConfigurationProvider.getInstance().autoSearch(this);
        }
        else
        {
            Alert alert = new Alert("Error",
                    "Location API not supported!", null,
                    AlertType.ERROR);
            State.display.setCurrent(alert);
        }

		// MainFormUI need to access life cycle and MIDlet methods
		MainForm.m = this;
	}

    public void providerSelectedEvent()
    {
        // Attempt to acquire the mutex
        synchronized (mutex)
        {
            // Start scanning location updates. Also set the TouristData
            // reference data.
            Gauge indicator = new Gauge(null, false, 50, 1);
            indicator.setValue(Gauge.CONTINUOUS_RUNNING);

            Alert alert = new Alert("Information",
                    "Please wait, looking for location data....", null,
                    AlertType.INFO);
            alert.setIndicator(indicator);

            State.display.setCurrent(alert);

            // Inform the user that MIDlet is looking for location data.
            data = new XacoveoData((ProviderStatusListener) this);
        }
    }

    public void firstLocationUpdateEvent()
    {
        // Attempt to acquire the mutex
        synchronized (mutex)
        {
        	MainForm mf = new MainForm();
        	data.setMainForm(mf);
        	State.display.setCurrent(mf);
        }
    }

}
