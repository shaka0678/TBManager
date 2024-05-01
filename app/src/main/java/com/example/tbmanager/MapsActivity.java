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



        // Load geofences from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("Geofences", MODE_PRIVATE);
        String geofenceJson = sharedPreferences.getString("geofence", "");
        Type type = new TypeToken<ArrayList<Geofence>>() {
        }.getType();
        ArrayList<Geofence> geofenceList = new Gson().fromJson(geofenceJson, type);

        if (geofenceList != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 300);
                return;
            }
            geofencingClient.addGeofences(getGeofencingRequest(geofenceList), geofencePendingIntent);
        }

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


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Get the geofence's latitude, longitude, radius and person's name from the intent
        LatLng latLng = getIntent().getParcelableExtra("location");
        if (latLng != null) {
            double latitude = latLng.latitude;
            double longitude = latLng.longitude;
            float radius = getIntent().getFloatExtra("radius", 0);
            String personName = getIntent().getStringExtra("personName");

            // Draw a circle on the map at the geofence's location with the geofence's radius
            mMap.addCircle(new CircleOptions()
                    .center(latLng)
                    .radius(radius)
                    .strokeColor(Color.RED)
                    .fillColor(0x220000FF)
                    .strokeWidth(5.0f));

            // Add a marker with the person's name as the title
            mMap.addMarker(new MarkerOptions().position(latLng).title(personName));

            // Move the camera to the geofence's location
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        } else {
            // Handle the case where LatLng is null
           // Log.e("MapsActivity", "LatLng object is null");
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        } else {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
        fetchAndPlotCoordinates();
        startUpdateTask();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            // ... Your existing code ...
        } else if (requestCode == 200) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Bluetooth permission granted, you can continue with Bluetooth operations
            } else {
                Toast.makeText(this, "Bluetooth permission denied!", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 300) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission granted, you can continue with Location operations
            } else {
                Toast.makeText(this, "Location permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void fetchAndPlotCoordinates() {
        coordinatesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Coordinates coordinates = dataSnapshot.getValue(Coordinates.class);
                    if (coordinates != null) {
                        LatLng latLng = new LatLng(coordinates.latitude, coordinates.longitude);
                        mMap.addMarker(new MarkerOptions().position(latLng).title("Marker at " + latLng.toString()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("MapsActivity", "Failed to read value.", error.toException());
            }
        });
    }
    private void startUpdateTask() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    mMap.clear(); // Clear previous markers
                    fetchAndPlotCoordinates(); // Fetch and plot new coordinates
                });
            }
        }, 0, 60000); // Update every 60 seconds
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

        // Save geofence to SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("Geofences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("geofence", new Gson().toJson(geofence));
        editor.apply();

        // Add geofence to geofencingClient
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

    private GeofencingRequest getGeofencingRequest(List<Geofence> geofences) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        for (Geofence geofence : geofences) {
            builder.addGeofence(geofence);
        }
        return builder.build();
    }

    private void saveGeofences(List<Geofence> geofences) {
        for (Geofence geofence : geofences) {
            //geofence.setExpirationDuration(Geofence.NEVER_EXPIRE);
        }

        // Save geofences to SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("Geofences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("geofences", new Gson().toJson(geofences));
        editor.apply();

        // Add geofences to geofencingClient
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

