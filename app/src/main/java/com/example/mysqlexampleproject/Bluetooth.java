package com.example.mysqlexampleproject;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static android.content.ContentValues.TAG;
import static android.support.v4.app.ActivityCompat.startActivityForResult;

/**
 * Created by Frede on 31-05-2018.
 */

public class Bluetooth extends Activity {

    public BluetoothAdapter mBluetoothAdapter;
    private final static int REQUEST_ENABLE_BT = 1;
    public List<BluetoothDevice> discoveredDevices = new ArrayList<>();
    public List<BluetoothDevice> pairedDevicesList = new ArrayList<>();
    public ConnectThread connecting;

    public Bluetooth() {
        BluetoothAdapter mBluetoothAdapter = verifyBluetoothSupport();
        if (mBluetoothAdapter != null) {
            enableBluetooth(mBluetoothAdapter);
        }
    }

        private BluetoothAdapter verifyBluetoothSupport() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter;
    }

    private void enableBluetooth(BluetoothAdapter mBluetoothAdapter) {
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(mBluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    public void pairedDevices() {
        pairedDevicesList.clear();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                pairedDevicesList.add(device);
            }
        }
    }

    public void discoverDevices(Activity activity) {
        int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
        if (mBluetoothAdapter.isDiscovering()) {mBluetoothAdapter.cancelDiscovery();}
        mBluetoothAdapter.startDiscovery();
        }

    private void enableDiscoverOfApp() {
        Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);
    }

    public void connectToUnit(BluetoothDevice device, Activity activity) {
        connecting = new ConnectThread(device, mBluetoothAdapter, activity);
        connecting.start();
    }

    //Laver en receiver, skal være der for at kunne discover devices
    public final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!pairedDevicesList.contains(device)) {
                    Log.e("Found device", "" + device.getName());
                    discoveredDevices.add(device);
                }
            }
        }
    };


}
