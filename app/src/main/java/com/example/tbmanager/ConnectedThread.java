package com.example.tbmanager;

import android.bluetooth.BluetoothSocket;
import android.util.Log;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class ConnectedThread extends Thread {

    private static final String TAG = "FrugalLogs";
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private String valueRead;

    public ConnectedThread(BluetoothSocket socket) {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        try {
            tmpIn = socket.getInputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating input stream", e);
        }
        try {
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating output stream", e);
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public String getValueRead(){
        return valueRead;
    }

    public void run() {
        byte[] buffer = new byte[1024];
        int bytes; // bytes returned from read()

        while (true) {
            try {
                bytes = mmInStream.read(buffer);
                String incomingMessage = new String(buffer, 0, bytes);
                Log.d(TAG, "InputStream: " + incomingMessage);

                String[] coords = incomingMessage.split(",");
                if (coords.length == 2) {
                    double longitude = Double.parseDouble(coords[0]);
                    double latitude = Double.parseDouble(coords[1]);

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("fenced");
                    Map<String, Double> coordMap = new HashMap<>();
                    coordMap.put("longitude", longitude);
                    coordMap.put("latitude", latitude);
                    ref.setValue(coordMap);
                }

                Thread.sleep(60000);
            } catch (IOException | InterruptedException e) {
                Log.e(TAG, "Error reading from InputStream.", e);
                break;
            }
        }
    }

    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the connect socket", e);
        }
    }
}