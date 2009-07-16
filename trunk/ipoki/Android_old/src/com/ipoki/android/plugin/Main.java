package com.ipoki.android.plugin;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import org.apache.http.util.ByteArrayBuffer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentReceiver;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity {
    /** Called when the activity is first created. */
    private static final int ACTIVITY_SETTINGS=0;
    private static final int ACTIVITY_COMMENTS=1;
    private static final String NEW_LOCATION = "ipoki.intent.action.LOCATION_CHANGED";
    
    protected final IntentFilter intentFilter = new IntentFilter(Main.NEW_LOCATION); 
    private LocationManager locMan = null;
    private LocationReceiver locRec = null;
    private String connectionState;
    private String sessionId;
    private boolean isConnected = false;
    private boolean stop = false;
    private String latitude;
    private String longitude;
    private String commentToSend = "";
    
    private Handler mHandler = new Handler();
    
    private Runnable disconnectRunnable = new Runnable() {
		public void run() {
	          try {
	               URL myURL = new URL(getString(R.string.ipoki_url) + 
	                         "/signout.php?iduser=" + sessionId);
	               HttpURLConnection httpCon = (HttpURLConnection)myURL.openConnection();
	               httpCon.setRequestMethod("GET");
	               httpCon.connect();
	               httpCon.getInputStream();
	               httpCon.disconnect();
	               isConnected = false;
	          } catch (Exception e) {
	                Toast.makeText(Main.this, "HttpConnection error", Toast.LENGTH_SHORT).show();
	          }
		}
	};
    
    private Runnable connectRunnable = new Runnable() {
		public void run() {
	          String response = null;
	          try {
	               URL myURL = new URL(getString(R.string.ipoki_url) + 
	                         "/signin.php?user=" + Settings.username + 
	                         "&pass=" + Settings.password);
	               HttpURLConnection httpCon = (HttpURLConnection)myURL.openConnection();
	               httpCon.setRequestMethod("GET");
	               httpCon.connect();
	               InputStream is = httpCon.getInputStream();
	               BufferedInputStream bis = new BufferedInputStream(is);
	               ByteArrayBuffer buffer = new ByteArrayBuffer(50);
	               int current = 0;
	               while((current = bis.read()) != -1){
	            	   buffer.append((byte)current);
	               }

	               response = new String(buffer.toByteArray());
	               httpCon.disconnect();
	               Vector<String> params = parseMessage(response);
	               if (params.elementAt(0).equals("CODIGO")) {
	            	   isConnected = true;
	            	   sessionId = params.elementAt(1);
	            	   ((TextView)findViewById(R.id.state_tv)).setText("Connected");
	            	   mHandler.post(positionRunnable);
	               }
	          } catch (Exception e) {
	                Toast.makeText(Main.this, "HttpConnection error", Toast.LENGTH_SHORT).show();
	          }
		}
	};
	
    private Runnable positionRunnable = new Runnable() {
		public void run() {
	          try {
	        	   String url;
	        	   synchronized(this) {
	        		   if (commentToSend.compareTo("") != 0) {
	        			   url = getString(R.string.ipoki_url) + 
	                         "/ear.php?iduser=" + sessionId + 
	                         "&lat=" + latitude + 
	                         "&lon=" + longitude + "&comment=" + commentToSend;
	        			   commentToSend = "";
	        		   } 
	        		   else {
	        			   url = getString(R.string.ipoki_url) + 
	                         "/ear.php?iduser=" + sessionId + 
	                         "&lat=" + latitude + 
	                         "&lon=" + longitude;
	        		   }
	        	   }
	               URL myURL = new URL(url);
	               HttpURLConnection httpCon = (HttpURLConnection)myURL.openConnection();
	               httpCon.setRequestMethod("GET");
	               httpCon.connect();
	               InputStream is = httpCon.getInputStream();
	               BufferedInputStream bis = new BufferedInputStream(is);
	               ByteArrayBuffer buffer = new ByteArrayBuffer(50);
	               int current = 0;
	               while((current = bis.read()) != -1){
	            	   buffer.append((byte)current);
	               }

	               String response = new String(buffer.toByteArray());
	               httpCon.disconnect();
	               Vector<String> params = parseMessage(response);
	               if (params.elementAt(0).equals("COMMENT")) {
	            	   ((TextView)findViewById(R.id.comment_user_tv)).setText(params.elementAt(1) + " says: ");
	            	   ((TextView)findViewById(R.id.comment_text_tv)).setText(params.elementAt(2));
	               }
	               if (!stop) {
	            	   mHandler.postDelayed(positionRunnable, Settings.pingServer * 1000);
	               }
	               else {
	            	   stop = false;
	            	   mHandler.post(disconnectRunnable);
	            	   ((TextView)findViewById(R.id.state_tv)).setText("Disconnected");
	               }
	          } catch (Exception e) {
	                Toast.makeText(Main.this, "HttpConnection error", Toast.LENGTH_SHORT).show();
	          }
		}
	};
    
    class LocationReceiver extends IntentReceiver {
    	@Override
    	public void onReceiveIntent(Context context, Intent intent) {
    		updateLocation();
    	}
    }
    
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
    	setContentView(R.layout.main);
        if (!LoadSettings()) {
        	Intent intent = new Intent(this, Settings.class);
        	startSubActivity(intent, ACTIVITY_SETTINGS);
        }
        else
        	((TextView)findViewById(R.id.username_tv)).setText(getString(R.string.user) + " " + Settings.username);
        
        getProviders();
        this.connectionState = getString(R.string.disconnected);
        
        ((TextView)findViewById(R.id.state_tv)).setText(this.connectionState);
    }
    @Override
    public void onStop() {
    	mHandler.removeCallbacks(connectRunnable);
    	super.onStop();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, String data, Bundle extras) {
        super.onActivityResult(requestCode, resultCode, data, extras);
        
        switch(requestCode) {
        case ACTIVITY_SETTINGS:
        	if (resultCode == RESULT_OK) {
        		SaveSettings();
        		((TextView)findViewById(R.id.username_tv)).setText(getString(R.string.user) + " " + Settings.username);
        	}
            break;
        case ACTIVITY_COMMENTS:
        	if (resultCode == RESULT_OK) {
        		synchronized(this) {
        			this.commentToSend = data;
        		}
        	}
            break;
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        
        menu.add(0, 0, "Connect");
        menu.add(0, 1, "Settings");
        menu.add(0, 2, "Disconnect");
        menu.add(0, 3, "Send Message");
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(Menu.Item item){
        switch (item.getId()) {
        case 0:
        	if (!this.isConnected)
        		this.connect();
            return true;
        case 1:
        	Intent intent = new Intent(this, Settings.class);
        	startSubActivity(intent, ACTIVITY_SETTINGS);
        	return true;
        case 2:
        	if (this.isConnected)
        		this.stop = true;
        	return true;
        case 3:
        	if (this.isConnected) {
            	Intent commentIntent = new Intent(this, Comments.class);
            	startSubActivity(commentIntent, ACTIVITY_COMMENTS);
        	}
        	else {
        		Toast.makeText(Main.this, "Not connected", Toast.LENGTH_SHORT).show();        	}
        	return true;
        }
        return false;
    }
    
    

    private void connect() {
    	mHandler.post(this.connectRunnable);
    }
    
    private void getProviders()
    {
    	this.locMan = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    	try {
        	LocationProvider provider = this.locMan.getProvider("gps");
            this.locMan.requestUpdates(provider, 0, 0, new Intent(Main.NEW_LOCATION));
    		this.locRec = new LocationReceiver();
    		this.registerReceiver(this.locRec, this.intentFilter);
    	}
    	catch(SecurityException se) {
    		((TextView)findViewById(R.id.username_tv)).setText("Error: " + se.getMessage());
    	}
    	catch(Exception e) {
    		((TextView)findViewById(R.id.username_tv)).setText("Error: " + e.getMessage());
    		return;
    	}
    }
    
    private void updateLocation()
    {
    	Location loc = this.locMan.getCurrentLocation("gps");
    	this.latitude = String.valueOf(loc.getLatitude());
    	((TextView)findViewById(R.id.latitude_tv)).setText(getString(R.string.latitude) + this.latitude);
    	this.longitude = String.valueOf(loc.getLongitude());
    	((TextView)findViewById(R.id.longitude_tv)).setText(getString(R.string.longitude) + this.longitude);
    }
    
    private boolean LoadSettings() {
        SharedPreferences settings = getSharedPreferences(this.getString(R.string.preferences_file), 0);
        Settings.username = settings.getString("Username", "");
        if (Settings.username == "")
        {
        	return false;
        }
        Settings.password = settings.getString("Password", "");
        Settings.pingServer = settings.getInt("Ping", Integer.MIN_VALUE);
        Settings.language = settings.getInt("Language", Integer.MIN_VALUE);
        
        return true;
    }
    
    private void SaveSettings()  {
        SharedPreferences settings = getSharedPreferences(this.getString(R.string.preferences_file), 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("Username", Settings.username);
        editor.putString("Password", Settings.password);
        editor.putInt("Ping", Settings.pingServer);
        editor.putInt("Language", Settings.language);
        editor.commit();
    }
    
    private Vector<String> parseMessage(String message) {
        Vector<String> messages = new Vector<String>();
        
        while (message.indexOf("$$$") != -1)
        {
        	messages.addElement(message.substring(0, message.indexOf("$$$")));
            message = message.substring(message.indexOf("$$$") + 3);
        }
        
        if (message.length() > 0)
        	messages.addElement(message);
            
        return messages;
    }

}
