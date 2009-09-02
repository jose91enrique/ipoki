package com.ipoki.plugin.blackberry;

import java.io.DataInputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.StreamConnection;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.util.ByteVector;
import net.rim.device.api.xml.parsers.DocumentBuilder;
import net.rim.device.api.xml.parsers.DocumentBuilderFactory;
import net.rim.device.api.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class MapConnectionThread extends Thread {
    private String _theUrl;
    private static int TIMEOUT = 500;
    private volatile boolean _start = false;
    private volatile boolean _stop = false; 
    private MapScreen _mapScreen = null;
    private final String ipokiUserAgent = Ipoki._ipokiUserAgent;
    
    public MapConnectionThread(MapScreen ms)
    {
    	_mapScreen = ms;
    }

    private String getUrl()
    {
        return _theUrl;
    }
    
    public void showMap(String url)
    {
        synchronized(this)
        {
            if (!_start)
            {
                _start = true;
                _theUrl = url;
            }
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
                }
            } 
            
            if (_stop)
            {
                return;
            }
            
            synchronized(this)
            {
                String url = null;
				StreamConnection s = null;
				DataInputStream dis = null;
				java.io.InputStream input = null;
				try
                {
                    s = (StreamConnection)Connector.open(getUrl(), Connector.READ, true);
                    HttpConnection httpConn = (HttpConnection)s;
                    httpConn.setRequestProperty("User-Agent", ipokiUserAgent);
                    
                    int status = httpConn.getResponseCode();
                    if (status == HttpConnection.HTTP_OK)
                    {
                        try
                        {
                            DocumentBuilder doc = DocumentBuilderFactory.newInstance().newDocumentBuilder(); 
							dis = s.openDataInputStream();
                            Document d = doc.parse(dis);
							dis.close();
							dis = null;
							Element el = d.getDocumentElement();
                            _theUrl = el.getFirstChild().getNodeValue();
                            url = getUrl();
                        }
                        catch(SAXException e)
                        {
                            System.err.println(e.toString());
                        }
                        catch(ParserConfigurationException e)
                        {
                            System.err.println(e.toString());
                        }
                    }
					s.close();
					s = null;
					if (url != null) { 
						s = (StreamConnection)Connector.open(url);
						httpConn = (HttpConnection)s;
						httpConn.setRequestProperty("User-Agent", ipokiUserAgent);                        
						status = httpConn.getResponseCode();
						if (status == HttpConnection.HTTP_OK)
                        {
                            bv.addElement(data[0]);
                            input = s.openInputStream();
                            byte[] data = new byte[1];
                            ByteVector bv = new ByteVector();
                            while ( -1 != input.read(data) )
                            {
                                bv.addElement(data[0]);
                            }
                            final byte[] png = bv.getArray();
                            input.close();
                            input = null;
                            s.close();
                            s = null;
                            try
                            {
                                _mapScreen._app.invokeLater(new Runnable() 
                                {
                                    public void run()
                                    {
                                        _mapScreen.mapField.setBitmap(Bitmap.createBitmapFromPNG(png, 0, -1));
                                    }
                                });    
                            }
                            catch(Exception e)
                            {
                                System.err.println(e.toString());
                            }
						}
                    }
                }
                catch (java.io.IOException e) 
                {
                    System.err.println(e.toString());
                }
                finally
                {
                    try {
                        if (dis != null) {
                            dis.close();
                            dis = null;
                        }
                    } catch (java.io.IOException e) {
                        // pass
                    }
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

    public void stop()
    { 
        _stop = true;
    }
}
