package com.example.tbmanager;
import static android.content.ContentValues.TAG;

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

import java.util.Timer;
import java.util.TimerTask;

public class General extends AppCompatActivity {

    private DatabaseReference databaseReference;

    private static class Coordinates {
        public double latitude;
        public double longitude;
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
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
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
        // Assume this method is called when a QR code is successfully scanned
        String deviceID = "device_id";

        getCurrentLocation(new CoordinateCallback() {
            @Override
            public void onLocationReceived(double latitude, double longitude) {
                long timestamp = System.currentTimeMillis();
                Coordinates coordinates = new Coordinates(latitude, longitude, timestamp);
                databaseReference.child(deviceID).setValue(coordinates);

                showMessage("Thank you for connecting");
            }
        });
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
