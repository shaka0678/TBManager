package com.example.tbmanager;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;
import android.Manifest;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

public class bbbblue extends AppCompatActivity {
    Button buttoncon;
    ImageButton imageButtonbck;
    Switch aSwitchcon;
    private static final int MY_PERMISSIONS_REQUEST_BLUETOOTH_CONNECT = 1; // or any other unique integer

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bbbblue);
        buttoncon = (Button) findViewById(R.id.dat3k);
        imageButtonbck = findViewById(R.id.imageBy);
        aSwitchcon = findViewById(R.id.switch2);


        aSwitchcon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (bluetoothAdapter == null) {
                    // Device doesn't support Bluetooth
                    return;
                }
                if (isChecked) {
                    // The toggle is enabled/checked
                    // Enable Bluetooth
                    if (!bluetoothAdapter.isEnabled()) {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling ActivityCompat#requestPermissions here to request the missing permissions
                            return;
                        }
                        bluetoothAdapter.enable();
                    }
                    Toast.makeText(getApplicationContext(), "Switch is on, Bluetooth enabled", Toast.LENGTH_SHORT).show();
                } else {
                    // The toggle is disabled/unchecked
                    // Disable Bluetooth
                    if (bluetoothAdapter.isEnabled()) {
                        bluetoothAdapter.disable();
                    }
                    Toast.makeText(getApplicationContext(), "Switch is off, Bluetooth disabled", Toast.LENGTH_SHORT).show();
                }
            }
        });
        imageButtonbck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(bbbblue.this, MainActivity2.class);
                startActivity(intent);
            }
        });

        buttoncon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(bbbblue.this);
                builder.setTitle("Bluetooth Connection");
                builder.setMessage("Do you want to enable bluetooth connection?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                        if (bluetoothAdapter == null) {
                            // Device doesn't support Bluetooth
                            return;
                        }

                        // Check if Bluetooth is enabled
                        if (!bluetoothAdapter.isEnabled()) {
                            // Enable Bluetooth
                            if (ContextCompat.checkSelfPermission(bbbblue.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(bbbblue.this,
                                        new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                                        MY_PERMISSIONS_REQUEST_BLUETOOTH_CONNECT);
                                return;
                            }
                            bluetoothAdapter.enable();
                        }

                        // Get the paired devices
                        if (ContextCompat.checkSelfPermission(bbbblue.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(bbbblue.this,
                                    new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                                    MY_PERMISSIONS_REQUEST_BLUETOOTH_CONNECT);
                            return;
                        }
                        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

                        // Check if the device with the specific MAC address is paired
                        for (BluetoothDevice device : pairedDevices) {
                            if (device.getAddress().equals("00:22:11:30:C5:62")) {
                                // The device is paired, connect to it
                                try {
                                    if (ContextCompat.checkSelfPermission(bbbblue.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(bbbblue.this,
                                                new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                                                MY_PERMISSIONS_REQUEST_BLUETOOTH_CONNECT);
                                        return;
                                    }
                                    BluetoothSocket socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")); // UUID for SPP
                                    if (ContextCompat.checkSelfPermission(bbbblue.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(bbbblue.this,
                                                new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                                                MY_PERMISSIONS_REQUEST_BLUETOOTH_CONNECT);
                                        return;
                                    }
                                    socket.connect();

                                    // Start receiving data
                                    InputStream inputStream = socket.getInputStream();
                                    byte[] buffer = new byte[1024];
                                    int bytes;

                                    // Read from the InputStream
                                    if ((bytes = inputStream.read(buffer)) > 0) {
                                        String readMessage = new String(buffer, 0, bytes);

                                        // Send the data to Firebase
                                        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                                        database.child("coordinates").push().setValue(readMessage);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            }
                        }
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });



                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_BLUETOOTH_CONNECT: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                } else {
                    // permission denied
                }
                return;
            }
            // other 'case' lines to check for other permissions this app might request
        }
    }

}