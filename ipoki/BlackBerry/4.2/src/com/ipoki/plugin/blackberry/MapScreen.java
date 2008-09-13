package com.ipoki.plugin.blackberry;

import com.ipoki.plugin.blackberry.resource.IpokiResource;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;

public class MapScreen extends MainScreen implements IpokiResource
{
    private int _zoom = 3;
    private String _latitude;
    private String _longitude;
    private final String _width;
    private final String _height;
    public BitmapField mapField;
    public Ipoki _app = (Ipoki)UiApplication.getUiApplication(); 
    ResourceBundle _resources = ResourceBundle.getBundle(BUNDLE_ID, BUNDLE_NAME);
    MapConnectionThread _mapConnectionThread = new MapConnectionThread(this);

    public MapScreen(String latitude, String longitude) 
    {
        _latitude = latitude.substring(0, 6);
        _longitude = longitude.substring(0, 6);
        
        _width = Integer.toString(Graphics.getScreenWidth());
        _height = Integer.toString(Graphics.getScreenHeight());
        mapField = new BitmapField();
        add(mapField);

    	_mapConnectionThread.start();
        showMap();
    }
    
    private void showMap()
    {
        String url = getUrl(_latitude, _longitude, _width, _height, Integer.toString(_zoom));
    	_mapConnectionThread.showMap(url);
    }
    
    private String getUrl(String latitude, String longitude, String width, String height, String zoom)
    {
        return "http://local.yahooapis.com/MapsService/V1/mapImage?appid=08REOqLV34HybSt1yvZRY7DcL5hbUGyaFpRP.hsVJve.01qb6KWXP78TmIPi_w--" + 
                "&latitude=" + latitude + 
                "&longitude=" + longitude + 
                "&image_height=" + height + 
                "&image_width=" + _width + 
                "&zoom=" + zoom;
    }
    
    private MenuItem _zoomIn = new MenuItem(Ipoki._resources, MNU_ZOOMIN, 200000, 10) {
        public void run()
        {   
            if (_zoom > 1)
            {
                _zoom --;
                showMap();
            }
        }
    };
    
    private MenuItem _zoomOut = new MenuItem(Ipoki._resources, MNU_ZOOMOUT, 200000, 10) {
        public void run()
        {
            if (_zoom < 12)
            {
                _zoom ++;
                showMap();
            }   
        }
    };
    
    MenuItem invokeClose = new MenuItem(_resources.getString(MNU_CLOSE),0,0)
    {
        public void run()
        {
        	_mapConnectionThread.stop();
            onClose();
        }        
    };    
    
    protected void makeMenu( Menu menu, int instance )
    {
        menu.add(_zoomIn);
        menu.add(_zoomOut);
        menu.add(invokeClose);

        //super.makeMenu(menu, instance);
    }

}