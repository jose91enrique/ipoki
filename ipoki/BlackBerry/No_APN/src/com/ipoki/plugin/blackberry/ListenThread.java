/*
 * StatusThread.java
 *
 * � <your company here>, 2003-2007
 * Confidential and proprietary.
 */


package com.ipoki.plugin.blackberry;

import javax.microedition.io.*;

import net.rim.device.api.i18n.*;
import com.ipoki.plugin.blackberry.resource.*;
import net.rim.device.api.ui.*;

/*
* Thread can be running or stopped.
* While running, it can be paused.
*/
public class ListenThread extends Thread implements IpokiResource
{
	private static ResourceBundle _resources = ResourceBundle.getBundle(BUNDLE_ID, BUNDLE_NAME);
    private static final int THREAD_TIMEOUT = 500;

    private volatile boolean _stop = false;
    private volatile boolean _running = false;
    private volatile boolean _isPaused = false;

    Ipoki _app;

    public ListenThread()
    {
        _app = (Ipoki)UiApplication.getUiApplication();
    }
    // Resume execution after pause
    public void go()
    {
        _running = true;
    }
    
    /*
    * _running = false is the flag that tells the thread to pause.
    * When the thread is actually pause, we signal it with _isPaused = true,
    */
    public void pause()
    {
        _running = false;
    }
    
    public boolean isPaused()
    {
        return _isPaused;
    }
        
    // Ends thread
    public void stop()
    {
        _stop = true;
    }
    
    public void run()
    {
        // main loop
        for(;;)
        {
            // Thread starts paused, waiting for the go()
            while (!_stop && !_running)
            {
                try
                {
                    sleep(THREAD_TIMEOUT);
                }
                catch ( InterruptedException e) 
                {
                    System.err.println(e.toString());
                }
            }
            
            // stop() was called
            if (_stop)
            {
                return;
            }
            
            // Loop until stopped or paused
            for(;;)
            {
                if (_stop)
                {
                    return;
                }
                
                // pause() was called
                if (!_running)
                {
                    _isPaused = true;
                    synchronized(this)
                    {
                        // Wake up waiting thread
                        this.notify();
                    }
                    break;
                }
                String lat = _app._lblLatitude.getText();
                String lon = _app._lblLongitude.getText();
                String noloc = _resources.getString(LBL_NOLOC);
                if (!noloc.equals(lat) && !noloc.equals(lat)) {
                    _app._connectionThread.ear(Ipoki._idUser, lat, lon, "");
                }
				
				
                try 
                {
                    int freqMilsec = Ipoki._freq * 1000;
                    Thread.sleep(freqMilsec); //wait for a bit
                } catch (InterruptedException e) {
                    System.err.println(e.toString());
                }
            }
        }
    }
}
