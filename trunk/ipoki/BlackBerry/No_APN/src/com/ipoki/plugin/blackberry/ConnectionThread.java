/*
 * ConnectionThread.java
 *
 * © <your company here>, 2003-2007
 * Confidential and proprietary.
 */

package com.ipoki.plugin.blackberry;

import com.ipoki.plugin.blackberry.resource.*;
import javax.microedition.io.*;
import net.rim.device.api.i18n.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.Dialog;

public class ConnectionThread extends Thread implements IpokiResource
{
    private static ResourceBundle _resources = ResourceBundle.getBundle(BUNDLE_ID, BUNDLE_NAME);
    private static final int TIMEOUT = 500;
    private static final int RETRY_TIME = 100;
    private String _theUrl;
    private static final String SIGNIN = "http://www.ipoki.com/signin.php";
    private static final String EAR = "http://www.ipoki.com/ear.php";
    private static final String SIGNOUT = "http://www.ipoki.com/signout.php";
    private static final String READPOS = "http://www.ipoki.com/readposition.php";
    private static final String MYFRIENDS = "http://www.ipoki.com/myfriends.php";
    private static final int SIGNIN_S = 0;
    private static final int EAR_S = 1;
    private static final int SIGNOUT_S = 2;
    private static final int READPOS_S = 3;
    private static final int MYFRIENDS_S = 4;
    private int _urlType = 0;
    private volatile boolean _start = false;
    private volatile boolean _stop = false; 
    private final String ipokiUserAgent = Ipoki._ipokiUserAgent;
    
    private Ipoki _app;
    
    public ConnectionThread()
    {
        _app = (Ipoki)UiApplication.getUiApplication();
    }
        
    public synchronized String getUrl()
    {
        return _theUrl;
    }
    
    public void signIn(String user, String pass)
    {
        if (_start)
        {
            setAlert(_resources.getString(LBL_ALERT_REQUESTINPROGRESS));
        }        
        synchronized(this)
        {
            if (_start)
            {
                setAlert(_resources.getString(LBL_ALERT_REQUESTINPROGRESS));                
            }
            else
            {
                // setStatus(Ipoki._resources.getString(LBL_CONNECTING));
                _theUrl = SIGNIN + "?user=" + user + "&pass=" + pass;
                _urlType = SIGNIN_S;
                _start = true;
           }
        }
    }
    

    public void setStatus(String status)
    {
        int passInd = status.indexOf("&pass=");
        if (passInd != -1) {
            int passNext = status.indexOf("&", passInd + 6);
            status = 
                status.substring(0, passInd) +
                "&pass=*" +
                (passNext != -1 ? status.substring(passNext) : "");
        }
        final String secureStatus = status;
        _app.invokeLater(new Runnable() 
        {
            public void run()
            {
                _app._lblStatus.setText(secureStatus);
            }
        });
    }
	
    public void setAlert(final String alert)
    {
        _app.invokeLater(new Runnable() 
        {
            public void run()
            {
                Dialog.alert(alert);
            }
        });
    }
	
    public void ear(String idUser, String lat, String lon, String comment)
    {
        try
        {
            if ( _start )
            {
                sleep(RETRY_TIME);
            }        
            synchronized(this)
            {
                if (_start)
                {
                    sleep(RETRY_TIME);
                }
                else
                {
                    _theUrl = EAR + "?iduser=" + idUser + "&lat=" + lat + "&lon=" + lon;
                    synchronized(_app)
                    {
                        if (_app._messageToSend.length() > 0)
                        {
                            _theUrl += "&comment=" + _app._messageToSend.replace(' ', '+');
                            _app._lastMessageSent = _app._messageToSend;
                            _app._messageToSend = "";
                        }
                    }
                    _urlType = EAR_S;
                    _start = true;
                }
            }
        }
        catch(InterruptedException e)
        {
            System.err.println(e.toString());
            setStatus("INTERRUPTED ear " + getUrl());
        }
    }
    
    public void signout(String idUser)
    {
        try
        {
            if ( _start )
            {
                sleep(RETRY_TIME);
            }        
            synchronized(this)
            {
                if (_start)
                {
                    sleep(RETRY_TIME);
                }
                else
                {
					_theUrl = SIGNOUT + "?iduser=" + idUser;
                    _urlType = SIGNOUT_S;
                    _start = true;
                }
            }
        }
        catch(InterruptedException e)
        {
            System.err.println(e.toString());
            setStatus("INTERRUPTED signout " + getUrl());
        }
    }
    
    public void run()
    {
        for (;;)
        {
            while ( !_start && !_stop)
            {
                try
                {
                    sleep(TIMEOUT);
                }
                catch(InterruptedException e)
                {
                    System.err.println(e.toString());
                    setStatus("INTERRUPTED run " + getUrl());
                }
            } 
            
            if (_stop)
            {
                return;
            }
            
            synchronized(this)
            {
                StreamConnection s = null;
                java.io.InputStream input = null;
                try
                {
                    setStatus(getUrl());
                    s = (StreamConnection)Connector.open(getUrl(), Connector.READ, true);
                    HttpConnection httpConn = (HttpConnection)s;
                    httpConn.setRequestProperty("User-Agent", ipokiUserAgent);
                    
                    int status = httpConn.getResponseCode();
                    setStatus(status + " " + getUrl());
                    if (status == HttpConnection.HTTP_OK)
                    {
						input = s.openInputStream();
						
                        byte[] data = new byte[256];
                        int len = 0;
                        int size = 0;
                        StringBuffer raw = new StringBuffer();
                        while ( -1 != (len = input.read(data)) )
                        {
                            raw.append(new String(data, 0, len));
                            size += len;
                        }
                        input.close();
                        input = null;
                        s.close();
                        s = null;
						java.util.Vector messages = parseMessage(raw.toString());
                        processMessages(messages);
                    }
                }
                catch (java.io.IOException e) 
                {
                    System.err.println(e.toString());
                    setStatus(e.toString());
				}
                finally
                {
                    try {
                        if (input != null) {
                            input.close();
                            input = null;
                        }
                    } catch (java.io.IOException e) {
                        // pass
                    }
                    try {
                        if (s != null) {
                            s.close();
                            s = null;
                        }
                    } catch (java.io.IOException e) {
                        // pass
                    }
                }

                _start = false;
            } // synchronized
        } // for
    } // run

    private void processMessages(final java.util.Vector messages)
    {
        if (messages.size() < 1)
            return;
        
        String typeMessage = (String)messages.elementAt(0);
        setStatus(typeMessage);
        if (typeMessage.equals("CODIGO"))
        {
            String message = (String)messages.elementAt(1);
            if (message.equals("ERROR") )
            {
				setAlert(Ipoki._resources.getString(LBL_LOGIN_ERROR));
				return;
            }
                
            Ipoki._idUser = (String)messages.elementAt(1);
            _app._listenThread.go();
            setStatus(Ipoki._resources.getString(LBL_CONNECTED));
            Ipoki._isConnected = true;
		}
        else if(typeMessage.equals("COMMENT"))
        {
            if (messages.size() < 3)
                return;
                
			setStatus((String)messages.elementAt(1) + ": " + (String)messages.elementAt(2));
      }
        
        if (_urlType == SIGNOUT_S && typeMessage.equals("OK"))
        {
            //_app._statusThread.pause();
            setStatus(Ipoki._resources.getString(LBL_DISCONNECTED));
            Ipoki._isConnected = false;
            //_app.popScreen(_app._gaugeScreen);
        }
    }

    public void stop()
    { 
        _stop = true;
    }
    
    private java.util.Vector parseMessage(String mensaje)
    {
        // Temporalmente almacenaremos los mensajes en un vector 
        // (ya que nos abemos el número de elementos)
        java.util.Vector mensajes = new java.util.Vector();
        
        // Mientras haya un $$$ en la cadena
        while (mensaje.indexOf("$$$") != -1)
        {
            // Añadimos lo que hay hasta el $$$ al vector
            mensajes.addElement(mensaje.substring(0, mensaje.indexOf("$$$")));
            // Eliminamos lo ya añadido (incluído el $$$)
            mensaje = mensaje.substring(mensaje.indexOf("$$$") + 3);
        }
        
        if (mensaje.length() > 0)
            mensajes.addElement(mensaje);
            
        return mensajes;
    }

} // class
