package com.example.tbmanager;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class General extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private TextView resultTextView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general);

        databaseReference = FirebaseDatabase.getInstance().getReference("Coordinates");
        resultTextView = findViewById(R.id.resultTextView);

        Button scanButton = findViewById(R.id.scanButton);
        scanButton.setOnClickListener(v -> {
            IntentIntegrator integrator = new IntentIntegrator(General.this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
            integrator.setPrompt("Scan a QR code");
            integrator.setCameraId(0);
            integrator.setBeepEnabled(false);
            integrator.setBarcodeImageEnabled(false);
            integrator.initiateScan();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            String scannedData = result.getContents();
            if (scannedData != null) {
                storeScannedData(scannedData);
            } else {
                Toast.makeText(this, "Failed to scan QR code", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void storeScannedData(String data) {
        // Store the scanned data in Firebase Realtime Database
        databaseReference.push().setValue(data);
        resultTextView.setText("Scanned Data: " + data);
    }
}
