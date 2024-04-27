package com.example.tbmanager;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.UUID;

public class devices extends AppCompatActivity {
    Button buttondev;
    Button buttoncon;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private InputStream inputStream;
    private final int REQUEST_BLUETOOTH_PERMISSION = 1;

    private Thread thread;
    private boolean stopThread;
    private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // UUID for Serial Port Profile (SPP)
    private BluetoothDevice bluetoothDevice;
    private static final String DEVICE_ADDRESS = "98:D3:91:FD:96:F3";
    private static final String TAG = "BluetoothConnection";
    private static final int REQUEST_ENABLE_BT = 1;
    private GoogleMap mMap;
    


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_devices);
        buttoncon = findViewById(R.id.dat3);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        

        buttoncon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectToBluetoothDevice();
            }
        });

    }

    private void connectToBluetoothDevice() {
        // Check if Bluetooth is supported on the device
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            return;
        }
        // Check if Bluetooth is enabled
        if (!bluetoothAdapter.isEnabled()) {
            // Bluetooth is not enabled, request user to enable it
            // This can be done by showing an AlertDialog to the user
            AlertDialog.Builder builder = new AlertDialog.Builder(devices.this);
            builder.setTitle("Bluetooth");
            builder.setMessage("Enable Bluetooth");

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // User clicked yes, enable Bluetooth
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    if (ActivityCompat.checkSelfPermission(devices.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT > 29){

                        return;
                    }


                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // User clicked no, dismiss dialog
                    dialog.dismiss();
                }
            });

            builder.show();
        } else {
            // Bluetooth is already enabled, show a Toast message
            Toast.makeText(devices    .this, "Bluetooth is already enabled", Toast.LENGTH_SHORT).show();
            // Proceed with Bluetooth connection
            connectToDevice();
        }
    }



    private void connectToDevice() {
        // Get the Bluetooth device with the specified address
        bluetoothDevice = bluetoothAdapter.getRemoteDevice("98:D3:91:FD:96:F3");

        // Attempt to connect to the Bluetooth device in a separate thread
        thread = new Thread(() -> {
            try {
                bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
                bluetoothSocket.connect();
                inputStream = bluetoothSocket.getInputStream();
                readData();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }


    private void readData() {
        stopThread = false;
        while (!stopThread) {
            try {
                byte[] buffer = new byte[1024];
                int bytes = inputStream.read(buffer);
                String data = new String(buffer, 0, bytes);
                // Parse the received GPS data and update the map
                parseAndDisplayLocation(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopThread = true;
        try {
            if (bluetoothSocket != null) {
                bluetoothSocket.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}