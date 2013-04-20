package com.stevekb.bounce;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    PhysicsSurface mySurface;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mySurface = (PhysicsSurface) findViewById(R.id.mySurface);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        firstStartMessage();
    }

    public void firstStartMessage() {
        SharedPreferences settings = getSharedPreferences("bounce_first_launch", MODE_PRIVATE);

        if (settings.getBoolean("firstLaunch", true)) {
            AlertDialog alert = new AlertDialog.Builder(this).create();
            alert.setMessage(getResources().getString(R.string.first_run_text));
            alert.setTitle(getResources().getString(R.string.first_run_title));
            alert.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.first_run_confirm), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            alert.show();

            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("firstLaunch", false);
            editor.commit();
        }
    }

    public void clearCircles(View v) {
        mySurface.clearCircles();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }

    public void onSensorChanged(SensorEvent event) {
        float result[] = event.values;

        float gDamp = 1 - (Math.abs(result[2]) / 10);

        float gx = -(result[0] / 10) * gDamp;
        float gy = (result[1] / 10) * gDamp;

        mySurface.setGravity(gx, gy);
    }
}