package com.example.yuras.thirdapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class SensorsActivity extends Activity implements SensorEventListener {
    SensorManager sensorManager;

    private final float[] mAccelerometerReading = new float[3];
    private final float[] mMagnetometerReading = new float[3];

    private final float[] mRotationMatrix = new float[9];
    private final float[] mOrientationAngles = new float[3];

    List<String> mSensorsData;
    ListAdapter adapter;

    ListView sensors_lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors);

        sensors_lv = (ListView) findViewById(R.id.sensors_lv);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        /*List<Sensor> deviceSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        List<String> sensors = new ArrayList<>();

        for (Sensor sensor : deviceSensors) {
            sensors.add(sensor.getName());
        }

        ListAdapter adapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_list_item_1, sensors);

        sensors_lv.setAdapter(adapter);*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();

        sensorManager.unregisterListener(this);
    }

    public void gotoWifi(View view) {
        finish();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){}

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, mAccelerometerReading,
                    0, mAccelerometerReading.length);
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, mMagnetometerReading,
                    0, mMagnetometerReading.length);
        }

        updateOrientationAngles();

        double azimuth = 180 * mOrientationAngles[0] / Math.PI;
        double pitch = 180 * mOrientationAngles[1] / Math.PI;
        double roll = 180 * mOrientationAngles[2] / Math.PI;

        mSensorsData = new ArrayList<>();
        mSensorsData.add("Azimuth: " + String.valueOf(azimuth));
        mSensorsData.add("Pitch: " + String.valueOf(pitch));
        mSensorsData.add("Roll: " + String.valueOf(roll));

        ListAdapter adapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_list_item_1, mSensorsData);

        sensors_lv.setAdapter(adapter);
    }

    public void updateOrientationAngles() {
        SensorManager.getRotationMatrix(mRotationMatrix, null,
                mAccelerometerReading, mMagnetometerReading);

        SensorManager.getOrientation(mRotationMatrix, mOrientationAngles);
    }
}
