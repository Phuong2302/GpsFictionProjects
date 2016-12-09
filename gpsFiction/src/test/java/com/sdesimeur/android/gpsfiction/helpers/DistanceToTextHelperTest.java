package com.sdesimeur.android.gpsfiction.helpers;

import org.junit.Assert;

/**
 * Created by sam on 09/12/16.
 */
public class DistanceToTextHelperTest {
    @org.junit.Before
    public void setUp() throws Exception {

    }

    @org.junit.Test
    public void getDistanceInText() throws Exception {
        DistanceToTextHelper d = new DistanceToTextHelper(0.954f);
        String s;
        s = d.getDistanceInText();
        System.out.println(s);
        Assert.assertEquals(s , s , "960 m");
        d.setDistanceInKM(12.4534534f);
        s = d.getDistanceInText();
        Assert.assertEquals(s,s,"12,5 km");
    }

}