package com.example.locationservices.model;

import java.io.Serializable;

/**
 * Created by danielsierraf on 1/9/17.
 */

public class Coords implements Serializable{
    private double latitude;
    private double longitude;

    public Coords(){
    }

    public Coords(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
