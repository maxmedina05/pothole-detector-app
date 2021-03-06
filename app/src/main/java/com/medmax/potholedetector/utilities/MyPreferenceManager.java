package com.medmax.potholedetector.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import com.medmax.potholedetector.R;
import com.medmax.potholedetector.config.AppSettings;

/**
 * Created by Max Medina on 2017-09-03.
 */

public class MyPreferenceManager {
    private static MyPreferenceManager _instance;

    private float winSize = 1.0f;
    private float smWinSize = 0.1f;
    private float K = 3.0f;
    private float zStdThresh = 0.19f;
    private float xStdThresh = 0.10f;
    private float coolDownTime = AppSettings.COOLDOWN_TIME;

    // Debugger
    private boolean isDebuggerOn = false;

    private MyPreferenceManager() {

    }

    public static MyPreferenceManager getInstance() {
        if(_instance == null) {
            _instance = new MyPreferenceManager();
        }

        return _instance;
    }

    public void loadPreferenceParameters(Resources resources, Context context) {
        SharedPreferences sharedPrefs = android.preference.PreferenceManager
                .getDefaultSharedPreferences(context);

        String key = resources.getString(R.string.pref_window_size);
        winSize = Float.parseFloat(sharedPrefs.getString(key, "1"));

        key = resources.getString(R.string.pref_small_window_size);
        smWinSize = Float.parseFloat(sharedPrefs.getString(key, "0.1"));

        key = resources.getString(R.string.pref_k_value);
        K = Integer.parseInt(sharedPrefs.getString(key, "3"));

        key = resources.getString(R.string.pref_std_thresh);
        zStdThresh = Float.parseFloat(sharedPrefs.getString(key, "0.19"));

        key = resources.getString(R.string.pref_x_std_thresh);
        xStdThresh = Float.parseFloat(sharedPrefs.getString(key, "0.10"));

        key = resources.getString(R.string.pref_debugger_mode);
        isDebuggerOn = sharedPrefs.getBoolean(key, false);

        key = resources.getString(R.string.pref_cooldown_time);
        coolDownTime = Float.parseFloat(sharedPrefs.getString(key, String.valueOf(AppSettings.COOLDOWN_TIME)));
    }

    public float getWinSize() {
        return winSize;
    }

    public void setWinSize(float winSize) {
        this.winSize = winSize;
    }

    public float getSmWinSize() {
        return smWinSize;
    }

    public void setSmWinSize(float smWinSize) {
        this.smWinSize = smWinSize;
    }

    public float getK() {
        return K;
    }

    public void setK(float k) {
        K = k;
    }

    public float getzStdThresh() {
        return zStdThresh;
    }

    public void setzStdThresh(float zStdThresh) {
        this.zStdThresh = zStdThresh;
    }

    public float getxStdThresh() {
        return xStdThresh;
    }

    public void setxStdThresh(float xStdThresh) {
        this.xStdThresh = xStdThresh;
    }

    public float getCoolDownTime() {
        return coolDownTime;
    }

    public void setCoolDownTime(float coolDownTime) {
        this.coolDownTime = coolDownTime;
    }

    public boolean isDebuggerOn() {
        return isDebuggerOn;
    }

    public void setDebuggerOn(boolean debuggerOn) {
        isDebuggerOn = debuggerOn;
    }
}
