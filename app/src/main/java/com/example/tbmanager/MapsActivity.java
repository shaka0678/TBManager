package com.example.tbmanager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.health.connect.datatypes.ExerciseRoute;
import android.location.Address;
import android.location.Geocoder;
import android.Manifest;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private SearchView mapsearch;
    private GeofencingClient geofencingClient;
    private PendingIntent geofencePendingIntent;
    private FusedLocationProviderClient fusedLocationClient;
    private static final String PREFS_NAME = "GeofencePrefs";
    private static final String GEOFENCES_KEY = "Geofences";

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference coordinatesRef = database.getReference("Coordinates");


    private static Handler handler;
    private List<String> availableDevices;
    private final static int ERROR_READ = 0;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Initialize the SearchView
        mapsearch = findViewById(R.id.mapsearch1);
        mapsearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = mapsearch.getQuery().toString();
                List<Address> addressList = null;

                if (!location.isEmpty()) {
                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.isEmpty()) {
                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                    List<Address> addressList = null;
                    try {
                        addressList = geocoder.getFromLocationName(newText, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(latLng).title(newText));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    }
                }
                return false;
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000); // 10 seconds
        locationRequest.setFastestInterval(5 * 1000); // 5 seconds

        locationCallback = new LocationCallback() {
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(currentLocation)
                                .title("Current Location: " + location.getLatitude() + ", " + location.getLongitude())
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                    }
                }

            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permissions if not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        } else {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                mMap.addMarker(new MarkerOptions().position(currentLocation)
                                        .title("Current Location: " + location.getLatitude() + ", " + location.getLongitude())
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                            }
                        }
                    });
        }
    }



    // In your MapsActivity
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Get the geofence's latitude, longitude, and radius from the Intent
        double latitude = getIntent().getDoubleExtra("latitude", 0);
        double longitude = getIntent().getDoubleExtra("longitude", 0);
        float radius = getIntent().getFloatExtra("radius", 0);

        // Create a LatLng object for the geofence's location
        LatLng geofenceLocation = new LatLng(latitude, longitude);

        // Add a circle to the map at the geofence's location with the geofence's radius
        mMap.addCircle(new CircleOptions()
                .center(geofenceLocation)
                .radius(radius)
                .strokeColor(Color.RED)
                .fillColor(0x220000FF)
                .strokeWidth(5.0f));

        // Move the map's camera to the geofence's location
        mMap.moveCamera(CameraUpdateFactory.newLatLng(geofenceLocation));
        fetchAndPlotCoordinates();
    }

    private void connectToBluetoothDevice(String deviceName) {
        // Replace this with actual Bluetooth connection logic
        Toast.makeText(this, "Connecting to " + deviceName, Toast.LENGTH_SHORT).show();
    }

    private void parseAndDisplayLocation(String data) {
        // Assuming data format is latitude:longitude
        String[] parts = data.split(":");
        if (parts.length == 2) {
            double latitude = Double.parseDouble(parts[0]);
            double longitude = Double.parseDouble(parts[1]);
            LatLng latLng = new LatLng(latitude, longitude);
            runOnUiThread(() -> {
                mMap.clear(); // Clear previous markers
                mMap.addMarker(new MarkerOptions().position(latLng).title("Arduino Location"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            });
        }
    }

    private PendingIntent getGeofencePendingIntent() {
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        geofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }

    private void saveGeofence(Geofence geofence) {
        // Add geofence to geofencingClient
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        List<Geofence> geofences = new ArrayList<>();
        geofences.add(geofence);
        geofencingClient.addGeofences(getGeofencingRequest(geofences), getGeofencePendingIntent())
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Geofence added successfully
                        Toast.makeText(MapsActivity.this, "Geofence added successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to add geofence
                        Toast.makeText(MapsActivity.this, "Failed to add geofence", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void fetchAndPlotCoordinates() {
        DatabaseReference coordinatesRef = FirebaseDatabase.getInstance().getReference("coordinates");
        // Set up a listener to fetch coordinates
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mMap != null) {
                    mMap.clear(); // Clear existing markers
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Double latitude = snapshot.child("latitude").getValue(Double.class);
                        Double longitude = snapshot.child("longitude").getValue(Double.class);
                        if (latitude != null && longitude != null) {
                            LatLng location = new LatLng(latitude, longitude);
                            mMap.addCircle(new CircleOptions()
                                    .center(location)
                                    .radius(20) // Set the radius as needed
                                    .strokeColor(Color.GREEN) // Outline color
                                    .fillColor(0x2200FF00) // Fill color with transparency
                                    .strokeWidth(5.0f));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("MapsActivity", "loadCoordinates:onCancelled", databaseError.toException());
            }
        };

        // Attach the listener to the DatabaseReference
        coordinatesRef.addValueEventListener(listener);

        // To update the location every 60 seconds, you can use a Handler to delay the call to fetchAndPlotCoordinates
        final Handler handler = new Handler();
        final int delay = 60000; //milliseconds

        handler.postDelayed(new Runnable(){
            public void run(){
                // Clear previous listener to avoid duplicate data or memory leaks
                coordinatesRef.removeEventListener(listener);
                // Fetch and plot coordinates again
                fetchAndPlotCoordinates();
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    private GeofencingRequest getGeofencingRequest(List<Geofence> geofences) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        for (Geofence geofence : geofences) {
            builder.addGeofence(geofence);
        }
        return builder.build();
    }

    private void saveGeofences1(List<Geofence> geofences) {
        for (Geofence geofence : geofences) {
            //geofence.setExpirationDuration(Geofence.NEVER_EXPIRE);
        }

        // Add geofences to geofencingClient
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        geofencingClient.addGeofences(getGeofencingRequest(geofences), getGeofencePendingIntent())
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Geofences added successfully
                        Toast.makeText(MapsActivity.this, "Geofences added successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to add geofences
                        Toast.makeText(MapsActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}