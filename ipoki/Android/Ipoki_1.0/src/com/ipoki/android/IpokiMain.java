package com.ipoki.android;
 
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class IpokiMain extends MapActivity {
	// User data
	private String mUser;
	private String mPass;
	private long mUpdateFreq;
	private String mVer = "ipoki.android.1.0";
	private Drawable mPicture;


	// Location
	private LocationManager mLocationManager=null;
	private LocationListener mLocationListener=null;
	static double mLatitude = 0;
	static double mLongitude = 0;
	static float mSpeed = 0;
	static double mHigh = 0;
	static double mTo = 0;
	static boolean isMapShowed = false;
	private boolean mPublishingOn = false;
	private long mLastTime = 0;
	private double mOldLatitude = 0;
	private double mOldLongitude = 0;


	// Download threads
	private ProgressDialog processDialog = null;
	
	// Utils
	private Geocoder mGeocoder;
	
	// Login screen 
	private boolean mLoginLayoutVisible = false;
	private EditText mUserText;
	private EditText mPassText;

	// Map
	private MapController mMapController;
	
	// Static members
	static String mUserKey = null;
	static Friend[] mFriends = null;
	static FriendsUpdateThread mFriendsUpdateThread = null;
	public static boolean mFriendsDownloaded = false;

	
	public TextView mMapUserName;
	public TextView outlat;
	public TextView outlon;
	public TextView outspeed;
	public TextView outhigh;
	
	private final String mServer = "http://www.ipoki.com";


	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Search location
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLocationListener = new MyLocationListener();
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 100, mLocationListener);
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 100, mLocationListener);
        
        // Set splash/login screen
        setContentView(R.layout.init);
        
        // Get preferences
		PreferenceManager.setDefaultValues(this, R.xml.setup, false);
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
		
		// Used to get address information
        mGeocoder = new Geocoder(this, Locale.getDefault());

		// TODO: to strings.xml
		mUser = p.getString("user", "");
		if (mUser == "") {
			showLoginPass();
		}
		else {
	    	mPass = p.getString("pass", "pass");
	    	mUpdateFreq = p.getLong("updateFreq", 60);
	    	logIn();
		}
			
		mFriendsUpdateThread = new FriendsUpdateThread();
        IpokiMain.mFriendsUpdateThread.setRunning(true);
        IpokiMain.mFriendsUpdateThread.start();
    }
	
	
	/*
	 * Login and user edits are on an invisible layout.
	 * If this is our first time with the app, or the credentials are wrong, 
	 * we make the layout visible.
	 */

	// Make login/pass layout visible
	public void showLoginPass() {
		showLoginPass("", "");
	}

	public void showLoginPass(String user, String pass) {
		View logpassLayout =  (View) findViewById(R.id.login_layout);
		logpassLayout.setVisibility(View.VISIBLE);

		mUserText = (EditText) findViewById(R.id.user_name);
		mUserText.setText(user);
		mPassText = (EditText) findViewById(R.id.password);
		mPassText.setText(pass);

		// Set button listener
		Button loginButton = (Button) findViewById(R.id.login_button);
		loginButton.setOnClickListener(clickLogin);
	}

    private OnClickListener clickLogin = new OnClickListener(){
		@Override
		public void onClick(View arg0) {
			// Get user and pass to call signin url
			mUser = mUserText.getText().toString();
			mPass = mPassText.getText().toString();
			logIn();
		}
    };
    
    // To log in we use an AsyncTask
    private void logIn() {
    	// TODO: to strings.xml
    	String userUrl = mServer + "/signin.php?user=" + mUser + "&pass=" + mPass + "&ver=" + mVer;
		new LoginUser().execute(userUrl);
		// TODO: to strings.xml
		processDialog = ProgressDialog.show(IpokiMain.this, "Ipoki", "Logging in user"); 
    }
    
    private class LoginUser extends AsyncTask<String, Integer, String> {

     	@Override
 		protected String doInBackground(String... params) {
     		// Make http get to url
 	    	return IpokiMain.this.serverRequest(params[0]);
 		}
     	
 	    protected void onPostExecute(String result) {
     		processDialog.dismiss();

    		// TODO: to strings.xml
     		if (result.equals("CODIGO$$$ERROR$$$")) {
     			// Wrong user/pass.
    			Toast.makeText(getBaseContext(), 
 					   "Wrong user or password", 
 					   Toast.LENGTH_SHORT).show();

 		        if (!mLoginLayoutVisible) {
 		        	showLoginPass(mUser, mPass);
 		        }
 		        
 		 	} else {
 		    	String[] resultData = result.split("\\${3}");
 		    	if (resultData.length >= 6) {
 			 		mUserKey = resultData[1];
 			 		mPublishingOn = true;
 			 		
 			 		// All went fine. Store credentials
 			 		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(IpokiMain.this);
 			 		SharedPreferences.Editor editor = prefs.edit();
 			 		editor.putString("user", mUser);
 			 		editor.putString("pass", mPass);
 			 		editor.commit();
 			 		
 			    	String userUrl = "http://www.ipoki.com/myfriends2.php?iduser=" + mUserKey;
 			    	String picUrl = resultData[6];
 					new DownloadFriends().execute(userUrl);
					new DownloadPicture().execute(picUrl);
					processDialog = ProgressDialog.show(IpokiMain.this, "Ipoki", "Downloading friends"); 

 		    	}
 		 	}
 	    }
    }
    
    private class DownloadPicture extends AsyncTask<String, Integer, Drawable> {

    	@Override
		protected Drawable doInBackground(String... params) {
			Drawable d = null;
	    	try {
	    		URL url = new URL(params[0]);
	    		HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
	    		int responseCode = urlConnection.getResponseCode();

	    		if (responseCode == HttpURLConnection.HTTP_OK) {
	    			InputStream is = urlConnection.getInputStream();
	    			BufferedInputStream bis = new BufferedInputStream(is);
	    			d = Drawable.createFromStream(bis, "");
	    		}
	    	} catch (IOException e) {
				e.printStackTrace();
			}
	    	return d;
		}
		
	    protected void onPostExecute(Drawable picture) {
	    	mPicture = picture;
	    }
    }

    
    private class DownloadFriends extends AsyncTask<String, Integer, Friend[]> {

     	@Override
 		protected Friend[] doInBackground(String... params) {
     		String result = IpokiMain.this.serverRequest(params[0]);
 	    	return processFriends(result);
 		}
     	
     	// With the raw data, we build a Friend object for each friend
     	private Friend[] processFriends(String result)
     	{
 	    	String[] friendsData = null;
 	    	/* Friends data comes with this format:
 	    	*  $$$user$$$latitude$$$longitude$$$key$$$date_last_position$$$picture_url
 	    	*  Let's split it into a string array
 	    	**/
 	    	friendsData = result.substring(3).split("\\${3}");
 	    	
 	    	if (friendsData.length % 6 != 0)
 	    		Log.w("Ipoki", "Malformed data from server");

 	    	// Each friend is composed of 6 fields
 	    	int friendsNum = friendsData.length / 6;
 	    	Friend[] friends = new Friend[friendsNum];
 	    	for (int i = 0; i < friendsNum; i++) {
 	    		friends[i] = new Friend(friendsData[6 * i], 
 	    								friendsData[6 * i + 1], 
 	    								friendsData[6 * i + 2], 
 	    								friendsData[6 * i + 3], 
 	    								friendsData[6 * i + 4], 
 	    								friendsData[6 * i + 5]);
 	    		// From our current position, calculate distance and direction (bearing)
 	    		friends[i].updateDistanceBearing(mLongitude, mLatitude);
 	    		// Get address
 	    		friends[i].setAddress(mGeocoder);
 	    	}
 	    	
 	    	return friends;
     	}
     	
 	    protected void onPostExecute(Friend[] friends) {
     		// Main friends collection
 	    	mFriends = friends;
 	    	
 	    	// We build another collection, this one with the friends in range
 	    	Friend.getFriendsInDistance();
 	    	mFriendsDownloaded = true;
 	    	if (Friend.mFriendsInDistance.length > 0)
 	    		// Selected friend by default
 	    		ARView.mSelectedFriend = Friend.mFriendsInDistance[0];
 	    	
 	    	// We dismiss login screen and go for the main one
     		processDialog.dismiss();
 	    	showMainView();
 	    }
    }
    
    private void showMainView() {
    	//setContentView(R.layout.main);
    	setContentView(R.layout.map_layout);
    	MapView mapView = (MapView)findViewById(R.id.ipoki_map);
    	mMapController = mapView.getController();
    	mapView.setSatellite(true);
    	mapView.setBuiltInZoomControls(true);
    	mMapController.setZoom(13);
    	GeoPoint geoPoint = new GeoPoint((int)(mLatitude*1E6), (int)(mLongitude*1E6));
    	mMapController.animateTo(geoPoint);
		Drawable marker = getResources().getDrawable(R.drawable.ipokito32x32orange);
		marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker
				.getIntrinsicHeight());
		mapView.getOverlays().add(new FriendsOverlay(marker));
		List<Overlay> overlays = mapView.getOverlays(); 
		MyLocationOverlay myLocationOverlay = new MyLocationOverlay(this, mapView);
		myLocationOverlay.enableCompass();
		myLocationOverlay.enableMyLocation();
		overlays.add(myLocationOverlay);
		ImageView userImage = (ImageView) findViewById(R.id.user_image);
		userImage.setImageDrawable(mPicture);
    	mapView.invalidate();
    	
    	final ToggleButton recordButton = (ToggleButton) findViewById(R.id.button_record);
    	recordButton.setOnClickListener(new OnClickListener() {
    	    public void onClick(View v) {
    	        // Perform action on clicks
    	        if (recordButton.isChecked()) {
    	        	new Thread() {
    	            	public void run() {
    	            		String url = mServer + "/set_h_on.php?iduser=" + mUserKey;
    	            		serverRequest(url);
    	            	} 
    	            }.start();    	        
    	        } else {
    	        	new Thread() {
    	            	public void run() {
    	            		String url = mServer + "/set_h_off.php?iduser=" + mUserKey;
    	            		serverRequest(url);
    	            	} 
    	            }.start();    	        
    	        }
    	    }
    	});
    	final ToggleButton publishButton = (ToggleButton) findViewById(R.id.button_publish);
    	publishButton.setOnClickListener(new OnClickListener() {
    	    public void onClick(View v) {
    	        // Perform action on clicks
    	        if (publishButton.isChecked()) {
    	        	new Thread() {
    	            	public void run() {
    	            		String url = mServer + "/set_p_off.php?iduser=" + mUserKey;
    	            		serverRequest(url);
    	            	} 
    	            }.start();    	        
    	        } else {
    	        	new Thread() {
    	            	public void run() {
    	            		String url = mServer + "/set_p_on.php?iduser=" + mUserKey;
    	            		serverRequest(url);
    	            	} 
    	            }.start();    	        
    	        }
    	    }
    	});

    	mMapUserName = (TextView) findViewById(R.id.map_user_name);
    	outlat = (TextView) findViewById(R.id.map_latitude);
        outlon = (TextView) findViewById(R.id.map_longitude);
        outspeed = (TextView) findViewById(R.id.map_speed);
        outhigh = (TextView) findViewById(R.id.map_height);
        
        mMapUserName.setText(mUser);
		 outlon.setText(String.format("%.4f", mLongitude));
		 outlat.setText(String.format("%.4f", mLatitude));
		 outspeed.setText(String.format("%.1f km/h",mSpeed*3.6));
		 outhigh.setText(String.format("%.1f m",mHigh));
        
        isMapShowed = true;
}
    
    public void showWelcome() {
		AlertDialog.Builder builder = new AlertDialog.Builder(IpokiMain.this);
 	    builder.setTitle("Welcome to Ipoki");
 	    builder.setIcon(R.drawable.icon);
 	    builder.setMessage("Welcome to Ipoki service.\n" + 
 	    		"To use the program and share your position you need a Ipoki account.\n" +
 	    		"Visit our web site in: \n\n" +
 	    		"http://www.ipoki.com\n\n" + 
 	    		"and sign up, it's free!!");
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // va al setup
            	ShowSetup();
            }
        });
        builder.show();
	}

	 public void updateLocationViews() {    	 
		 if (isMapShowed) {
	      	 // pintar la posicion, velocidad y altura
	    	 outlon.setText(String.format("%.5f", mLongitude));
	    	 outlat.setText(String.format("%.5f", mLatitude));
	    	 outspeed.setText(String.format("%.1f",mSpeed*3.6));
	    	 outhigh.setText(String.format("%.1f",mHigh));
		 }
    	 
    	 if (mPublishingOn) {
    		long now = System.currentTimeMillis(); 
    		if ((now - mLastTime) > (mUpdateFreq * 1000)) {
        	 	updateLocationAtServer();
        	 	mLastTime = System.currentTimeMillis();
    		}
    	 }
     }

     public void updateLocationAtServer() {
    	// controla si ha habido cambio de posicion
    	String changed = "0"; 
 	 	if (mOldLatitude != mLatitude || mOldLongitude != mLongitude) {
 	 		changed = "1";
 	 	}
    	serverRequest(mServer + "/ear.php?iduser=" + mUserKey + "&lat=" + String.valueOf(mLatitude).substring(0,10) + "&lon=" + String.valueOf(mLongitude).substring(0,10) + "&h=" + String.valueOf(mHigh) + "&speed=" + String.valueOf(mSpeed*3.6) + "&to=" + String.valueOf(mTo) + "&comment=&action=0&change=" + changed);
	 	mOldLatitude = mLatitude;
	 	mOldLongitude = mLongitude;
     }
    
     

     


     private void PowerOff(){
    	// hace la llamada a la web
      	Comunica(mServer + "/signout.php?iduser=" + mUserKey);
      	mPublishingOn = false;
     }

     public void ShowExit() {
    	 // si esta conectado pregunta si se borra la posicion
    	 if (mPublishingOn){
    		PideFin();
    	 } else {
   	    	// Acabar
    		mLocationManager.removeUpdates(mLocationListener);
    		finish();
    	 }
     }

     public void PideFin() {
    	 // si esta conectado pregunta si se borra la posicion
 	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
 	    builder.setTitle("Ipoki Connection");
 	    builder.setMessage("You are connected.\n" +
 	    		"Do you want to keep the position on Ipoki after exit?");
 	    builder.setIcon(R.drawable.alert_dialog_icon);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
       	    	// Acabar
        		mLocationManager.removeUpdates(mLocationListener);
            	finish();
            }
        });
 	   builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
         	    // hace la llamada a la web
        		Comunica(mServer + "/signout.php?iduser=" + mUserKey);      		
       	    	// Acabar
        		mLocationManager.removeUpdates(mLocationListener);
            	finish();
            }
        });         
        builder.show();
     }

     public void ShowFriends() {
    	 if (!mPublishingOn){
 	 	    AlertDialog.Builder builder = new AlertDialog.Builder(IpokiMain.this);
	 	    builder.setTitle("Ipoki Connection");
	 	    builder.setIcon(R.drawable.alert_dialog_icon);
	 	    builder.setMessage("You are not connected.");
	        builder.show();
     	 } else {
	     	// Muestra los amigos
			Toast.makeText(getBaseContext(), 
					   "Loading friends...", 
					   Toast.LENGTH_LONG).show();
	      	Intent intent = new Intent(); 
	     	intent.setClass(IpokiMain.this, FriendsView.class); 
	     	startActivity(intent);
     	 }
     }

     public void ShowSetup() {
     	// La configuracion
         Intent launchPreferencesIntent = new Intent().setClass(this, Setup.class);
         startActivityForResult(launchPreferencesIntent, 1);
     }
    
     public class LoadSetup extends PreferenceActivity {

    	    @Override
    	    protected void onCreate(Bundle savedInstanceState) {
    	        super.onCreate(savedInstanceState);

    	        // Load the preferences from an XML resource
    	        addPreferencesFromResource(R.xml.setup);
    	    }
    	}
     
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
        
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
	        case R.id.mymap:
	            //mymap            	
	        	ShowMap();
	        	break;            
            case R.id.setup:
                //settings
            	ShowSetup();
            	break;            
            case R.id.myfriends:
                //myfriends
            	ShowFriends();
            	break;            
            case R.id.ar_view:
            	startActivity(new Intent(this, IpokiAR.class));
            	break;            
            case R.id.exit:
                //exit
            	ShowExit();
            	break;            
        }
        return false;
    }

    public void ShowMap() {
     	// Muestra el mapa del usuario
     	Intent intent = new Intent(); 
    	intent.setClass(IpokiMain.this, Mymap.class); 
    	startActivity(intent); 
     }

    private class MyLocationListener implements LocationListener {
    	
		@Override
		public void onLocationChanged(Location location) {
			if (location != null) {
				mLatitude = location.getLatitude();
				mLongitude = location.getLongitude();
				mSpeed = location.getSpeed();
				mHigh = location.getAltitude();
				mTo = location.getBearing();
				// lo pinta
				updateLocationViews();
			}			
		}
	
		@Override
		public void onProviderDisabled(String provider) {
		}
	
		@Override
		public void onProviderEnabled(String provider) {
		}
	
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}
        
    private String Comunica(String userURL){
    	String resultado=null;
    	URL url;
		try {
			url = new URL(userURL);
    		HttpURLConnection urlConnection = null;
			try {
				urlConnection = (HttpURLConnection)url.openConnection();
			} catch (IOException e) {
				//e.printStackTrace();
			}
    		int responseCode = 0;
			try {
				responseCode = urlConnection.getResponseCode();
			} catch (IOException e) {
				//e.printStackTrace();
			}
    		if (responseCode == HttpURLConnection.HTTP_OK) {
    			InputStream is = null;
				try {
					is = urlConnection.getInputStream();
				} catch (IOException e) {
					//e.printStackTrace();
				}
    			BufferedReader br = new BufferedReader(new InputStreamReader(is));
    	    	try {
    	    		// coge el resultado de la llamada pero no hace nada.
					resultado = br.readLine();
				} catch (IOException e) {
					//e.printStackTrace();
				}
    		}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} 
		return resultado;
    }
    
    String serverRequest(String urlString) {
		String result = "";
    	try {
        	URL url = new URL(urlString);
    		HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
    		int responseCode = urlConnection.getResponseCode();

    		if (responseCode == HttpURLConnection.HTTP_OK) {
    			InputStream is = urlConnection.getInputStream();
    			BufferedReader br = new BufferedReader(new InputStreamReader(is));
    			result = br.readLine();
    		}
    	} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	return result;
    }
    

    @Override
    protected void onDestroy() {
    	super.onDestroy();
		mLocationManager.removeUpdates(mLocationListener);
       boolean retry = true;
       IpokiMain.mFriendsUpdateThread.setRunning(false);
        while (retry) {
            try {
            	IpokiMain.mFriendsUpdateThread.join();
                retry = false;
           } catch (InterruptedException e) {
            }
        }
    }


	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}    
	
	public class FriendsOverlay extends ItemizedOverlay<OverlayItem> {
		private List<Friend> locations = new ArrayList<Friend>();
		private Drawable mMarker;

		public FriendsOverlay(Drawable marker) {
			super(marker);
			mMarker = marker;
			
			for (Friend f: IpokiMain.mFriends) {
				locations.add(f);		
			}
			populate();
		}

		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			super.draw(canvas, mapView, shadow);
			boundCenterBottom(mMarker);
		}
		
		@Override
		protected boolean onTap(int i) {
/*			Friend f = locations.get(i);
			IpokiMain.this.mMapUserName.setText(f.mName);*/
			return false;
		}
		
		@Override
		protected OverlayItem createItem(int i) {
			Friend f = locations.get(i);
			GeoPoint gp = new GeoPoint((int) (f.mLatitude * 1E6), (int) (f.mLongitude * 1E6));
			return new OverlayItem(gp, f.mName, "tralala");		
		}

		@Override
		public int size() {
			return locations.size();
		}

	}

}