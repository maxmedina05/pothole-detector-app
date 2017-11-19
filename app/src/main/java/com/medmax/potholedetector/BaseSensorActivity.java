package com.medmax.potholedetector;

import android.app.Activity;
import android.content.Context;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.medmax.potholedetector.config.AppSettings;
import com.medmax.potholedetector.services.GPSManager;
import com.medmax.potholedetector.services.OnGPSUpdateListener;
import com.medmax.potholedetector.utilities.FrequencyCalculator;
import com.medmax.potholedetector.utilities.MyPreferenceManager;

import java.util.Locale;

/**
 * Created by Max Medina on 2017-07-06.
 */

public abstract class BaseSensorActivity extends Activity implements View.OnClickListener, SensorEventListener, OnGPSUpdateListener {

    // Constants
    public static final String LOG_TAG = BaseSensorActivity.class.getSimpleName();

    // Variables
    protected char mGravityAxis = 'Y';
    protected float mTimestamp = 0;
    protected String mDeviceName = "";
    protected String defectFoundMsg = "";
    private FrequencyCalculator mFrequencyCalculator;

    // Sensor Variables
    protected volatile float[] mRawAccelerometerValues = new float[3];
    protected volatile float[] mRawMagneticValues = new float[3];
    protected float mCarMovingSpeed = 0;
    protected float lastLongitude = 0;
    protected float lastLatitude = 0;
    protected Location previousLocation = null;

    private GPSManager mGPSManager;
    protected SensorManager mSensorManager;
    private Sensor mAccelerometerSensor;

    // Preferences
    protected MyPreferenceManager mPreferenceManager;

    // UI Components
    protected TextView tvTimestamp;
    protected TextView tvFrequency;
    protected TextView tvAxisX;
    protected TextView tvAxisY;
    protected TextView tvAxisZ;
    protected TextView tvSpeed;
    protected TextView tvGravity;

    protected ToggleButton btnLog;

    // Multi thread
    private Handler mHandler;
    private Runnable mRunnable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        defectFoundMsg = getString(R.string.defect_found);
        mPreferenceManager = MyPreferenceManager.getInstance();
        mPreferenceManager.loadPreferenceParameters(getResources(), this);
        setUILayout();
        setupUIComponents();
        setupSensors();
        setupUIUpdateThread();

        mGPSManager = new GPSManager(this);
        mGPSManager.setOnGPSUpdateListener(this);

        mDeviceName = Build.MANUFACTURER + " " + Build.MODEL;
        mFrequencyCalculator = new FrequencyCalculator();
    }

    // Device Methods
    @Override
    protected void onResume() {
        super.onResume();
        mFrequencyCalculator.reset();
        mSensorManager.registerListener(this, mAccelerometerSensor, AppSettings.SAMPLING_RATE);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), AppSettings.SAMPLING_RATE);
        mGPSManager.requestLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        btnLog.setChecked(false);
        mSensorManager.unregisterListener(this);
        mGPSManager.removeLocationUpdates();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                onAccelerometerSensorChanged(event.values);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                onMagneticSensorChanged(event.values);
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onGPSUpdate(Location location) {
        if (location.hasSpeed()) {
            mCarMovingSpeed = location.getSpeed() * AppSettings.SPEED_CONSTANT;
        }
        lastLatitude = (float) location.getLatitude();
        lastLongitude = (float) location.getLongitude();
        previousLocation = location;
    }


    // Sensor Methods
    protected void setUILayout() {
        setContentView(R.layout.activity_sensor);
    }

    private void setupUIUpdateThread() {
        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRunnable = this;
                updateUI();
                mHandler.postDelayed(mRunnable, AppSettings.UPDATE_UI_DELAY);
            }
        }, AppSettings.UPDATE_UI_DELAY);
    }

    private void setupUIComponents() {
        tvTimestamp = (TextView) findViewById(R.id.tv_timestamp);
        tvSpeed = (TextView) findViewById(R.id.tv_speed);
        tvGravity = (TextView) findViewById(R.id.tv_gravity);
        tvFrequency = (TextView) findViewById(R.id.tv_frequency);
        tvAxisX = (TextView) findViewById(R.id.tv_x_axis);
        tvAxisY = (TextView) findViewById(R.id.tv_y_axis);
        tvAxisZ = (TextView) findViewById(R.id.tv_z_axis);
        btnLog = (ToggleButton) findViewById(R.id.btn_log);

        btnLog.setOnClickListener(this);
    }

    private void setupSensors() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    protected void onAccelerometerSensorChanged(float[] values) {
        mFrequencyCalculator.calculateFrequency();
        System.arraycopy(values, 0, mRawAccelerometerValues, 0, values.length);
    }

    protected void onMagneticSensorChanged(float[] values) {
        System.arraycopy(values, 0, mRawMagneticValues, 0, values.length);

        if(mRawAccelerometerValues != null && mRawMagneticValues != null ){
            float R[] = new float[9];
            float I[] = new float[9];

            if(SensorManager.getRotationMatrix(R, I, mRawAccelerometerValues, mRawMagneticValues)) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);

                float azimut = orientation[0];
                float rotation = (float) (-azimut * 360 / (2 * Math.PI));
                Log.d(LOG_TAG, String.format("rotation: %f", rotation));
            }
        }
    }

    protected void updateUI() {
        tvAxisX.setText(String.format(Locale.US, "x: %.4f", mRawAccelerometerValues[0]));
        tvAxisY.setText(String.format(Locale.US, "y: %.4f", mRawAccelerometerValues[1]));
        tvAxisZ.setText(String.format(Locale.US, "z: %.4f", mRawAccelerometerValues[2]));

        tvFrequency.setText(String.format(Locale.US, "%.1f hz", mFrequencyCalculator.getFqHz()));
        tvTimestamp.setText(String.format(Locale.US, "timestamp: %.3f s", mTimestamp));
        tvSpeed.setText(String.format(Locale.US, "speed: %.2f m/s", mCarMovingSpeed));
        tvGravity.setText(String.format(Locale.US, "gravity: %c", mGravityAxis));
    }

    protected void sendToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }
}