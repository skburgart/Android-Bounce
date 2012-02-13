package com.stevekb.sandbox;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;

public class SandboxActivity extends Activity implements SensorEventListener{
	
	private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    PhysicsSurface mySurface;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mySurface = (PhysicsSurface) findViewById(R.id.mySurface);
		
		mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
 	}
	
	public void clearCircles(View v) {
		mySurface.clearCircles();
	}
	
	protected void onResume() {
        super.onResume();
        mySurface.setThreadRunning(true);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    }
	
	public void onPause() {
        super.onPause();
        mySurface.setThreadRunning(false);
        mSensorManager.unregisterListener(this);
    }

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	public void onSensorChanged(SensorEvent event) {
		float result[] = event.values;
		
		mySurface.setGravity(-(result[0]/10) * Math.abs(result[2])/7, (result[1]/10) * Math.abs(result[2])/7);

	}
}