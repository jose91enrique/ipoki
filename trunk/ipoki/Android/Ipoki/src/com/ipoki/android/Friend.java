package com.ipoki.android;

class Friend {
	final String mName;
	final double mLongitude;
	final double mLatitude;
	final String mSessionKey;
	private double mDistance;
	private double mBearing;
	
	static final int earthRatio = 6371;
	
	public Friend(String name, String latitude, String longitude, String sessionKey) {
		mName = name;
		mLongitude = Double.parseDouble(longitude);
		mLatitude = Double.parseDouble(latitude);
		mSessionKey = sessionKey;
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
