package com.example.tbmanager;

import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.maps.model.LatLng;

import java.time.Duration;

public class GeoHelper extends ContextWrapper {

    private static final String TAG = "GeoHelper";
    PendingIntent pendingIntent;
    public GeoHelper(Context base) {
        super(base);
    }

    public GeofencingRequest geofencingRequest(Geofence geofence){
        return null;
    }
    public Geofence getGeofence(String ID, int Duration, LatLng latLng,float radius,int transitionTypes){
        return null;
    }
    public  PendingIntent getPendingIntent(){

        if (pendingIntent != null){
            return pendingIntent;
        }
        else {
            Intent intent = new Intent(this,GeofenceBroadcastReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(this,3000,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        }

        return null;
    }
}
