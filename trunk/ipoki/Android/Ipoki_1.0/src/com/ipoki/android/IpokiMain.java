package com.ipoki.android;
 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

public class IpokiMain extends MapActivity {
	// User data
	private String mUser;
	private String mPass;
	private String mIntervalo;
	private String mVer = "ipoki.android.1.0";

	// Location
	private LocationManager mLocationManager=null;
	private LocationListener mLocationListener=null;
	static double mLatitude = 0;
	static double mLongitude = 0;
	static float mSpeed = 0;
	static double mHigh = 0;
	static double mTo = 0;
	static boolean isMapShowed = false;

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
	
	public boolean mOn = false;
	private final String mServer = "http://www.ipoki.com";

	private double oldLatitude = 0;
	private double oldLongitude = 0;
	private String wprivate = "0";
	private String wrec = "0";
	private long lastTime = 0;

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
	    	mIntervalo = p.getString("intervalo", "3");
	    	logIn();
		}
			


		mFriendsUpdateThread = new FriendsUpdateThread();
        IpokiMain.mFriendsUpdateThread.setRunning(true);
        IpokiMain.mFriendsUpdateThread.start();


        

		// leer preferencias la primera vez
/*		PreferenceManager.setDefaultValues(this, R.xml.setup, false);
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(this);
		mUser = p.getString("user", "value_default_98732");
		// si no hay user grabado, va a preferencias para pedirlo
		if (mUser.equals("value_default_98732")) {
			ShowWelcome();
		}*/
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
		try {
			URL url = new URL(userUrl);
			new LoginUser().execute(url);
			// TODO: to strings.xml
     		processDialog = ProgressDialog.show(IpokiMain.this, "Ipoki", "Logging in user");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} 
    }
    
    private class LoginUser extends AsyncTask<URL, Integer, String> {

     	@Override
 		protected String doInBackground(URL... params) {
     		// Make http get to url
 	    	return IpokiMain.this.getFromServer(params[0]);
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
 			 		wrec = resultData[4];
 			 		wprivate = resultData[5];
 			 		mOn = true;
 			 		
 			 		// All went fine. Store credentials
 			 		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(IpokiMain.this);
 			 		SharedPreferences.Editor editor = prefs.edit();
 			 		editor.putString("user", mUser);
 			 		editor.putString("pass", mPass);
 			 		editor.commit();
 			 		
 			    	String userUrl = "http://www.ipoki.com/myfriends2.php?iduser=" + mUserKey;
 					try {
 						URL url = new URL(userUrl);
 						new DownloadFriends().execute(url);
 			     		processDialog = ProgressDialog.show(IpokiMain.this, "Ipoki", "Downloading friends");
 					} catch (MalformedURLException e) {
 						e.printStackTrace();
 					} 

 		    	}
 		 	}
 	    }
    }
    
    private class DownloadFriends extends AsyncTask<URL, Integer, Friend[]> {

     	@Override
 		protected Friend[] doInBackground(URL... params) {
     		String result = IpokiMain.this.getFromServer(params[0]);
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
     		processDialog.dismiss();
     		
     		// Main friends collection
 	    	mFriends = friends;
 	    	
 	    	// We build another collection, this one with the friends in range
 	    	Friend.getFriendsInDistance();
 	    	if (Friend.mFriendsInDistance.length > 0)
 	    		// Selected friend by default
 	    		ARView.mSelectedFriend = Friend.mFriendsInDistance[0];
 	    	
 	    	// We dismiss login screen and go for the main one
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
    	mapView.invalidate();

		// los textbox
/*        outlat = (TextView) findViewById(R.id.txtlat);
        outlon = (TextView) findViewById(R.id.txtlon);
        outspeed = (TextView) findViewById(R.id.txtspeed);
        outhigh = (TextView) findViewById(R.id.txth);*/
    	
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
        
        // los oidores de los botones
/*        ImageView btna = (ImageView) findViewById(R.id.img00);
        btna.setOnClickListener(pulsabtna);
        ImageView btnb = (ImageView) findViewById(R.id.img01);
        btnb.setOnClickListener(pulsabtnb);
        ImageView redbtnon = (ImageView) findViewById(R.id.img20);
        redbtnon.setOnClickListener(pulsaredbtnon);
        ImageView redbtnoff = (ImageView) findViewById(R.id.img21);
        redbtnoff.setOnClickListener(pulsaredbtnoff);
        ImageView btnon = (ImageView) findViewById(R.id.img30);
        btnon.setOnTouchListener (tocabtnon);
        btnon.setOnClickListener(pulsabtnon);
        ImageView btnoff = (ImageView) findViewById(R.id.img31);
        btnoff.setOnClickListener(pulsabtnoff);*/
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
    	 
/*    	 if (mOn) {
    		// si esta conectado se manda la posicion 
    		// solo si ha pasado el intervalo correspondiente (MINIMO=3)
    		int iwork = 0;
 			try {
 	    		iwork = Integer.parseInt(mIntervalo);
			} catch (Exception e) {
				//e.printStackTrace();
			}
    		if (iwork < 3) iwork = 3;
    		long mNow = System.currentTimeMillis(); 
    		if ((mNow - lastTime) > (iwork * 1000)) {
        	 	SendLoc();
        	 	lastTime = System.currentTimeMillis();
    		}
    	 }*/
     }

     public void SendLoc() {
    	// controla si ha habido cambio de posicion
    	String mChange = "0"; 
 	 	if (oldLatitude != mLatitude) {
 	 		mChange = "1";
 	 	}
 	 	if (oldLongitude != mLongitude) {
 	 		mChange="1";
 	 	}
    	Comunica(mServer + "/ear.php?iduser=" + mUserKey + "&lat=" + String.valueOf(mLatitude).substring(0,10) + "&lon=" + String.valueOf(mLongitude).substring(0,10) + "&h=" + String.valueOf(mHigh) + "&speed=" + String.valueOf(mSpeed*3.6) + "&to=" + String.valueOf(mTo) + "&comment=&action=0&change=" + mChange);
	 	oldLatitude = mLatitude;
	 	oldLongitude = mLongitude;
     }
    
     

     


     private void PowerOff(){
    	// hace la llamada a la web
      	Comunica(mServer + "/signout.php?iduser=" + mUserKey);
      	mOn = false;
     }
     
     private void SetPublic() {
 	    // hace la llamada a la web
    	Comunica(mServer + "/set_p_off.php?iduser=" + mUserKey);
     }

     private void SetPrivate() {
  	    // hace la llamada a la web
     	Comunica(mServer + "/set_p_on.php?iduser=" + mUserKey);
     }

     private void ActiveRec(){
  	    // hace la llamada a la web
     	Comunica(mServer + "/set_h_on.php?iduser=" + mUserKey);
     }

     private void InactiveRec(){
  	    // hace la llamada a la web
     	Comunica(mServer + "/set_h_off.php?iduser=" + mUserKey);
     }

     public void ShowExit() {
    	 // si esta conectado pregunta si se borra la posicion
    	 if (mOn){
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

     public void ShowMap() {
     	// Muestra el mapa del usuario
     	Intent intent = new Intent(); 
    	intent.setClass(IpokiMain.this, Mymap.class); 
    	startActivity(intent); 
     }
     
     public void ShowFriends() {
    	 if (!mOn){
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
     
    private OnClickListener pulsabtna = new OnClickListener(){

		@Override
		public void onClick(View arg0) {
			Toast.makeText(getBaseContext(), 
					   "Set private position", 
					   Toast.LENGTH_LONG).show();
			
			ImageView image = (ImageView)findViewById(R.id.img00); 
		    image.setVisibility(View.INVISIBLE); 
		    image = (ImageView)findViewById(R.id.img01); 
		    image.setVisibility(View.VISIBLE); 

		    // llama a SetPrivate
		    SetPrivate();
			}
		};

    private OnClickListener pulsabtnb = new OnClickListener(){

		@Override
		public void onClick(View arg0) {
			Toast.makeText(getBaseContext(), 
					   "Set public position", 
					   Toast.LENGTH_LONG).show();
			
			ImageView image = (ImageView)findViewById(R.id.img01); 
		    image.setVisibility(View.INVISIBLE); 
		    image = (ImageView)findViewById(R.id.img00); 
		    image.setVisibility(View.VISIBLE);
		    
		    // llama a SetPublic
		    SetPublic();
		}
    };

    private OnClickListener pulsaredbtnon = new OnClickListener(){

		@Override
		public void onClick(View arg0) {
			Toast.makeText(getBaseContext(), 
					   "Track route on", 
					   Toast.LENGTH_LONG).show();
			
			ImageView image = (ImageView)findViewById(R.id.img20); 
		    image.setVisibility(View.INVISIBLE); 
		    image = (ImageView)findViewById(R.id.img21); 
		    image.setVisibility(View.VISIBLE); 
		    image = (ImageView)findViewById(R.id.img11); 
		    image.setVisibility(View.VISIBLE); 
		    image = (ImageView)findViewById(R.id.img10); 
		    image.setVisibility(View.INVISIBLE);
		    
		    //llamar a ActiveRec
		    ActiveRec();
		}
    };

    private OnClickListener pulsaredbtnoff = new OnClickListener(){

		@Override
		public void onClick(View arg0) {
			Toast.makeText(getBaseContext(), 
						"Track route off", 
					   Toast.LENGTH_LONG).show();

			ImageView image = (ImageView)findViewById(R.id.img21); 
		    image.setVisibility(View.INVISIBLE); 
		    image = (ImageView)findViewById(R.id.img20); 
		    image.setVisibility(View.VISIBLE); 
		    image = (ImageView)findViewById(R.id.img10); 
		    image.setVisibility(View.VISIBLE); 
		    image = (ImageView)findViewById(R.id.img11); 
		    image.setVisibility(View.INVISIBLE); 

		    // llamar a InactiveRec
		    InactiveRec();
		}
    };

    private OnTouchListener tocabtnon = new OnTouchListener(){
    	
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// hace que esto salga inmediatamente al pulsar !!!
			ImageView image = (ImageView)findViewById(R.id.img30); 
		    image.setVisibility(View.INVISIBLE); 
		    image = (ImageView)findViewById(R.id.img31); 
		    image.setVisibility(View.VISIBLE);
		    return false;
		}
    };

    private OnClickListener pulsabtnon = new OnClickListener(){
    	
		@Override
		public void onClick(View arg0) {
			// llamar a PowerOn
		    logIn();
		    PonerBotones();
		}
    };

    private OnClickListener pulsabtnoff = new OnClickListener(){

		@Override
		public void onClick(View arg0) {
			Toast.makeText(getBaseContext(), 
					   "Disconnecting", 
					   Toast.LENGTH_LONG).show();
			ImageView image = (ImageView)findViewById(R.id.img31); 
		    image.setVisibility(View.INVISIBLE); 
		    image = (ImageView)findViewById(R.id.img30); 
		    image.setVisibility(View.VISIBLE);
		    
		    // llama a PowerOff
	        PowerOff();		    
		    QuitarBotones();
		}
    };

    private void QuitarBotones(){
    	TextView lab = (TextView)findViewById(R.id.lab3); 
    	lab.setVisibility(View.INVISIBLE); 
    	lab = (TextView)findViewById(R.id.lab1); 
    	lab.setVisibility(View.INVISIBLE); 
    	lab = (TextView)findViewById(R.id.lab2); 
    	lab.setVisibility(View.INVISIBLE); 

    	ImageView image = (ImageView)findViewById(R.id.img00); 
	    image.setVisibility(View.INVISIBLE); 
	    image = (ImageView)findViewById(R.id.img01); 
	    image.setVisibility(View.INVISIBLE); 
		image = (ImageView)findViewById(R.id.img20); 
	    image.setVisibility(View.INVISIBLE); 
	    image = (ImageView)findViewById(R.id.img21); 
	    image.setVisibility(View.INVISIBLE); 
	    image = (ImageView)findViewById(R.id.img11); 
	    image.setVisibility(View.INVISIBLE); 
	    image = (ImageView)findViewById(R.id.img10); 
	    image.setVisibility(View.INVISIBLE);
    }
    
    private void PonerBotones(){
    	TextView lab = (TextView)findViewById(R.id.lab3); 
    	lab.setVisibility(View.VISIBLE); 
    	lab = (TextView)findViewById(R.id.labtit); 
    	lab.setVisibility(View.INVISIBLE); 
    	lab = (TextView)findViewById(R.id.lab1); 
    	lab.setVisibility(View.VISIBLE); 
    	lab = (TextView)findViewById(R.id.lab2); 
    	lab.setVisibility(View.VISIBLE); 

    	// activar los controles de REC y Privado segun la configuracion
 		if (wrec.equals("2")) {
			ImageView image = (ImageView)findViewById(R.id.img11); 
		    image.setVisibility(View.VISIBLE); 
		    image = (ImageView)findViewById(R.id.img21); 
		    image.setVisibility(View.VISIBLE);
 		} else {
 			ImageView image = (ImageView)findViewById(R.id.img10); 
 		    image.setVisibility(View.VISIBLE); 
 		    image = (ImageView)findViewById(R.id.img20); 
 		    image.setVisibility(View.VISIBLE); 
 		}
 		if (wprivate.equals("2")) {
			ImageView image = (ImageView)findViewById(R.id.img00); 
		    image.setVisibility(View.VISIBLE);
 		} else {
 	    	ImageView image = (ImageView)findViewById(R.id.img01); 
 		    image.setVisibility(View.VISIBLE); 
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
            case R.id.myfriends:
                //myfriends
            	ShowFriends();
            	break;            
            case R.id.setup:
                //settings
            	ShowSetup();
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
    
    String getFromServer(URL url) {
			String result = "";
 	    	try {
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
}