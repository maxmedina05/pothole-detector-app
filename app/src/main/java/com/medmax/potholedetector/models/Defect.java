package com.medmax.potholedetector.models;

import java.util.Locale;

/**
 * Created by Max Medina on 2017-07-11.
 */

public class Defect {

    private int Id = 0;

    // one window stats
    private double onexMean    = 0;
    private double oneyMean    = 0;
    private double onezMean    = 0;
    private double onexStd     = 0;
    private double oneyStd     = 0;
    private double onezStd     = 0;

    // window stats
    private double xMean    = 0;
    private double yMean    = 0;
    private double zMean    = 0;
    private double xStd     = 0;
    private double yStd     = 0;
    private double zStd     = 0;

    // small window stats
    private double sm_xMean = 0;
    private double sm_yMean = 0;
    private double sm_zMean = 0;
    private double sm_xStd  = 0;
    private double sm_yStd  = 0;
    private double sm_zStd  = 0;
    private double sm_zMax  = 0;

    private float startTime = 0;
    private float endTime = 0;

    private float latitude = 0;
    private float longitude = 0;

    private int classType = 0;

    public Defect() {

    }

    public Defect(int id, int classType) {
        Id = id;
        this.classType = classType;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public double getxMean() {
        return xMean;
    }

    public void setxMean(double xMean) {
        this.xMean = xMean;
    }

    public double getyMean() {
        return yMean;
    }

    public void setyMean(double yMean) {
        this.yMean = yMean;
    }

    public double getzMean() {
        return zMean;
    }

    public void setzMean(double zMean) {
        this.zMean = zMean;
    }

    public double getxStd() {
        return xStd;
    }

    public void setxStd(double xStd) {
        this.xStd = xStd;
    }

    public double getyStd() {
        return yStd;
    }

    public void setyStd(double yStd) {
        this.yStd = yStd;
    }

    public double getzStd() {
        return zStd;
    }

    public void setzStd(double zStd) {
        this.zStd = zStd;
    }

    public double getSm_xMean() {
        return sm_xMean;
    }

    public void setSm_xMean(double sm_xMean) {
        this.sm_xMean = sm_xMean;
    }

    public double getSm_yMean() {
        return sm_yMean;
    }

    public void setSm_yMean(double sm_yMean) {
        this.sm_yMean = sm_yMean;
    }

    public double getSm_zMean() {
        return sm_zMean;
    }

    public void setSm_zMean(double sm_zMean) {
        this.sm_zMean = sm_zMean;
    }

    public double getSm_xStd() {
        return sm_xStd;
    }

    public void setSm_xStd(double sm_xStd) {
        this.sm_xStd = sm_xStd;
    }

    public double getSm_yStd() {
        return sm_yStd;
    }

    public void setSm_yStd(double sm_yStd) {
        this.sm_yStd = sm_yStd;
    }

    public double getSm_zStd() {
        return sm_zStd;
    }

    public void setSm_zStd(double sm_zStd) {
        this.sm_zStd = sm_zStd;
    }

    public int getClassType() {
        return classType;
    }

    public void setClassType(int classType) {
        this.classType = classType;
    }

    public float getStartTime() {
        return startTime;
    }

    public void setStartTime(float startTime) {
        this.startTime = startTime;
    }

    public float getEndTime() {
        return endTime;
    }

    public void setEndTime(float endTime) {
        this.endTime = endTime;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public double getOnexMean() {
        return onexMean;
    }

    public void setOnexMean(double onexMean) {
        this.onexMean = onexMean;
    }

    public double getOneyMean() {
        return oneyMean;
    }

    public void setOneyMean(double oneyMean) {
        this.oneyMean = oneyMean;
    }

    public double getOnezMean() {
        return onezMean;
    }

    public void setOnezMean(double onezMean) {
        this.onezMean = onezMean;
    }

    public double getOnexStd() {
        return onexStd;
    }

    public void setOnexStd(double onexStd) {
        this.onexStd = onexStd;
    }

    public double getOneyStd() {
        return oneyStd;
    }

    public void setOneyStd(double oneyStd) {
        this.oneyStd = oneyStd;
    }

    public double getOnezStd() {
        return onezStd;
    }

    public void setOnezStd(double onezStd) {
        this.onezStd = onezStd;
    }

    public String getCSVPrint() {
        return String.format(Locale.US, "%d,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%d",
                Id,
                onexMean,
                onexStd,
                oneyMean,
                oneyStd,
                onezMean,
                onezStd,

                xMean,
                xStd,
                yMean,
                yStd,
                zMean,
                zStd,

                sm_xMean,
                sm_xStd,
                sm_yMean,
                sm_yStd,
                sm_zMean,
                sm_zStd,
                sm_zMax,

                startTime,
                endTime,

                latitude,
                longitude,

                classType
                );
    }

    public double getSm_zMax() {
        return sm_zMax;
    }

    public void setSm_zMax(double sm_zMax) {
        this.sm_zMax = sm_zMax;
    }

    public static class ClassType {
        public static final int NOTHING = 0;
        public static final int POTHOLE = 1;
        public static final int SPEED_BUMP = 2;
        public static final int DEFECT = 3;
    }

    public static class Axis {
        public static final int AXIS_X = 0;
        public static final int AXIS_Y = 1;
        public static final int AXIS_Z = 2;
    }
}
