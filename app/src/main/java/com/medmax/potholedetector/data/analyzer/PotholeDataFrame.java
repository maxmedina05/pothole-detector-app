package com.medmax.potholedetector.data.analyzer;

import com.medmax.potholedetector.models.AccData;
import com.medmax.potholedetector.models.Defect;
import com.medmax.potholedetector.utilities.MathHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Max Medina on 2017-07-08.
 *
 * Description: Record only 3 seconds of data
 * when it has 3 second of data delete the last second.
 *
 */

public class PotholeDataFrame {
    public static final int MAX_TIME_RECORDED = 2;
    public static final int LAST_SECOND_TIME = 1;
    private List<AccData> dataframe;
    private List<AccData> lastdf;
    private double mStartTime = 0;
    private double mMean = 0;
    private boolean isMeanCalculated = false;

    public PotholeDataFrame() {
        dataframe = new ArrayList<>();
        lastdf = new ArrayList<>();

    }


    public PotholeDataFrame(List<AccData> dataframe) {
        this.dataframe = dataframe;
        lastdf = new ArrayList<>();
    }

    private static int binarySearch(List<AccData> arr, int l, int r, double x) {
        if(r >= l) {
            int mid = l + (r - l) / 2;
            double timestamp = MathHelper.round(arr.get(mid).getTimestamp(), 4);

            if(timestamp == x)
                return mid;

            if(timestamp > x)
                return binarySearch(arr, l, mid-1, x);
            return binarySearch(arr, mid+1, r, x);
        }

        return 0;
    }

    public PotholeDataFrame bsQuery(double begin, double end){
        begin = MathHelper.round(begin, 4);
        end = MathHelper.round(end, 4);

        List<AccData> df = new ArrayList<>();
        int start_idx = binarySearch(dataframe, 0, dataframe.size() - 1, begin);

        for (int i = start_idx; i < dataframe.size(); i++) {
            AccData row = dataframe.get(i);
            double timestamp = MathHelper.round(row.getTimestamp(), 4);

            if(timestamp >= begin && timestamp <= end) {
                df.add(row);
            }

            if(timestamp >= end) {
                break;
            }
        }
        return new PotholeDataFrame(df);
    }

    public PotholeDataFrame query(double begin, double end){
        List<AccData> df = new ArrayList<>();

        for (AccData row : dataframe) {
            if(row.getTimestamp() >= begin && row.getTimestamp() <= end) {
                df.add(row);
            }

            if(row.getTimestamp() >= end) {
                break;
            }
        }

        return new PotholeDataFrame(df);
    }

    public double computeMean(){
        if(isMeanCalculated) {
            return mMean;
        }

        double sum = 0;
        double mean = 0;
        double n = dataframe.size();

        for (AccData x : dataframe) {
            sum += x.getzAxis();
        }
        mMean = mean = sum / n;
        isMeanCalculated = true;
        return mean;
    }

    public double computeMean(int axis){
        double sum = 0;
        double mean = 0;
        double n = dataframe.size();

        for (AccData row : dataframe) {
            double x = getAxis(row, axis);
            sum += x;
        }
        mMean = mean = sum / n;
        isMeanCalculated = true;
        return mean;
    }

    private double getAxis(AccData row, int axis) {
        double value = 0;

        switch (axis) {
            case Defect.Axis.AXIS_X:
                value = row.getxAxis();
                break;
            case Defect.Axis.AXIS_Y:
                value = row.getyAxis();
                break;
            case Defect.Axis.AXIS_Z:
                value = row.getzAxis();
                break;
        }

        return value;
    }

    public double computeStd(){
        double sum = 0;
        double std = 0;
        double mean = computeMean();
        double n = dataframe.size();

        for (AccData acd : dataframe) {
            double x = acd.getzAxis();

            sum += (x - mean)*(x - mean);
        }

        std = Math.sqrt(sum / (n - 1));
        return std;
    }

    public double computeStd(int axis, double mean){
        double sum = 0;
        double std = 0;
        double n = dataframe.size();

        for (AccData row : dataframe) {
            double x = getAxis(row, axis);
            sum += (x - mean)*(x - mean);
        }

        std = Math.sqrt(sum / (n - 1));
        return std;
    }

    public double computeMax(){
        double max = dataframe.get(0).getzAxis();

        for (AccData acd : dataframe) {
            double x = acd.getzAxis();
            if(x > max) {
                max = x;
            }
        }
        return max;
    }

    /**
     * Add a new row but with conditions
     * @param row
     * it will only record 3 seconds when 3 seconds has been reached it will replace the list with
     * the last 1 recorded.
     * steps:
     *      1. check if it's empty if it is then that's the first record and set the start time.
     *      2. verify if 3 seconds has pass
     *          if yes: reset the dataframe; dataframe = lastdf
     *          if no: check if 2 seconds has pass
     *              if yes: add to the lastdf
     */
    public void addRow(AccData row) {
        dataframe.add(row);
        isMeanCalculated = false;

        double currentTime = row.getTimestamp();
        if(dataframe.size() == 1) {
            mStartTime = row.getTimestamp();
        }

        // It's almost full it has recorded at least 2 seconds
        if((currentTime - mStartTime) > (MAX_TIME_RECORDED - LAST_SECOND_TIME)) {
            lastdf.add(row);
        }

        // MAX_TIME_RECORDED REACHED!
        if((currentTime - mStartTime) >= MAX_TIME_RECORDED) {
            dataframe.clear();
            // dataframe.addAll(lastdf);
            dataframe = lastdf;
            lastdf = new ArrayList<>();

            // reset time
            mStartTime = currentTime;
        }

    }
}
