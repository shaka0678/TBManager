package com.example.tbmanager;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "GeofenceBroadcast";

    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (geofencingEvent.hasError()) {
            Log.e(TAG, "GeofencingEvent error: " + geofencingEvent.getErrorCode());
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get the triggering geofences
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Handle the geofence transition
            handleGeofenceTransition(context, geofenceTransition, triggeringGeofences);
        } else {
            Log.e(TAG, "Unknown transition: " + geofenceTransition);
        }
    }

    private void handleGeofenceTransition(Context context, int geofenceTransition, List<Geofence> triggeringGeofences) {
        for (Geofence geofence : triggeringGeofences) {
            Log.d(TAG, "Geofence transition: " + geofenceTransition + " for geofence ID: " + geofence.getRequestId());
        }
    }

    public void createGeofences(Context context, List<Geofence> geofenceList) {
        GeofencingClient geofencingClient = LocationServices.getGeofencingClient(context);
        PendingIntent geofencePendingIntent = createGeofencePendingIntent(context);

        GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                .addGeofences(geofenceList)
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .build();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent)
                .addOnSuccessListener(aVoid -> {
                    // Geofences added successfully
                    Log.d(TAG, "Geofences added successfully");
                })
                .addOnFailureListener(e -> {
                    // Failed to add geofences
                    Log.e(TAG, "Failed to add geofences: " + e.getMessage());
                    Toast.makeText(context, "Failed to add geofences", Toast.LENGTH_SHORT).show();
                });
    }

    private PendingIntent createGeofencePendingIntent(Context context) {
        Intent intent = new Intent(context, GeofenceBroadcastReceiver.class);
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
