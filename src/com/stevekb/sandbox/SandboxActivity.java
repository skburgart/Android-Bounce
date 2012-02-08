package com.stevekb.sandbox;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SandboxActivity extends Activity implements SensorEventListener{
	
	private SensorManager mSensorManager;
    private Sensor mAccelerometer;
	CustomSurface mySurface;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mySurface = (CustomSurface) findViewById(R.id.mySurface);
		mySurface.startMyLogicThread();
		
		mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
 	}
	
	protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
	
	public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        mySurface.setActive(false);
    }

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	public void onSensorChanged(SensorEvent event) {
		float result[] = event.values;
		
		mySurface.gx = -(result[0]/10) * Math.abs(result[2])/7;
		mySurface.gy = (result[1]/10) * Math.abs(result[2])/7;

	}
}