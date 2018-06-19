package com.example.mysqlexampleproject;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FindBluetoothActivity extends Activity {
    Bluetooth bluetooth;
    Activity thisActivity;
    Context thisContext;
    ListView bluetoothListView;
    Boolean recieverRegistered = false;
    BluetoothDevice connectedDevice;
    Map<String, DevicePair> bluetoothDiscoveredMap = new LinkedHashMap<>();
    BluetoothListAdapter bluetoothListAdapter;
    TextView bluetoothTextView;
    private List<BluetoothDevice> discoveredDevices = new ArrayList<>();
    private List<BluetoothDevice> pairedDevicesList = new ArrayList<>();

    //Laver en receiver, skal være der for at kunne discover devices
    public final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!(getPairedDevicesList().contains(device) || getDiscoveredDevices().contains(device) || device.getName()==null)) {
                    Log.e("Found device", "" + device.getName());
                    addDiscoveredDeviceList(device);
                    updateBluetoothDevicesList();
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bluetooth = new Bluetooth();
        thisActivity = this;
        thisContext = this;
        changeContentGetBluetooth();
    }

    public void changeContentGetBluetooth() {
        setContentView(R.layout.activity_connect_to_bluetooth);
        bluetoothListView = findViewById(R.id.DeviceListView);
        bluetoothTextView = findViewById(R.id.bluetoothStatus);
        initialiseBluetooth();
        bluetoothTextView.setText("Discovering devices");
        updateBluetoothDevicesList();


        Button btnBluetooth = findViewById(R.id.FindBluetooth);
        btnBluetooth.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (bluetooth.checkBluetoothAvailability()) {
                    bluetooth.discoverDevices(thisActivity);
                } else {
                    bluetoothTextView.setText("The device doesn't support bluetooth");
                }
            }
        });

        bluetoothListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                bluetoothTextView.setText("Initiating connection");
                Object device = adapterView.getItemAtPosition(position);
                if (bluetooth.getConnecting() == null) {
                    bluetooth.connectToUnit((BluetoothDevice) device, thisActivity);
                    updatePairedList();
                } else if(!device.equals(connectedDevice)) {
                    bluetooth.getConnecting().cancel();
                    bluetooth.setConnecting(null);

                    bluetooth.connectToUnit((BluetoothDevice) device, thisActivity);
                    updatePairedList();
                }
                connectedDevice = (BluetoothDevice) device;
                updateBluetoothDevicesList();
            }

        });

        Button backButton = findViewById(R.id.getBack);
        backButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                clearDiscoveredDeviceList();
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

    }




    public void initialiseBluetooth() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
        recieverRegistered = true;
    }

    public void updateBluetoothDevicesList() {
        bluetoothDiscoveredMap = updateMap(bluetoothDiscoveredMap);
        bluetoothListAdapter = new BluetoothListAdapter(thisContext, bluetoothDiscoveredMap);
        bluetoothListView.setAdapter(bluetoothListAdapter);
    }

    public void updatePairedList() {
        pairedDevicesList = new ArrayList<>(bluetooth.getBluetoothAdapter().getBondedDevices());
        for (BluetoothDevice device : pairedDevicesList) {
            removeDiscoveredDeviceList(device);
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        if(recieverRegistered) {
            unregisterReceiver(mReceiver);
        }
    }

    public List<BluetoothDevice> getDiscoveredDevices() {
        return discoveredDevices;
    }

    public List<BluetoothDevice> getPairedDevicesList() {
        return pairedDevicesList;
    }

    public Map<String, DevicePair> updateMap(Map map) {
        updatePairedList();
        for (BluetoothDevice device : getPairedDevicesList()) {
            if (bluetooth.getConnecting() != null && bluetooth.getConnecting().getConnectedDevice() != null && bluetooth.getConnecting().getConnectedDevice().equals(device)) {
                // if (map.containsKey(connecting.getConnectedDevice().getName())) { map.remove(connecting.getConnectedDevice().getName());}
                Log.e("DeviceStatus", "Device connected");
                map.put(device.getName(), new DevicePair(device, "Connected"));
                bluetoothTextView.setText("Connected to device");
            } else {
                Log.e("DeviceStatus","Device paired");
                map.put(device.getName(), new DevicePair(device, "Paired"));
            }

        }
        for (BluetoothDevice device : getDiscoveredDevices()) {
            map.put(device.getName(), new DevicePair(device, ""));
        }
        Log.e("Updated map", "Discovered: "+discoveredDevices + "Paired: "+pairedDevicesList );
        return map;
    }

    public void addDiscoveredDeviceList(BluetoothDevice device) {
        discoveredDevices.add(device);
    }

    public void removeDiscoveredDeviceList(BluetoothDevice device) {
        discoveredDevices.remove(device);
    }


    public void clearDiscoveredDeviceList() {
        discoveredDevices.clear();
    }


}
