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

public class Bluetooth {

    private static BluetoothAdapter mBluetoothAdapter;
    private final static int REQUEST_ENABLE_BT = 1;
    public List<BluetoothDevice> discoveredDevices = new ArrayList<>();
    public List<BluetoothDevice> pairedDevicesList = new ArrayList<>();
    private Context thisContext;

    public Bluetooth(Context context) {
        thisContext = context;
        setBluetoothAdapter(BluetoothAdapter.getDefaultAdapter());
        if (checkBluetoothAvailability()) {
            enableBluetooth();
        }
    }

    public Boolean checkBluetoothAvailability() {
        return (mBluetoothAdapter != null);
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    public void setBluetoothAdapter(BluetoothAdapter settingBluetoothAdapter) {
        mBluetoothAdapter = settingBluetoothAdapter;
    }

    public void enableBluetooth() {
        if (!mBluetoothAdapter.isEnabled()) {
            if(thisContext instanceof MainActivity){
                Intent enableBtIntent = new Intent(mBluetoothAdapter.ACTION_REQUEST_ENABLE);
                ((MainActivity)thisContext).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }

        }
    }

    public Boolean isBluetoothEnabled() {
        return mBluetoothAdapter.isEnabled();
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
