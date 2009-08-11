package com.ipoki.android;

import java.util.List;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;

public class Ipoki extends Activity {

    private SensorManager mSensorManager;
	private CameraView mCameraView;
	private ARView mARView;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);


        mCameraView = new CameraView(this);
        setContentView(mCameraView);
        
        mARView = new ARView(this);
        addContentView(mARView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    
        List<Sensor> listSensors = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (listSensors.size() > 0)
        {
        	mSensorManager.registerListener(mARView, listSensors.get(0), SensorManager.SENSOR_DELAY_UI);
        }

        listSensors = mSensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
        if (listSensors.size() > 0)
        {
        	mSensorManager.registerListener(mARView, listSensors.get(0), SensorManager.SENSOR_DELAY_UI);
        }
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
}
