package com.example.tbmanager;

import static android.content.ContentValues.TAG;


import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public class devices extends AppCompatActivity {
    Button buttondiscover;
    Button buttoncon;
    Button buttonenable;
    ImageButton imageButtonbck;
    private ListView listView;
    private BluetoothAdapter mBTAdapter;
    private static final int BT_ENABLE_REQUEST = 10; //  for BT Enable
    private static final int SETTINGS = 20;
    private static final int REQUEST_CODE_PERMISSION_BLUETOOTH_SCAN =100;
    private UUID mDeviceUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private int mBufferSize = 50000; //Default
    public static final String DEVICE_EXTRA = "com.example.anysensormonitoring.SOCKET";
    public static final String DEVICE_UUID = "com.example.anysensormonitoring.uuid";
    private static final String DEVICE_LIST = "com.example.anysensormonitoring.devicelist";
    private static final String DEVICE_LIST_SELECTED = "com.example.anysensormonitoring.devicelistselected";
    public static final String BUFFER_SIZE = "com.example.anysensormonitoring.buffersize";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);


        buttondiscover = (Button) findViewById(R.id.dat13);
        buttoncon = (Button) findViewById(R.id.dat3);
        imageButtonbck = findViewById(R.id.imageB);
        buttonenable = findViewById(R.id.dat14);
        listView = findViewById(R.id.lvNewDevices);
        imageButtonbck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(devices.this, MainActivity2.class);
                startActivity(intent);
            }
        });

        buttonenable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if Bluetooth is enabled
                if (!mBTAdapter.isEnabled()) {
                    Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBT, BT_ENABLE_REQUEST);
                } else {
                    // Start Bluetooth discovery
                    if (ActivityCompat.checkSelfPermission(devices.this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    mBTAdapter.startDiscovery();
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Cancel discovery because it's costly and we're about to connect
                if (ActivityCompat.checkSelfPermission(devices.this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mBTAdapter.cancelDiscovery();

                // Get the device MAC address, the last 17 chars in the View
                String info = ((TextView) view).getText().toString();
                String address = info.substring(info.length() - 17);

                // Get the BluetoothDevice object
                BluetoothDevice device = mBTAdapter.getRemoteDevice(address);

                // Attempt to connect to the device
                // You need to implement a method connectDevice that pairs and connects with the device
                connectDevice(device);
            }
        });

        listView = (ListView) findViewById(R.id.lvNewDevices);

        if (savedInstanceState != null) {
            ArrayList<BluetoothDevice> list = savedInstanceState.getParcelableArrayList(DEVICE_LIST);
            if (list != null) {
                initList(list);
                MyAdapter adapter = (MyAdapter) listView.getAdapter();
                int selectedIndex = savedInstanceState.getInt(DEVICE_LIST_SELECTED);
                if (selectedIndex != -1) {
                    adapter.setSelectedIndex(selectedIndex);
                    buttoncon.setEnabled(true);
                }
            } else {
                initList(new ArrayList<BluetoothDevice>());
            }

        } else {
            initList(new ArrayList<BluetoothDevice>());
        }
        buttoncon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mBTAdapter = BluetoothAdapter.getDefaultAdapter();

                if (mBTAdapter == null) {
                    Toast.makeText(getApplicationContext(), "Bluetooth not found", Toast.LENGTH_SHORT).show();
                } else if (!mBTAdapter.isEnabled()) {
                    // Create an AlertDialog
                    new AlertDialog.Builder(devices.this)
                            .setTitle("Bluetooth Connection")
                            .setMessage("Do you want to enable Bluetooth connection?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // User clicked the "Yes" button, enable Bluetooth
                                    Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                                    enableBT.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                                    if (ActivityCompat.checkSelfPermission(devices.this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(devices.this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                                        // TODO: Consider calling ActivityCompat#requestPermissions
                                        return;
                                    }
                                    startActivityForResult(enableBT, BT_ENABLE_REQUEST);

                                    // Get the remote device and initiate a connection
                                    String deviceAddress = "00:22:11:30:C5:62";
                                    BluetoothDevice device = mBTAdapter.getRemoteDevice(deviceAddress);
                                    // Assuming you have a method connectToDevice that takes a BluetoothDevice and connects to it
                                    connectDevice(device);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                }
            }
        });
        buttondiscover.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mBTAdapter = BluetoothAdapter.getDefaultAdapter();

                if (mBTAdapter == null) {
                    Toast.makeText(getApplicationContext(), "Bluetooth not found", Toast.LENGTH_SHORT).show();
                } else if (!mBTAdapter.isEnabled()) {
                    Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBT, BT_ENABLE_REQUEST);
                } else {
                    if (ActivityCompat.checkSelfPermission(devices.this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(devices.this, new String[]{android.Manifest.permission.BLUETOOTH_SCAN}, REQUEST_CODE_PERMISSION_BLUETOOTH_SCAN);
                        return;
                    }

                    mBTAdapter.startDiscovery();

                    // Make the device discoverable
                    int requestCode = 1;
                    Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                    startActivityForResult(discoverableIntent, requestCode);
                }
            }
        });
// Create a BroadcastReceiver for ACTION_FOUND
        final BroadcastReceiver receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (ActivityCompat.checkSelfPermission(devices.this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address

                    // Add the name and address to your listView adapter here
                    MyAdapter adapter = (MyAdapter) listView.getAdapter();
                    adapter.add(deviceName + "\n" + deviceHardwareAddress);
                    adapter.notifyDataSetChanged();
                }
            }
        };

// Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

    }

    private void connectDevice(BluetoothDevice device) {
        BluetoothSocket bluetoothSocket = null;

        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice
            // MY_UUID is the app's UUID string, also used by the server code
            UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // Standard SerialPortService ID
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
            bluetoothSocket = device.createRfcommSocketToServiceRecord(MY_UUID);

            // Cancel discovery because it's costly and we're about to connect
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            bluetoothSocket.connect();

            // Do work here to manage the connection (in a separate thread)
            manageConnectedSocket(bluetoothSocket);
        } catch (IOException e) {
            // Unable to connect; close the socket and return
            try {
                bluetoothSocket.close();
            } catch (IOException closeException) {
                Log.e(TAG, "Could not close the client socket", closeException);
            }
            return;
        }
    }

    private void manageConnectedSocket(BluetoothSocket socket) {
        // Create a separate thread to read data from the socket
        Thread readThread = new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] buffer = new byte[1024];  // buffer store for the stream
                int bytes; // bytes returned from read()

                // Keep listening to the InputStream until an exception occurs
                while (true) {
                    try {
                        // Read from the InputStream
                        bytes = socket.getInputStream().read(buffer);
                        // Send the obtained bytes to the UI activity
                        // You need to implement a method handleMessage that handles the received data
                        handleMessage(buffer, bytes);
                    } catch (IOException e) {
                        break;
                    }
                }
            }
        });

        // Create a separate thread to write data to the socket
        Thread writeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] buffer = new byte[1024];  // buffer store for the stream

                // Keep writing to the OutputStream until an exception occurs
                while (true) {
                    try {
                        // Write to the OutputStream
                        socket.getOutputStream().write(buffer);
                    } catch (IOException e) {
                        break;
                    }
                }
            }
        });

        // Start the threads
        readThread.start();
        writeThread.start();
    }

    private void handleMessage(byte[] buffer, int bytes) {
        // Convert the bytes to a string
        String receivedMessage = new String(buffer, 0, bytes);

        // Now you can use this string to update your UI or process the data
        // For example, you can use a Handler to send the message to the UI thread
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                // Assuming you have a TextView to display the received message
                TextView textView = findViewById(R.id.textViewR);
                textView.setText(receivedMessage);
            }
        });
    }
    protected void onPause() {
// TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onStop() {
// TODO Auto-generated method stub
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case BT_ENABLE_REQUEST:
                if (resultCode == RESULT_OK) {
                    msg("Bluetooth Enabled successfully");
                    //new SearchDevices().execute();
                } else {
                    msg("Bluetooth couldn't be enabled");
                }

                break;
            case SETTINGS: //If the settings have been updated
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                String uuid = prefs.getString("prefUuid", "Null");
                mDeviceUUID = UUID.fromString(uuid);
                Log.d(TAG, "UUID: " + uuid);
                String bufSize = prefs.getString("prefTextBuffer", "Null");
                mBufferSize = Integer.parseInt(bufSize);

                String orientation = prefs.getString("prefOrientation", "Null");
                Log.d(TAG, "Orientation: " + orientation);
                if (orientation.equals("Landscape")) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else if (orientation.equals("Portrait")) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else if (orientation.equals("Auto")) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Quick way to call the Toast
     * @param str
     */
    private void msg(String str) {
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
    }


    // Initialize the List adapter

    private void initList(List<BluetoothDevice> objects) {
        final MyAdapter adapter = new MyAdapter(getApplicationContext(), R.layout.list_item, R.id.lstContent, objects);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setSelectedIndex(position);
                buttoncon.setEnabled(true);
            }
        });
    }

    /**
     * Searches for paired devices. Doesn't do a scan! Only devices which are paired through Settings->Bluetooth
     * will show up with this.
     *
     */

    /**
     * Custom adapter to show the current devices in the list. This is a bit of an overkill for this
     *
     */
    private class MyAdapter extends ArrayAdapter<BluetoothDevice> {
        private int selectedIndex;
        private Context context;
        private int selectedColor = Color.parseColor("#abcdef");
        private List<BluetoothDevice> myList;

        public MyAdapter(Context ctx, int resource, int textViewResourceId, List<BluetoothDevice> objects) {
            super(ctx, resource, textViewResourceId, objects);
            context = ctx;
            myList = objects;
            selectedIndex = -1;
        }

        public void setSelectedIndex(int position) {
            selectedIndex = position;
            notifyDataSetChanged();
        }

        public BluetoothDevice getSelectedItem() {
            return myList.get(selectedIndex);
        }

        @Override
        public int getCount() {
            return myList.size();
        }

        @Override
        public BluetoothDevice getItem(int position) {
            return myList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void add(String s) {
        }

        private class ViewHolder {
            TextView tv;
        }

        public void replaceItems(List<BluetoothDevice> list) {
            myList = list;
            notifyDataSetChanged();
        }

        public List<BluetoothDevice> getEntireList() {
            return myList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View vi = convertView;
            ViewHolder holder;
            if (convertView == null) {
                vi = LayoutInflater.from(context).inflate(R.layout.list_item, null);
                holder = new ViewHolder();

                holder.tv = (TextView) vi.findViewById(R.id.lstContent);

                vi.setTag(holder);
            } else {
                holder = (ViewHolder) vi.getTag();
            }

            if (selectedIndex != -1 && position == selectedIndex) {
                holder.tv.setBackgroundColor(selectedColor);
            } else {
                holder.tv.setBackgroundColor(Color.WHITE);
            }
            BluetoothDevice device = myList.get(position);
            if (ActivityCompat.checkSelfPermission(devices.this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            holder.tv.setText(device.getName() + "\n " + device.getAddress());

            return vi;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
// Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.homescreen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(devices.this, PreferencesActivity.class);
            startActivityForResult(intent, SETTINGS);
        }
        return super.onOptionsItemSelected(item);
    }
}


