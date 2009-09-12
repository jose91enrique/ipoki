package com.ipoki.android;

import java.text.DateFormat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class ARView extends View implements SensorEventListener {
    private float   mAccelerometerValues[] = new float[3];
    private float   mMagneticValues[] = new float[3];
    private float rotationMatrix[] = new float[16];
    private float mOrientationValues[] = new float[3];
    final double m2PiDiv3 = 2 * Math.PI / 3;
    final double m5PiDiv6 = 5 * Math.PI / 6;
    final double mPiDiv6 = Math.PI / 6;
    final double mPiDiv4 = Math.PI / 4;
    final double mPiDiv2 = Math.PI / 2;
    private final Drawable mIpokito;
    private final Drawable mSelectedIpokito;
    private final Drawable mLeftArrow;
    private final Drawable mRightArrow;
    private final int mIpokitoSemiHeight;
    private final int mIpokitoSemiWidth;
    private final int mSelectedIpokitoSemiHeight;
    private final int mSelectedIpokitoSemiWidth;
    private final Paint mPaintTextUsername;
    private final Paint mPaintTextLabels;
    private final Paint mPaintTextData;
    private final Paint mPaintRadar;
    private final GradientDrawable mFriendsRect;
    private Friend mSelectedFriend = null;
    private int mTopLeftArrow;
    private int mLeftLeftArrow;
    private int mTopRightArrow;
    private int mLeftRightArrow;


    public ARView(Context context) {
		super(context);
		
		if (IpokiMain.mFriends != null && IpokiMain.mFriends.length > 0)
			mSelectedFriend = IpokiMain.mFriends[0];
	    // Ipokito bitmap.
        mIpokito = context.getResources().getDrawable(R.drawable.ipokito_friend);
        mSelectedIpokito = context.getResources().getDrawable(R.drawable.ipokito_selected);
        mLeftArrow = context.getResources().getDrawable(R.drawable.arrow_left);
        mRightArrow = context.getResources().getDrawable(R.drawable.arrow_right);
        mIpokitoSemiHeight = mIpokito.getIntrinsicHeight() / 2;
        mIpokitoSemiWidth = mIpokito.getIntrinsicWidth() / 2;
        mSelectedIpokitoSemiHeight = mSelectedIpokito.getIntrinsicHeight() / 2;
        mSelectedIpokitoSemiWidth = mSelectedIpokito.getIntrinsicWidth() / 2;
        
        mFriendsRect = new GradientDrawable();
        mFriendsRect.setShape(GradientDrawable.RECTANGLE);
        mFriendsRect.setStroke(3, Color.WHITE);
        mFriendsRect.setCornerRadius(48);
        mFriendsRect.setAlpha(0x80);
        
        mPaintTextUsername = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintTextUsername.setTextSize(24);
        mPaintTextUsername.setTextAlign(Paint.Align.LEFT);
        mPaintTextUsername.setColor(Color.LTGRAY);
        mPaintTextUsername.setTypeface(Typeface.create((Typeface)null, Typeface.BOLD));

        mPaintTextLabels = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintTextLabels.setTextSize(10);
        mPaintTextLabels.setTextAlign(Paint.Align.LEFT);
        mPaintTextLabels.setColor(Color.LTGRAY);
        mPaintTextLabels.setTypeface(Typeface.create((Typeface)null, Typeface.BOLD));

        mPaintTextData = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintTextData.setTextSize(10);
        mPaintTextData.setTextAlign(Paint.Align.LEFT);
        mPaintTextData.setColor(Color.LTGRAY);
        
        mPaintRadar = new Paint();
        mPaintRadar.setStyle(Paint.Style.STROKE);
        mPaintRadar.setColor(Color.LTGRAY);
        mPaintRadar.setAlpha(255);
    }
    
    protected void onDraw(Canvas canvas) {
    	super.onDraw(canvas);
    	
        SensorManager.getRotationMatrix(rotationMatrix, null, mAccelerometerValues, mMagneticValues);
        SensorManager.getOrientation(rotationMatrix, mOrientationValues);
        
        double direction = mOrientationValues[0] + mPiDiv2;

        canvas.drawCircle(50, 50, 45, mPaintRadar);
        canvas.drawLine(50, 50, (float)(50 + 45 * Math.cos(direction + mPiDiv6)), (float)(50 + 45 * Math.sin(direction + mPiDiv6)), mPaintRadar);
        canvas.drawLine(50, 50, (float)(50 + 45 * Math.cos(direction - mPiDiv6)), (float)(50 + 45 * Math.sin(direction - mPiDiv6)), mPaintRadar);
        
    	Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.YELLOW);
        paint.setTextSize(20);

        int x;
        int y = 20;
        int canvasHeight = canvas.getHeight();
        int canvasWidth = canvas.getWidth();
        Drawable icon;
        int ipokitoSemiWidth;
        int ipokitoSemiHeight;
        
        if (IpokiMain.mFriends != null) {
	        for(Friend f: IpokiMain.mFriends) {
	        	double d[] = f.getDistanceBearing();
	        	if (f.isSelected) {
	        		icon = mSelectedIpokito;
	        		ipokitoSemiWidth = mSelectedIpokitoSemiWidth;
	        		ipokitoSemiHeight = mSelectedIpokitoSemiHeight;
	        	}
	        	else {
	        		icon = mIpokito;
	        		ipokitoSemiWidth = mIpokitoSemiWidth;
	        		ipokitoSemiHeight = mIpokitoSemiHeight;
	        	}

	        	/* With remapping, precision gets worse. Instead of remapping, we change axis by hand to take into account landscape mode
	        	 * We add 90º to azimuth (when the camera points to the north, the top of the phone points to the west) */
	        	double angDiff = getAngleDiff(direction, d[1]);
	        	x = (int) ((0.5 - 2 * angDiff / 3) * canvasWidth);
	        	// We take roll instead of pitch, since the phone is rotated
	            y = (int) (-1 * (mOrientationValues[2] / m2PiDiv3  + 0.25) * canvasHeight);
	            icon.setBounds(x - ipokitoSemiWidth, y - ipokitoSemiHeight, x + mIpokitoSemiWidth, y + mIpokitoSemiHeight);
	            icon.draw(canvas);
	            f.setScreenPos(x, y);
	        }
    	}    
        
	    mTopLeftArrow = canvasHeight - 84;
	    mLeftLeftArrow = 36;
	    mTopRightArrow = canvasHeight - 84;
	    mLeftRightArrow = canvasWidth - 84;

        mFriendsRect.setBounds(5, canvasHeight - 115, canvasWidth - 5, canvasHeight - 5);
        mFriendsRect.draw(canvas);
        
        mLeftArrow.setBounds(36, canvasHeight - 84, 84, canvasHeight - 36);
        mLeftArrow.draw(canvas);

        mRightArrow.setBounds(canvasWidth - 84, canvasHeight - 84, canvasWidth - 36, canvasHeight - 36);
        mRightArrow.draw(canvas);

        if (mSelectedFriend != null) {
        	Drawable d = mSelectedFriend.mPicture;
        	if (d != null) {
	        	d.setBounds(110, canvasHeight - 100, 190, canvasHeight - 20);
	        	d.draw(canvas);
        	}
        	try {
        	canvas.drawText(mSelectedFriend.mName, 210, canvasHeight - 90, mPaintTextUsername);
        	canvas.drawText("Distance (km):", 210, canvasHeight - 72, mPaintTextLabels);
        	canvas.drawText(String.format("%.1f", mSelectedFriend.mDistance), 280, canvasHeight - 72, mPaintTextData);
        	canvas.drawText("Location date:", 210, canvasHeight - 58, mPaintTextLabels);
        	DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
        	canvas.drawText(df.format(mSelectedFriend.mLocationDate), 280, canvasHeight - 58, mPaintTextData);
        	canvas.drawText("Location:", 210, canvasHeight - 44, mPaintTextLabels);
        	canvas.drawText(mSelectedFriend.mAddress.getCountryName(), 280, canvasHeight - 44, mPaintTextData);
        	String address = mSelectedFriend.mAddress.getAddressLine(0);
        	if (address != null)
        		canvas.drawText(address, 210, canvasHeight - 30, mPaintTextData);
        	String locality = mSelectedFriend.mAddress.getLocality();
        	if (locality != null)
        		canvas.drawText(locality, 210, canvasHeight - 16, mPaintTextData);
        	}
        	catch(NullPointerException npx) {
        		Log.e("IPOKI", npx.toString());
        	}
        }
    }

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
	       synchronized (this) {
	        	switch(event.sensor.getType()) {
	        	case Sensor.TYPE_ACCELEROMETER:
		            for (int i=0 ; i<3 ; i++) {
		                mAccelerometerValues[i] = event.values[i];
		            }
		            break;
	        	case Sensor.TYPE_MAGNETIC_FIELD:
		            for (int i=0 ; i<3 ; i++) {
		                mMagneticValues[i] = event.values[i];
		            }
		            break;
		        default:
		        	break;
	        	}
	       }
	       invalidate();
	}

	@Override
    public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			float x = event.getX();
			float y = event.getY();
			int distance = 20;
			
			if (Math.abs(mTopLeftArrow + 24 - y) < 20 && Math.abs(mLeftLeftArrow + 24 - x) < 20) {
				Log.i("Ipoki", "LeftArrow pressed");
				Log.i("Ipoki", "Friend: " + mSelectedFriend.mName + " - " + String.format("%f", mSelectedFriend.mBearing));
				Log.i("Ipoki", "Next Friend: " + mSelectedFriend.mPrevious.mName + " - " + String.format("%f", mSelectedFriend.mPrevious.mBearing));
				mSelectedFriend.isSelected = false;
				mSelectedFriend = mSelectedFriend.mPrevious;
				mSelectedFriend.isSelected = true;
				return true;
			}
			else if (Math.abs(mTopRightArrow + 24 - y) < 20 && Math.abs(mLeftRightArrow + 24 - x) < 20){
				Log.i("Ipoki", "RightArrow pressed");
				Log.i("Ipoki", "Friend: " + mSelectedFriend.mName + " - " + String.format("%f", mSelectedFriend.mBearing));
				Log.i("Ipoki", "Next Friend: " + mSelectedFriend.mNext.mName + " - " + String.format("%f", mSelectedFriend.mNext.mBearing));
				mSelectedFriend.isSelected = false;
				mSelectedFriend = mSelectedFriend.mNext;
				mSelectedFriend.isSelected = true;
				return true;
			}
			for (Friend f: IpokiMain.mFriends) {
				int friendDis = f.getDistanceFromScreenPoint(x, y);
				if (friendDis < distance) {
					distance = friendDis;
					mSelectedFriend.isSelected = false;
					mSelectedFriend = f;
					mSelectedFriend.isSelected = true;
				}
			}
		}
		return true;
	}

	private double getAngleDiff(double ang1, double ang2) {
		double result = ang1 - ang2;
		if (result >=  Math.PI)
			result -= 2 * Math.PI;
		else if (result <=  -Math.PI)
			result += 2 * Math.PI;
		
		return result;
	}
	
}
