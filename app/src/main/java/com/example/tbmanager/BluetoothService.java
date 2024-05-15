package com.example.tbmanager;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class BluetoothService extends Service {
    private BluetoothSocket socket;
    private InputStream inputStream;
    private Thread thread;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice("00:22:11:30:C5:62");
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // UUID for Serial Port Profile

        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling ActivityCompat#requestPermissions here to request the missing permissions, and then overriding
                // public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation for ActivityCompat#requestPermissions for more details.
                return START_NOT_STICKY; // or any other appropriate return value
            }
            socket = device.createRfcommSocketToServiceRecord(uuid);
            socket.connect();
            inputStream = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] buffer = new byte[1024];
                int bytes;

                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        bytes = inputStream.read(buffer);
                        if (bytes == -1) {
                            break; // End of stream has been reached
                        }
                        String strReceived = new String(buffer, 0, bytes);

                        // Split the received string into longitude and latitude
                        String[] parts = strReceived.split(",");
                        if (parts.length >= 2) {
                            double longitude = Double.parseDouble(parts[0]);
                            double latitude = Double.parseDouble(parts[1]);

                            // Write longitude and latitude to Firebase Realtime Database
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("coordinates");
                            myRef.child("longitude").setValue(longitude);
                            myRef.child("latitude").setValue(latitude);
                        }
                    } catch (IOException | NumberFormatException e) {
                        e.printStackTrace();
                        Thread.currentThread().interrupt(); // Interrupt the thread if an error occurs
                    }
                }
            }
        });

        thread.start();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            thread.interrupt();
            inputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}