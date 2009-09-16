package com.ipoki.android;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

class Friend {
	final String mName;
	final double mLongitude;
	final double mLatitude;
	final String mSessionKey;
	Date mLocationDate;
	Drawable mPicture;
	Address mAddress;
	boolean isSelected = false;
	public double mDistance;
	public double mBearing;
	private int mX = -1;
	private int mY = -1;
	Friend mNext;
	Friend mPrevious;
	public static Friend[] mFriendsInDistance;
	static double mFriendsDistance = 10; 
	
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
	
	public void setAddress(Geocoder geocoder) {
		try {
			List<Address> addresses = geocoder.getFromLocation(mLatitude, mLongitude, 1);
			if (!addresses.isEmpty()) {
				mAddress = addresses.get(0);
			}
		} catch (IOException e) {
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
    
    public static void placeFriendInOrder(Friend existingFriend, Friend newFriend) {
  		if (existingFriend.mBearing > newFriend.mBearing) {
  			while(existingFriend.mBearing > newFriend.mBearing && existingFriend.mBearing > existingFriend.mPrevious.mBearing) {
  				existingFriend = existingFriend.mPrevious;
  			}
  			if (existingFriend.mBearing < existingFriend.mPrevious.mBearing && existingFriend.mBearing > newFriend.mBearing)
  				existingFriend = existingFriend.mPrevious;
  			newFriend.mPrevious = existingFriend;
  			newFriend.mNext = existingFriend.mNext;
  			newFriend.mNext.mPrevious = newFriend;
  			existingFriend.mNext = newFriend;
  		}
  		else {
 			while(existingFriend.mBearing < newFriend.mBearing && existingFriend.mBearing < existingFriend.mNext.mBearing) {
 				existingFriend = existingFriend.mNext;
 			}
 			if (existingFriend.mBearing > existingFriend.mNext.mBearing && existingFriend.mBearing < newFriend.mBearing)
 				existingFriend = existingFriend.mNext;
  			newFriend.mPrevious = existingFriend.mPrevious;
  			newFriend.mNext = existingFriend;
  			newFriend.mPrevious.mNext = newFriend;
  			existingFriend.mPrevious = newFriend;
  		}
    }
    
    public static void getFriendsInDistance() {
    	ArrayList<Friend> friendsArray = new ArrayList<Friend>();
    	Friend[] allFriends = IpokiMain.mFriends;
    	int friendsInDistanceNum = 0;
	    for (Friend f: allFriends) {
	    	if (f.mDistance < Friend.mFriendsDistance) {
	    		if (friendsInDistanceNum > 0) {
	 	    		Friend existingFriend = friendsArray.get(friendsInDistanceNum - 1); 
	 	    		Friend newFriend = f; 
	 	    		Friend.placeFriendInOrder(existingFriend, newFriend);
	    		}
	    		else if (friendsInDistanceNum == 0) {
	    			f.mNext = f;
	    			f.mPrevious = f;
	    			f.isSelected = true;
	    		} 
	    		friendsInDistanceNum++;
	    		friendsArray.add(f);
	    	}
 	    }
	    mFriendsInDistance = new Friend[friendsArray.size()];
	    mFriendsInDistance = friendsArray.toArray(mFriendsInDistance);
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
        	if (IpokiMain.mFriends != null) {
	        	for (Friend f: IpokiMain.mFriends) {
	       			f.updateDistanceBearing(IpokiMain.mLongitude, IpokiMain.mLatitude);
	        	}
        	}
        	try {
				FriendsUpdateThread.sleep(6000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
    }
}
