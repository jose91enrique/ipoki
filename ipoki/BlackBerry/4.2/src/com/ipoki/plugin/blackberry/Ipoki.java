/*
 * Created by Javier Cancela
 * Copyright (C) 2007 ipoki.com, All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, If not, see <http://www.gnu.org/licenses/>.*/

// hash of com.ipoki.plugin.blackberry.user
// 0x57faa02a62ef6e45L
// hash of com.ipoki.plugin.blackberry.pass
// 0xe91da859a7ee3581L
// hash of com.ipoki.plugin.blackberry.freq
// 0x90b4a6286eb92c20L
package com.ipoki.plugin.blackberry;

import javax.microedition.location.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.system.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.i18n.*;
import net.rim.device.api.system.CodeModuleManager;
import com.ipoki.plugin.blackberry.resource.*;

public class Ipoki  extends UiApplication implements IpokiResource
{
    IpokiMainScreen _mainScreen;

    PopupScreen _gaugeScreen;
    GaugeField _gauge;
    LabelField _label;
    LocationProvider _locationProvider;    
    String _lastMessageSent = "";
    String _messageToSend = "";

    ConnectionThread _connectionThread = new ConnectionThread();
    ListenThread _listenThread = new ListenThread();

    LabelField _lblStatus;
    LabelField _lblUser;
    LabelField _lblLongitude;
    LabelField _lblLatitude;
    LabelField _lblAltSpeed;
    LabelField _lblCommentSent;
    
    static int _interval = 1; //seconds - this is the period of position query
    static ResourceBundle _resources = ResourceBundle.getBundle(BUNDLE_ID, BUNDLE_NAME);
    static PersistentObject _userStore;
    static PersistentObject _passStore;
    static PersistentObject _freqStore;
    static String _idUser = "";
    static String _comment = "";
    static String _user;
    static String _pass;
    static int _freq;
    static boolean _isConnected = false;
    static String _osVersion = getOSVersion();
    static String _ipokiVersion = ApplicationDescriptor.currentApplicationDescriptor().getVersion();
    static String _ipokiUserAgent = "Ipoki/BlackBerry/" + _ipokiVersion;
    
    // Restore user preferences
    static 
    {
        _userStore = PersistentStore.getPersistentObject(0x57faa02a62ef6e45L);
        _passStore = PersistentStore.getPersistentObject(0xe91da859a7ee3581L);
        _freqStore = PersistentStore.getPersistentObject(0x90b4a6286eb92c20L);
        
        if(_userStore.getContents()!= null && _passStore.getContents()!= null && _freqStore.getContents()!= null)
        {
            _user = (String)_userStore.getContents();
            _pass = (String)_passStore.getContents();
            _freq = Integer.parseInt((String)_freqStore.getContents());
        }
        else
        {
            _user = "";
            _pass = "";
            _freq = 10;
        }
    }
    
    // App entry point, create Ipoki instance and enters event dispatcher
    public static void main(String[] args)
    {
        Ipoki app = new Ipoki();
        app.enterEventDispatcher();
    }
   
    // Launch connection and listen threads here.
    // Ask for user data if not in persistent store 
    public Ipoki()
    {
        _mainScreen = new IpokiMainScreen();
        _label = new LabelField("");
        _mainScreen.add(_label);
        _gaugeScreen = new PopupScreen(new VerticalFieldManager());
        _gauge = new GaugeField("", 0, 9, 0, GaugeField.LABEL_AS_PROGRESS);
        _gaugeScreen.add(_gauge);

        
        //start the helper threads
        _connectionThread.start();
        _listenThread.start();
        
        // Show main screen
        pushScreen(_mainScreen);

        // If persistent store is empty, show setup screen
        if (_user == "")
        {
            SetupScreen setupScreen = new SetupScreen();
            pushScreen(setupScreen);
        }
        
        // Start retrieving location data
        startLocationUpdate();
    }
    
    // Looks for a location provider. Exits app if no provider is found.
    private void startLocationUpdate()
    {
        try 
        {
            _locationProvider = LocationProvider.getInstance(null);
            
            if ( _locationProvider == null ) 
            {
                // We would like to display a dialog box indicating that GPS isn't supported, but because
                // the event-dispatcher thread hasn't been started yet, modal screens cannot be pushed onto
                // the display stack.  So delay this operation until the event-dispatcher thread is running
                // by asking it to invoke the following Runnable object as soon as it can.
                Runnable showGpsUnsupportedDialog = new Runnable() 
                {
                    public void run() 
                    {
                        Dialog.alert("GPS is not supported on this platform, exiting...");
                        System.exit( 1 );
                    }
                };
                invokeLater( showGpsUnsupportedDialog );  // ask event-dispatcher thread to display dialog ASAP
            } 
            else 
            {
                // only a single listener can be associated with a provider, and unsetting it involves the same
                // call but with null, therefore, no need to cache the listener instance
                _locationProvider.setLocationListener(new LocationListenerImpl(), _interval, 1, 1);
            }
        } catch (LocationException le) {
            System.err.println("Failed to instantiate the LocationProvider object, exiting...");
            System.err.println(le); 
            System.exit(0);
        }        
    }

    // This class is called from the location provider
    private class LocationListenerImpl implements LocationListener
    {
    	// New location sent
        public void locationUpdated(LocationProvider provider, Location location)
        {
        	// If valid, we get the data
            if(location.isValid())
            {
                UpdateLocation(location);
            }
        }
  
        // We discard provider state changes
        public void providerStateChanged(LocationProvider provider, int newState)
        {
        }        
    }
    
    /* 
     * We update the interface with the new location 
     * in a different thread, to return this method as soon
     * as possible
     */ 
    private void UpdateLocation(final Location location)
    {
        invokeLater(new Runnable() 
        {
            public void run()
            {
                double longitude = location.getQualifiedCoordinates().getLongitude();
                double latitude = location.getQualifiedCoordinates().getLatitude();
                float altitude = location.getQualifiedCoordinates().getAltitude();
                float speed = location.getSpeed();  
                String altSpeed = String.valueOf(altitude) + " - " + String.valueOf(speed);
                _lblLongitude.setText(String.valueOf(longitude));
                _lblLatitude.setText(String.valueOf(latitude));
                _lblAltSpeed.setText(altSpeed);
            }
        });    
    }
    
    /*
     * Screen to set user preferences. Shown the first time the user launches
     * the app, and when selecting the options menu at the main screen
     */
    private class SetupScreen extends MainScreen
    {
        private LabelField _userLabel;
        private EditField _userEdit;
        private LabelField _passLabel;
        private PasswordEditField _passEdit;
        private LabelField _freqLabel;
        private EditField _freqEdit;
        
        /*
         * We load the previous settings.
         * By now the only settings are user, password and server connection frequency
         */
        public SetupScreen() 
        {    
            _userLabel = new LabelField(Ipoki._resources.getString(LBL_USER), DrawStyle.ELLIPSIS);
            add(_userLabel);
            _userEdit = new EditField("", Ipoki._user, 20, Field.EDITABLE);
            add(_userEdit);
            _passLabel = new LabelField(Ipoki._resources.getString(LBL_PASSWORD), DrawStyle.ELLIPSIS);
            add(_passLabel);
            _passEdit = new PasswordEditField("", Ipoki._pass, 20, Field.EDITABLE);
            add(_passEdit);
            _freqLabel = new LabelField(Ipoki._resources.getString(LBL_FREQ), DrawStyle.ELLIPSIS);
            add(_freqLabel);
            _freqEdit = new EditField("", String.valueOf(Ipoki._freq), 20, Field.EDITABLE | EditField.FILTER_INTEGER);
            add(_freqEdit);
        }
        
        private MenuItem _cancel = new MenuItem(Ipoki._resources, MNU_CANCEL,  300000, 10) {
            public void run()
            {
                 SetupScreen.this.close();
            }
        };  
        
        private MenuItem _save = new MenuItem(Ipoki._resources, MNU_SAVE, 200000, 10) {
            public void run()
            {   
            	// As always, update ui from a different thread
                invokeLater(new Runnable() 
                {
                    public void run()
                    {
                        _lblUser.setText(_userEdit.getText());
                    }
                });    
                
                // Save at persistent store
                saveOptions(_userEdit.getText(), _passEdit.getText(), _freqEdit.getText());
                popScreen(SetupScreen.this);
            }
        };
         
        protected void makeMenu( Menu menu, int instance )
        {
            menu.add(_save);
            menu.add(_cancel);
            
            super.makeMenu(menu, instance);
        }
    }

    /*
     * We write the preferences in the persistent store
     */
    public void saveOptions(String user, String pass, String freq)
    {
        _userStore.setContents(user);
        _userStore.commit();
        _user = user;

        _passStore.setContents(pass);
        _passStore.commit();
        _pass = pass;
        
        _freqStore.setContents(freq);
        _freqStore.commit();
        _freq = Integer.parseInt(freq);
    }
    
    /*
     * Called from IpokiMainScreen to change user preferences
     */
    public void viewOptions()
    {
        SetupScreen setupScreen = new SetupScreen();
        pushScreen(setupScreen);
    }
    
    /*
     * Screen to send a message to the server. 
     */
    private class MessageScreen extends MainScreen
    {
        private LabelField _messageLabel;
        private EditField _messageEdit;
        private LabelField _lastLabel;
        private LabelField _lastMessageLabel;
        
        public MessageScreen(String last) 
        {    
            _messageLabel = new LabelField(Ipoki._resources.getString(LBL_MESSAGE), DrawStyle.ELLIPSIS);
            Font font = _messageLabel.getFont();
            Font newFont = font.derive(Font.BOLD);
            _messageLabel.setFont(newFont);
            add(_messageLabel);
            _messageEdit = new EditField("", "", 144, Field.EDITABLE);
            add(_messageEdit);
            _lastLabel = new LabelField(Ipoki._resources.getString(LBL_LAST_MESSAGE), DrawStyle.ELLIPSIS);
            font = _lastLabel.getFont();
            newFont = font.derive(Font.BOLD);
            _lastLabel.setFont(newFont);
            add(_lastLabel);
            _lastMessageLabel = new LabelField(last, DrawStyle.ELLIPSIS);
            add(_lastMessageLabel);
        }
        
        private MenuItem _cancel = new MenuItem(Ipoki._resources, MNU_CANCEL,  300000, 10) {
            public void run()
            {
                 MessageScreen.this.close();
            }
        };  
        
        private MenuItem _send = new MenuItem(Ipoki._resources, MNU_SEND, 200000, 10) {
            public void run()
            {   
            	/* 
            	 * The connection thread reads _messageToSend when sending the location
            	 * to the server. If not empty, it sends the message with the location,
            	 * and them empties _messageToSend. So we need to synchronize the writing
            	 */
            	synchronized(Ipoki.this)
            	{
            		_messageToSend = _messageEdit.getText();
            	}
                MessageScreen.this.close();
            }
        };
        
        protected void makeMenu( Menu menu, int instance )
        {
            menu.add(_send);
            menu.add(_cancel);
            
            super.makeMenu(menu, instance);
        }
    }

    /*
     * Called from IpokiMainScreen to send a message
     */
    public void sendMessage()
    {
        MessageScreen messageScreen = new MessageScreen(_lastMessageSent);
        pushScreen(messageScreen);
    }
    

    

    
    public void showMap()
    {
    	_listenThread.pause();
        MapScreen mapScreen = new MapScreen(this._lblLatitude.getText(), this._lblLongitude.getText());
        pushScreen(mapScreen);
        _listenThread.go();
    }
    
    public void showAbout()
    {
        Bitmap bitmap = Bitmap.getBitmapResource("ipokito.png");
        Dialog about = new Dialog(Dialog.D_OK, _resources.getString(APP_TITLE), 0, bitmap, 0);
        LabelField text0 = new LabelField("Version: " + _ipokiVersion);
        LabelField text1 = new LabelField("OS Version: " + _osVersion);
        LabelField text2 = new LabelField(_resources.getString(APP_COMPANY));
        about.add(text0);
        about.add(text1);
        about.add(text2);
        pushScreen(about); 
    }
    
    public void connect()
    {
        _connectionThread.signIn(_user, _pass);
    }
    
    public void disconnect()
    {
        if (!_idUser.equals(""))
        {
            pauseListenThread();
            _connectionThread.signout(_idUser);
        }
    }
    
    public void pauseListenThread()
    {
        _listenThread.pause();
        try 
        {
            synchronized(_listenThread)
            {
                //Check the paused condition, incase the notify fires prior to our wait, in which case we may never see that nofity
                while ( !_listenThread.isPaused() )
                {
                    _listenThread.wait();
                }
            }
        } 
        catch (InterruptedException e) 
        {
            System.err.println(e.toString());
        }
    }
    
    private static String getOSVersion()
    {
    	String version = "Unknown";
    	int handle = CodeModuleManager.getModuleHandle( "net_rim_bb_browser_daemon" );
    	if( handle != 0 ){
    	    version = CodeModuleManager.getModuleVersion( handle );
    	}
    	
    	return version;
    }
} 


