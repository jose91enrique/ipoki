package ipoki.plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;

public class IpokiAR extends Activity {
    private SensorManager mSensorManager;
	private CameraView mCameraView;
	private ARView mARView;

	private LocationManager mLocationManager=null;
	private LocationListener mLocationListener=null;
	static double mLatitude;
	static double mLongitude;
	static final String mUserKey = "fgQSnMKxegGnfKfDXqQfnIVJR";

	static Friend[] mFriends = null;
	static FriendsUpdateThread mFriendsUpdateThread = null;
	public static final String PREFS_NAME = "PreferencesFile";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN); 

        mCameraView = new CameraView(this);
        setContentView(mCameraView);
       
        mARView = new ARView(this);
        addContentView(mARView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    
        List<Sensor> listSensors = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (listSensors.size() > 0)
        {
        	mSensorManager.registerListener(mARView, listSensors.get(0), SensorManager.SENSOR_DELAY_NORMAL);
         }

        listSensors = mSensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
        if (listSensors.size() > 0)
        {
        	mSensorManager.registerListener(mARView, listSensors.get(0), SensorManager.SENSOR_DELAY_NORMAL);
        }

		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLocationListener = new MyLocationListener();
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 100, mLocationListener);
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 100, mLocationListener);

		getFriends();
		mFriendsUpdateThread = new FriendsUpdateThread();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
		mLocationManager.removeUpdates(mLocationListener);
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
    }

    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    protected void onStop()
    {
        super.onStop();
    }
    
    private class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			if (location != null) {
				mLatitude = location.getLatitude();
				mLongitude = location.getLongitude();
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
    
    private void getFriends(){
    	URL url;
    	// TODO falta meter Main.mServer para que este bien...
		String userUrl = getString(R.string.friends_url) + Main.mUserKey;

		try {
			url = new URL(userUrl);
			new DownloadFriends().execute(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} 
    }

    private class DownloadFriends extends AsyncTask<URL, Integer, Friend[]> {

    	@Override
		protected Friend[] doInBackground(URL... params) {
			String friendsString = "";
	    	try {
	    		HttpURLConnection urlConnection = (HttpURLConnection)params[0].openConnection();
	    		int responseCode = urlConnection.getResponseCode();

	    		if (responseCode == HttpURLConnection.HTTP_OK) {
	    			InputStream is = urlConnection.getInputStream();
	    			BufferedReader br = new BufferedReader(new InputStreamReader(is));
	    			friendsString = br.readLine();
	    		}
	    	} catch (IOException e) {
				e.printStackTrace();
			}
	    	return processFriends(friendsString);
		}
    	
    	private Friend[] processFriends(String result)
    	{
	    	String[] friendsData = null;
	    	friendsData = result.substring(3).split("\\${3}");
	    	
	    	if (friendsData.length % 6 != 0)
	    		Log.w("Ipoki", "Malformed data from server");

	    	int friendsNum = friendsData.length / 6;
	    	Friend[] friends = new Friend[friendsNum];
	    	for (int i = 0; i < friendsNum; i++) {
	    		friends[i] = new Friend(friendsData[6 * i], 
	    								friendsData[6 * i + 1], 
	    								friendsData[6 * i + 2], 
	    								friendsData[6 * i + 3], 
	    								friendsData[6 * i + 4], 
	    								friendsData[6 * i + 5]);
	    		friends[i].updateDistanceBearing(mLongitude, mLatitude);
	    	}
	    	
	    	return friends;
    	}
		
	    protected void onPostExecute(Friend[] friends) {
	    	mFriends = friends;
	    }
    }
}
