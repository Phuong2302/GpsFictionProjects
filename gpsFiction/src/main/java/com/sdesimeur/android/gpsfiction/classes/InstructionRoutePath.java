package com.sdesimeur.android.gpsfiction.classes;

/**
 * Created by sam on 23/10/16.
 */

public class InstructionRoutePath {
    public String nextInstructionString = "";
    public double distanceToNextInstruction = 0;

    public InstructionRoutePath(String nis, double dni) {
        nextInstructionString = nis;
        distanceToNextInstruction = dni;
    }
}
