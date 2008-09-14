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
        return _theUrl + ";deviceside=true";
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
                StreamConnection s = null;
                String url = getUrl();
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
                            DataInputStream dis = s.openDataInputStream();
                            Document d = doc.parse(dis);
                            Element el = d.getDocumentElement();
                            _theUrl = el.getFirstChild().getNodeValue();
                            url = getUrl();
                            dis.close();
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
                    
                    s = (StreamConnection)Connector.open(url);
                    httpConn = (HttpConnection)s;
                    httpConn.setRequestProperty("User-Agent", "Ipoki/BlackBerry/0.1");
                    
                    status = httpConn.getResponseCode();
                    if (status == HttpConnection.HTTP_OK)
                    {
                        java.io.InputStream input = s.openInputStream();
                        byte[] data = new byte[1];
                        final ByteVector bv = new ByteVector();
                        while ( -1 != input.read(data) )
                        {
                            bv.addElement(data[0]);
                        }
                        try
                        {
                        	_mapScreen._app.invokeLater(new Runnable() 
			                {
			                    public void run()
			                    {
			                        _mapScreen.mapField.setBitmap(Bitmap.createBitmapFromPNG(bv.getArray(), 0, -1));
			                        _mapScreen.invalidate();
			                    }
			                });    
                        }
                        catch(Exception e)
                        {
                            System.err.println(e.toString());
                        }
                        input.close();
                    }
                    s.close();                     
                }
                catch (java.io.IOException e) 
                {
                    System.err.println(e.toString());
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
