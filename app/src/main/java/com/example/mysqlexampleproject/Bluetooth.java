package com.example.mysqlexampleproject;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
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


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.content.ContentValues.TAG;
import static android.support.v4.app.ActivityCompat.startActivityForResult;

/**
 * Created by Frede on 31-05-2018.
 */

public class Bluetooth implements Serializable {

    private static BluetoothAdapter mBluetoothAdapter;
    private final static int REQUEST_ENABLE_BT = 1;
    private static ConnectThread connecting;

    public Bluetooth() {
        setBluetoothAdapter(BluetoothAdapter.getDefaultAdapter());
    }

    public void enableBluetooth(Activity activity) {
        if (!mBluetoothAdapter.isEnabled()) {
            if(activity instanceof MainActivity){
                Intent enableBtIntent = new Intent(mBluetoothAdapter.ACTION_REQUEST_ENABLE);
                (activity).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }

        }
    }

    public void discoverDevices(Activity activity) {
        int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
        if (isBluetoothAdapterDiscovering()) {getBluetoothAdapter().cancelDiscovery();}
        getBluetoothAdapter().startDiscovery();
    }



    public void connectToUnit(BluetoothDevice device, Activity activity) {
        setConnecting(new ConnectThread(device, getBluetoothAdapter()));
        connecting.initiateConnection(activity);
    }

    public Boolean isBluetoothAdapterDiscovering() {
        return getBluetoothAdapter().isDiscovering();
    }





    public static Boolean isBluetoothEnabled() {
        return mBluetoothAdapter.isEnabled();
    }

    public ConnectThread getConnecting() {
        return connecting;
    }

    public void setConnecting(ConnectThread connecting) {
        this.connecting = connecting;
    }



    public static Boolean checkBluetoothAvailability() {
        return (mBluetoothAdapter != null);
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    public void setBluetoothAdapter(BluetoothAdapter settingBluetoothAdapter) {
        this.mBluetoothAdapter = settingBluetoothAdapter;
    }

}
