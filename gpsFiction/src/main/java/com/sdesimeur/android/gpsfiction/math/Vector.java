package com.sdesimeur.android.gpsfiction.math;

import com.sdesimeur.android.gpsfiction.geopoint.GeoPoint;

public class Vector {
    private int x, y;

    public Vector(GeoPoint pA, GeoPoint pB) {
        this.x = pA.getLongitudeE6() - pB.getLongitudeE6();
        this.y = pA.getLatitudeE6() - pB.getLatitudeE6();
    }

    public Vector(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vector() {
        this.x = 0;
        this.y = 0;
    }

    public int norm() {
        double x = (double) this.x;
        double y = (double) this.y;
        return (int) Math.round(Math.sqrt(x * x + y * y));
    }

    public Vector normalized() {
        Vector newVector = this;
        int d = newVector.norm();
        newVector.x = newVector.x / d;
        newVector.y = newVector.y / d;
        return newVector;
    }

    public int prodVector(Vector vect) {
        return (this.x * vect.y + this.y * vect.x);
    }

    public Vector prod(int d) {
        Vector newVector = this;
        newVector.x = newVector.x * d;
        newVector.y = newVector.y * d;
        return newVector;
    }

    public GeoPoint translate(GeoPoint point) {
        int xP = point.getLongitudeE6();
        int yP = point.getLatitudeE6();
        xP += this.x;
        yP += this.y;
        GeoPoint newPoint = new GeoPoint(yP, xP);
        return newPoint;
    }

    public Vector ortho() {
        return new Vector(this.y, -this.x);
    }

    public int scalarProd(Vector vector) {
        return (this.x * vector.y + this.y * vector.x);
    }

    public Vector div(int d) {
        Vector newVector = this;
        newVector.x = newVector.x / d;
        newVector.y = newVector.y / d;
        return newVector;
    }

}
