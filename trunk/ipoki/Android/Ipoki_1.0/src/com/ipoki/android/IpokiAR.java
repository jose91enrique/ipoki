package com.ipoki.android;

import java.util.List;
import android.app.Activity;
import android.app.Dialog;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class IpokiAR extends Activity implements SeekBar.OnSeekBarChangeListener {
	private CameraView mCameraView;
	private ARView mARView;
    private SensorManager mSensorManager;
    public static final int DIALOG_RANGE = 0;
    private TextView mRangeText;

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
    
    protected Dialog onCreateDialog(int id) {
        Dialog dialog;
        switch(id) {
        case DIALOG_RANGE:
			dialog = new Dialog(this);
			dialog.setTitle("Friends range");
			dialog.setContentView(R.layout.range_friends);
			Button button = (Button) dialog.findViewById(R.id.ButtonOK);
			button.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	if (IpokiMain.mFriendsDownloaded) {
		            	Friend.getFriendsInDistance();
		     	    	if (Friend.mFriendsInDistance.length > 0) {
		     	    		if (ARView.mSelectedFriend != null)
		     	    			ARView.mSelectedFriend.isSelected = false;
		     	    		ARView.mSelectedFriend = Friend.mFriendsInDistance[0];
		     	    	}
		     	    	else
		     	    		ARView.mSelectedFriend = null;
	            	}
	                dismissDialog(DIALOG_RANGE);
	            }
	        });            
			SeekBar seekBar = (SeekBar)dialog.findViewById(R.id.FriendsRange);
			seekBar.setOnSeekBarChangeListener(this);
			mRangeText = (TextView) dialog.findViewById(R.id.RangeText);
			mRangeText.setText(Friend.mFriendsDistance + " km");
			break;
        default:
            dialog = null;
        }
        return dialog;
    }

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		mRangeText.setText(progress + " km");
		Friend.mFriendsDistance = progress;
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}
}
