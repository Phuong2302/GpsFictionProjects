package com.sdesimeur.android.gpsfiction;

import org.junit.Assert;
import org.junit.Test;

import helpers.DistanceToTextHelper;

/**
 * Created by sam on 08/12/16.
 */
public class DistanceToTextHelperTest {

    @Test
    public void getDistanceInText()  {
        DistanceToTextHelper d = new DistanceToTextHelper(0.5f);
        String txt = d.getDistanceInText();
        System.out.println(txt.toString());
        Assert.assertEquals(d.toString(),"500 m");
    }

}