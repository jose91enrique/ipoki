package com.ipoki.android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;

public class ARView extends View implements SensorEventListener {
    private float   mAccelerometerValues[] = new float[3];
    private float   mMagneticValues[] = new float[3];
    private float rotationMatrix[] = new float[16];
    //private float remappedRotationMatrix[] = new float[16];
    private float mOrientationValues[] = new float[3];
    final double m2PiDiv3 = 2 * Math.PI / 3;
    final double m5PiDiv6 = 5 * Math.PI / 6;
    final double mPiDiv4 = Math.PI / 4;
    final double mPiDiv2 = Math.PI / 2;
    private final Drawable mIpokito;
    private int mIpokitoSemiHeight;
    private int mIpokitoSemiWidth;


    public ARView(Context context) {
		super(context);
        mIpokito = context.getResources().getDrawable(R.drawable.ipokito2);
        mIpokitoSemiHeight = mIpokito.getIntrinsicHeight() / 2;
        mIpokitoSemiWidth = mIpokito.getIntrinsicWidth() / 2;
	}
    
    protected void onDraw(Canvas canvas) {
    	super.onDraw(canvas);
    	
        SensorManager.getRotationMatrix(rotationMatrix, null, mAccelerometerValues, mMagneticValues);
        // With remapping, precision gets worse
        //SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, remappedRotationMatrix);
        //SensorManager.getOrientation(remappedRotationMatrix, mOrientationValues);
        SensorManager.getOrientation(rotationMatrix, mOrientationValues);

    	Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.YELLOW);
        paint.setTextSize(20);

        int x;
        int y = 20;
        int canvasHeight = canvas.getHeight();
        int canvasWidth = canvas.getWidth();
        if (Ipoki.mFriends != null) {
	        for(Friend f: Ipoki.mFriends) {
	        	double d[] = f.getDistanceBearing();

	        	/* With remapping, precision gets worse. Instead of remapping, we change axis by hand to take into account landscape mode
	        	 * We add 90º to azimuth (when the camera points to the north, the top of the phone points to the west) */
	        	double angDiff = getAngleDiff(mOrientationValues[0] + mPiDiv2, d[1]);
	        	x = (int) ((0.5 - 2 * angDiff / 3) * canvasWidth);
	        	// We take roll instead of pitch, since the phone is rotated
	            y = (int) (-1 * (mOrientationValues[2] / m2PiDiv3  + 0.25) * canvasHeight);
	            mIpokito.setBounds(x - mIpokitoSemiWidth, y - mIpokitoSemiHeight, x + mIpokitoSemiWidth, y + mIpokitoSemiHeight);
	            mIpokito.draw(canvas);
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

	private double getAngleDiff(double ang1, double ang2) {
		double result = ang1 - ang2;
		if (result >=  Math.PI)
			result -= 2 * Math.PI;
		else if (result <=  -Math.PI)
			result += 2 * Math.PI;
		
		return result;
	}
	
}
