package com.sdesimeur.android.gpsfiction.helpers;

import java.text.NumberFormat;

/**
 * Created by sam on 28/10/16.
 */
public class DistanceToTextHelper {
    float distance;
    public DistanceToTextHelper (float d) {
        distance=d;
    }
    public void setDistanceInM(float d) {
        distance=d/1000;
    }
    public void setDistanceInKM(float d) {
        distance=d;
    }
    public String getDistanceInText () {
        float d=0;
        String distanceText = "";
        if (distance >= 10) {
            d = (float) ((Math.ceil(distance * 10)) / 10);
            distanceText = NumberFormat.getInstance().format(d) + " km";
        } else if ((distance >= 0.200) && (distance < 10)) {
            d = (float) ((Math.ceil(distance * 100)) * 10);
            distanceText = NumberFormat.getInstance().format(d) + " m";
        } else if ((distance >= 0.060) && (distance < 0.200)) {
            d = (float) Math.ceil(distance * 1000);
            distanceText = NumberFormat.getInstance().format(d) + " m";
        } else {
            d = (float) (Math.ceil(distance * 10000) / 10);
            distanceText = NumberFormat.getInstance().format(d) + " m";
        }
        //return distanceText.replaceFirst("[\\.,]00* ", " ");
        return distanceText;
    }

    public float getDistanceInM() {
        return distance*1000;
    }

    public float getDistanceInKM() {
        return distance;
    }
}