package com.ipoki.android;

import android.content.Context;
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
