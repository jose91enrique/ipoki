package com.ipoki.android;

import java.util.List;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;

public class IpokiAR extends Activity {
	private CameraView mCameraView;
	private ARView mARView;
    private SensorManager mSensorManager;

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
    }
}
