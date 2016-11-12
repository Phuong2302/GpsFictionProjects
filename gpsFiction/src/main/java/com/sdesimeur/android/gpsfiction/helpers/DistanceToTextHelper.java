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
        String distanceText = "";
        if (distance >= 10) {
            distance = (float) ((Math.ceil(distance * 10)) / 10);
            distanceText = NumberFormat.getInstance().format(distance) + " km";
        } else if ((distance >= 0.800) && (distance < 10)) {
            distance = (float) ((Math.ceil(distance * 100)) / 100);
            distanceText = NumberFormat.getInstance().format(distance) + " km";
        } else if ((distance >= 0.060) && (distance < 0.800)) {
            distance = (float) Math.ceil(distance * 1000);
            distanceText = NumberFormat.getInstance().format(distance) + " m";
        } else {
            distance = (float) (Math.ceil(distance * 10000) / 10);
            distanceText = NumberFormat.getInstance().format(distance) + " m";
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