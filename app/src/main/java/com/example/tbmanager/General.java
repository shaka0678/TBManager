package com.example.tbmanager;

import static android.content.ContentValues.TAG;

import static com.example.tbmanager.General.Coordinates.MY_UUID;
import static com.example.tbmanager.General.Coordinates.REQUEST_ENABLE_BT;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class General extends AppCompatActivity {

    private DatabaseReference databaseReference;

    static class Coordinates {
        public double latitude;
        public double longitude;
        public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        static final int REQUEST_ENABLE_BT = 1;


        public long timestamp;

        public Coordinates() {
        }

        public Coordinates(double latitude, double longitude, long timestamp) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.timestamp = timestamp;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general);

        databaseReference = FirebaseDatabase.getInstance().getReference("Coordinates");

        ImageView qrCodeImageView = findViewById(R.id.qr_code_image);
        String data = "device_id";

        Bitmap qrCodeBitmap = generateQRCode(data, 200, 200);
        qrCodeImageView.setImageBitmap(qrCodeBitmap);

        handleQRCodeScan();

        startLocationUpdateTask();
    }

    private Bitmap generateQRCode(String data, int width, int height) {
        com.google.zxing.qrcode.QRCodeWriter qrCodeWriter = new com.google.zxing.qrcode.QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height);
            int matrixWidth = bitMatrix.getWidth();
            int matrixHeight = bitMatrix.getHeight();
            int[] pixels = new int[matrixWidth * matrixHeight];

            for (int y = 0; y < matrixHeight; y++) {
                for (int x = 0; x < matrixWidth; x++) {
                    pixels[y * matrixWidth + x] = bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF;
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(matrixWidth, matrixHeight, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, matrixWidth, 0, 0, matrixWidth, matrixHeight);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void handleQRCodeScan() {
        // Start a Bluetooth server socket
        new Thread(() -> {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothServerSocket serverSocket = null;
            try {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord("AppName", UUID.fromString("MY_UUID"));
                while (true) {
                    BluetoothSocket clientSocket = serverSocket.accept(); // Wait for client to connect
                    new Thread(() -> {
                        try {
                            InputStream inputStream = clientSocket.getInputStream();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                            // Read the latitude and longitude sent by the client
                            String latitudeStr = reader.readLine();
                            String longitudeStr = reader.readLine();
                            double latitude = Double.parseDouble(latitudeStr);
                            double longitude = Double.parseDouble(longitudeStr);

                            // Check if the Bluetooth device's MAC address matches the expected value
                            BluetoothDevice clientDevice = clientSocket.getRemoteDevice();
                            String macAddress = clientDevice.getAddress();
                            if (!macAddress.equals("74:8a:28:92:29:5c")) {
                                showMessage("Invalid device MAC address");
                                return;
                            }

                            // Store the location data in Firebase
                            long timestamp = System.currentTimeMillis();
                            Coordinates coordinates = new Coordinates(latitude, longitude, timestamp);
                            databaseReference.child("device_id").setValue(coordinates);

                            showMessage("Location data received from the client");

                            // Close connections
                            reader.close();
                            inputStream.close();
                            clientSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (serverSocket != null) {
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    private void startLocationUpdateTask() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getCurrentLocation(new CoordinateCallback() {
                    @Override
                    public void onLocationReceived(double latitude, double longitude) {
                        String deviceID = "device_id";
                        long timestamp = System.currentTimeMillis();

                        Coordinates coordinates = new Coordinates(latitude, longitude, timestamp);
                        databaseReference.child(deviceID).setValue(coordinates);
                    }
                });
            }
        }, 0, 60000); // Update every 60 seconds
    }

    private void getCurrentLocation(CoordinateCallback callback) {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            Task<Location> locationResult = fusedLocationClient.getLastLocation();
            locationResult.addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    Location location = task.getResult();
                    callback.onLocationReceived(location.getLatitude(), location.getLongitude());
                } else {
                    Log.w("TAG", "getLastLocation:exception", task.getException());
                    callback.onLocationReceived(0.0, 0.0); // Default values or error handling
                }
            });
        } catch (SecurityException e) {
            Log.e("TAG", "getCurrentLocation: security exception", e);
            callback.onLocationReceived(0.0, 0.0); // Default values or error handling
        }
    }

    private void showMessage(String message) {
        runOnUiThread(() -> Toast.makeText(General.this, message, Toast.LENGTH_SHORT).show());
    }

    private interface CoordinateCallback {
        void onLocationReceived(double latitude, double longitude);
    }
}
