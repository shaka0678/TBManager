package com.example.tbmanager;

public class Coordinates {
    public double latitude;
    public double longitude;

    public Coordinates() {
        // Default constructor required for calls to DataSnapshot.getValue(Coordinates.class)
    }

    public Coordinates(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
