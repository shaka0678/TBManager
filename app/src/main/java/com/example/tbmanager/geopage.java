package com.example.tbmanager;

import static com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_DWELL;
import static com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER;
import static com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_EXIT;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

public class geopage extends AppCompatActivity {
    EditText editTextduration;
    EditText editTextPatient;
    EditText editTextLog;
    EditText editTextlat;
    EditText editTextradius;
    Button buttonsub;
    private GeofencingClient geofencingClient;
    private List<Geofence> geofenceList = new ArrayList<>();
    private final int MY_PERMISSIONS_REQUEST_LOCATION = 123;

    @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geopage);

        editTextPatient = findViewById(R.id.intkb);
        editTextduration= findViewById(R.id.int0);
        editTextLog = findViewById(R.id.int2);
        editTextlat = findViewById(R.id.int4);
        editTextradius = findViewById(R.id.int6);
        buttonsub = findViewById(R.id.int9);


        geofencingClient = LocationServices.getGeofencingClient(this);

        buttonsub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String patientName = editTextPatient.getText().toString().trim();
                String longitudeStr = editTextLog.getText().toString().trim();
                String latitudeStr = editTextlat.getText().toString().trim();
                String radiusStr = editTextradius.getText().toString().trim();
                String durationText = editTextduration.getText().toString().trim();
                long durationMillis;

                try {
                    int durationDays = Integer.parseInt(durationText);

                    durationMillis = durationDays * 24 * 60 * 60 * 1000;
                } catch (NumberFormatException e) {
                    Toast.makeText(geopage.this, "Quarantine lasts 15 days only", Toast.LENGTH_SHORT).show();

                    return;
                }
                float radius;
                try {
                    radius = Float.parseFloat(radiusStr);

                    // Check if the radius is within the valid range
                    if (radius < 20) {
                        Toast.makeText(geopage.this, "Below the limit", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (radius > 65){
                        Toast.makeText(geopage.this, "Above the limit", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(geopage.this, "Invalid input format", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (patientName.isEmpty() || longitudeStr.isEmpty() || latitudeStr.isEmpty() || radiusStr.isEmpty()) {
                    Toast.makeText(geopage.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                double longitude;
                double latitude;
                try {
                    longitude = Double.parseDouble(longitudeStr);
                    latitude = Double.parseDouble(latitudeStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(geopage.this, "Invalid input format", Toast.LENGTH_SHORT).show();
                    return;
                }
                Geofence geofence = new Geofence.Builder()
                        .setRequestId(patientName) // Unique ID for the geofence
                        .setCircularRegion(latitude, longitude, radius)
                        .setExpirationDuration(durationMillis)
                        .setLoiteringDelay(1200000)
                        .setTransitionTypes(GEOFENCE_TRANSITION_ENTER | GEOFENCE_TRANSITION_DWELL | GEOFENCE_TRANSITION_EXIT)
                        .build();
                geofenceList.add(geofence);

                // Create GeofencingRequest
                GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                        .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                        .addGeofences(geofenceList)
                        .build();



                PendingIntent geofencePendingIntent = createGeofencePendingIntent();
                if (ActivityCompat.checkSelfPermission(geopage.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted, handle it (e.g., show a message to the user)
                } else {
                    ActivityCompat.requestPermissions(geopage.this,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);
                }
                geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent);


                Intent mapIntent = new Intent(geopage.this, MapsActivity.class);
                mapIntent.putExtra("latitude", latitude);
                mapIntent.putExtra("radius", radius);
                mapIntent.putExtra("longitude", longitude);
                startActivity(mapIntent);


            }
        });
    }
    private PendingIntent createGeofencePendingIntent() {
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

}