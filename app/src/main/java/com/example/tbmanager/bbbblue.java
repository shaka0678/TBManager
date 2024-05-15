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
import java.util.UUID;

public class bbbblue extends AppCompatActivity {
    Button buttoncon;
    ImageButton imageButtonbck;
    Switch aSwitchcon;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1; // or any other unique integer

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
                        Intent intent = new Intent(bbbblue.this, BluetoothService.class);
                        startService(intent);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.setNeutralButton("Connection", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                        BluetoothDevice device = bluetoothAdapter.getRemoteDevice("00:22:11:30:C5:62");

                        if (ContextCompat.checkSelfPermission(bbbblue.this, Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {
                            // Permission is not granted
                            ActivityCompat.requestPermissions(bbbblue.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                        }
                        if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                            // The device is not paired, show a dialog asking the user to pair
                            AlertDialog.Builder pairBuilder = new AlertDialog.Builder(bbbblue.this);
                            pairBuilder.setTitle("Pair with device");
                            if (ContextCompat.checkSelfPermission(bbbblue.this, Manifest.permission.ACCESS_FINE_LOCATION)
                                    != PackageManager.PERMISSION_GRANTED) {
                                // Permission is not granted
                                ActivityCompat.requestPermissions(bbbblue.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                            }
                            pairBuilder.setMessage("Do you want to pair with the device " + device.getName() + "?");
                            pairBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Pair with the device
                                    // Note: This requires the BLUETOOTH_ADMIN permission
                                    try {
                                        if (ContextCompat.checkSelfPermission(bbbblue.this, Manifest.permission.ACCESS_FINE_LOCATION)
                                                != PackageManager.PERMISSION_GRANTED) {
                                            // Permission is not granted
                                            ActivityCompat.requestPermissions(bbbblue.this,
                                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                                        }
                                        device.createBond();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            pairBuilder.setNegativeButton("No", null);
                            pairBuilder.show();
                        } else {
                            // The device is already paired, start the BluetoothService
                            Intent intent = new Intent(bbbblue.this, BluetoothService.class);
                            startService(intent);
                        }
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }

}