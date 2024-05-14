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
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bbbblue);
        buttoncon = (Button) findViewById(R.id.dat3k);
        imageButtonbck = findViewById(R.id.imageBy);

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
                        if (bluetoothAdapter != null) {
                            if (!bluetoothAdapter.isEnabled()) {
                                if (ActivityCompat.checkSelfPermission(bbbblue.this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return;
                                }
                                bluetoothAdapter.enable();
                            }
                            BluetoothDevice device = bluetoothAdapter.getRemoteDevice("00:22:11:30:C5:62");
                            BluetoothSocket socket = null;
                            try {
                                socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")); // UUID for Serial Port Profile
                                socket.connect();
                                InputStream inputStream = socket.getInputStream();
                                byte[] buffer = new byte[1024];
                                int bytes;
                                while (true) {
                                    bytes = inputStream.read(buffer);
                                    String strReceived = new String(buffer, 0, bytes);
                                    // Write to Firebase Realtime Database
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference myRef = database.getReference("coordinates");
                                    myRef.setValue(strReceived);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
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
                builder.setNeutralButton("", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                // Set the neutral button to be a close icon
                Button neutralButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                Drawable closeIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_close_24, null);
                neutralButton.setCompoundDrawablesWithIntrinsicBounds(closeIcon, null, null, null);
            }
        });

    }

}