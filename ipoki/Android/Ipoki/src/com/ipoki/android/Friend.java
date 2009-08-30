package com.ipoki.android;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

class Friend {
	final String mName;
	final double mLongitude;
	final double mLatitude;
	final String mSessionKey;
	Date mLocationDate;
	Drawable mPicture;
	boolean isSelected = false;
	private double mDistance;
	private double mBearing;
	private int mX = -1;
	private int mY = -1;
	
	static final int earthRatio = 6371;
	
	public Friend(String name, String latitude, String longitude, String sessionKey, String dateTime, String urlPicture) {
		mName = name;
		mLongitude = Double.parseDouble(longitude);
		mLatitude = Double.parseDouble(latitude);
		mSessionKey = sessionKey;
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			URL url = new URL(urlPicture);
			new DownloadPicture().execute(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} 

		try {
			mLocationDate = dateFormater.parse(dateTime);
		} catch (ParseException e) {
			mLocationDate = null;			
			e.printStackTrace();
		}
	}
	
	public void setScreenPos(int x, int y) {
		mX = x;
		mY = y;
	}
	
	public int getDistanceFromScreenPoint(float x, float y) {
		int xDis = ((int)x - mX);
		int yDis = ((int)y - mY);
		return (int) Math.sqrt(xDis*xDis + yDis*yDis);
	}
	
	public synchronized double[] getDistanceBearing() {
		double[] d = new double[]{ mDistance, mBearing };
		return d;
	}
	public synchronized void updateDistanceBearing(double baseLong, double baseLat) {
		double rLat1 = Math.toRadians(baseLat);
		double rLat2 = Math.toRadians(mLatitude);
		double rDLon = Math.toRadians(mLongitude) - Math.toRadians(baseLong);
		double sinLat1 = Math.sin(rLat1);
		double sinLat2 = Math.sin(rLat2);
		double cosLat1 = Math.cos(rLat1);
		double cosLat2 = Math.cos(rLat2);
		double cosDLon = Math.cos(rDLon);
		
		mDistance = Math.acos(sinLat1 * sinLat2 + cosLat1 * cosLat2 * cosDLon) * earthRatio;
		
		double y = Math.sin(rDLon) * cosLat2;
		double x = cosLat1 * sinLat2 - sinLat1 * cosLat2 * cosDLon;
		mBearing = Math.atan2(y, x);
	}
	
    private class DownloadPicture extends AsyncTask<URL, Integer, Drawable> {

    	@Override
		protected Drawable doInBackground(URL... params) {
			Drawable d = null;
	    	try {
	    		HttpURLConnection urlConnection = (HttpURLConnection)params[0].openConnection();
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
}

class FriendsUpdateThread extends Thread {
	private boolean mRun = false;
	
	public FriendsUpdateThread() {
	}

	public void setRunning(boolean run) {
		mRun = run;
    }
    
    public boolean isRunning() {
        return mRun;
    }

	
    public void run() {
        while (mRun) {
        	if (Ipoki.mFriends != null) {
	        	for (Friend f: Ipoki.mFriends) {
	       			f.updateDistanceBearing(Ipoki.mLongitude, Ipoki.mLatitude);
	        	}
        	}
        	try {
				FriendsUpdateThread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
    }
}
