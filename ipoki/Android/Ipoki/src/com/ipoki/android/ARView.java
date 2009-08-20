package com.ipoki.android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;

public class ARView extends View implements SensorEventListener {
    private float   mAccelerometerValues[] = new float[3];
    private float   mMagneticValues[] = new float[3];
    private float rotationMatrix[] = new float[16];
    private float remappedRotationMatrix[] = new float[16];
    private float mOrientationValues[] = new float[3];
    final double m2PiDiv3 = 2 * Math.PI / 3;
    final double m5PiDiv6 = 5 * Math.PI / 6;
    final double mPiDiv4 = Math.PI / 4;

    public ARView(Context context) {
		super(context);
	}
    
    protected void onDraw(Canvas canvas) {
    	super.onDraw(canvas);
    	
        SensorManager.getRotationMatrix(rotationMatrix, null, mAccelerometerValues, mMagneticValues);
        SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, remappedRotationMatrix);
        SensorManager.getOrientation(remappedRotationMatrix, mOrientationValues);

    	Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.YELLOW);
        paint.setTextSize(20);

        float x;
        float y = 20;
        int canvasHeight = canvas.getHeight();
        int canvasWidth = canvas.getWidth();
/*        if (Ipoki.mFriends != null) {
	        for(Friend f: Ipoki.mFriends) {
	        	double d[] = f.getDistanceBearing();
	            canvas.drawText("bearing " + String.valueOf(d[1]), 160, y, paint);
	            y += 20;
	        }
    	}    */
        if (Ipoki.mFriends != null) {
        	Friend f = Ipoki.mFriends[0];
        	double d[] = f.getDistanceBearing();
        	double sinDiffAng = Math.sin(mOrientationValues[0] - d[1]);
        	x = (float) ((0.5 - 2 * sinDiffAng / 3) * canvasWidth);
        	//x = (float) ((mOrientationValues[0] + d[1]) / mPiDiv4 * canvasWidth / 2 + (canvasWidth / 2));
        	canvas.drawText("or2: " + String.valueOf(mOrientationValues[2]), 160, 20, paint);
        	canvas.drawText("or0: " + String.valueOf(mOrientationValues[0]), 160, 40, paint);
        	canvas.drawText("or1: " + String.valueOf(mOrientationValues[1]), 160, 60, paint);
        	canvas.drawText("diffAng: " + String.valueOf(mOrientationValues[0] - d[1]), 160, 80, paint);
        	canvas.drawText("sinDiffAng: " + String.valueOf(sinDiffAng), 160, 100, paint);
        	canvas.drawText("x: " + String.valueOf(x), 160, 120, paint);
            y = (float) (-1 * (mOrientationValues[1] / m2PiDiv3  + 0.25) * canvasHeight);
        	canvas.drawText("y: " + String.valueOf(y), 160, 140, paint);
            canvas.drawLine(x - 10, y, x + 10, y, paint);
        }
        //canvas.drawText("y: " + String.valueOf(y), 160, 80, paint);
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

}
