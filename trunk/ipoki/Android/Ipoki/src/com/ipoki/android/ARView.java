package com.ipoki.android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.view.View;

public class ARView extends View implements SensorEventListener {
	public float   mAccelerometerValues[] = new float[3];
    public float   mMagneticValues[] = new float[3];

    public ARView(Context context) {
		super(context);
	}
    
    protected void onDraw(Canvas canvas) {
    	super.onDraw(canvas);

    	Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.YELLOW);
        paint.setTextSize(20);

        canvas.drawText("Ipoki for Android", 160, 100, paint);
        int y = 20;
        int canvasHeight = canvas.getHeight();
        for(Friend f: Ipoki.mFriends) {
        	double d[] = f.getDistanceBearing();
        	canvas.drawText(f.mName + " : " + Double.toString(d[0]) + " - " + Double.toString(d[1]), 20, y, paint);
        	y += 20;
        	if (y > canvasHeight - 20)
        		break;
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
	        		mAccelerometerValues = event.values.clone();
		            break;
	        	case Sensor.TYPE_MAGNETIC_FIELD:
	        		mMagneticValues = event.values.clone();
		            break;
		        default:
		        	break;
	        	}
	       }
	}

}
