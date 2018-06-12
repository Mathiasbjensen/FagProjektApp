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

public class Bluetooth {

    private static BluetoothAdapter mBluetoothAdapter;
    private final static int REQUEST_ENABLE_BT = 1;
    private List<BluetoothDevice> discoveredDevices = new ArrayList<>();
    private List<BluetoothDevice> pairedDevicesList = new ArrayList<>();
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

    public void updatePairedList() {
        pairedDevicesList = new ArrayList<>(getBluetoothAdapter().getBondedDevices());
        for (BluetoothDevice device : pairedDevicesList) {
            removeDiscoveredDeviceList(device);
        }
    }

    public void connectToUnit(BluetoothDevice device, Activity activity) {
        setConnecting(new ConnectThread(device, getBluetoothAdapter()));
        connecting.initiateConnection(activity);
        updatePairedList();
    }

    public Boolean isBluetoothAdapterDiscovering() {
        return getBluetoothAdapter().isDiscovering();
    }


    public List<BluetoothDevice> getDiscoveredDevices() {
        return discoveredDevices;
    }

    public List<BluetoothDevice> getPairedDevicesList() {
        return pairedDevicesList;
    }

    public Map<String, DevicePair> updateMap(Map map) {


        for (BluetoothDevice device : pairedDevicesList) {
            Log.e("Connection status", ""+connecting.getConnectedDevice()+" : "+device);
            if (connecting != null && connecting.getConnectedDevice().equals(device)) {
               // if (map.containsKey(connecting.getConnectedDevice().getName())) { map.remove(connecting.getConnectedDevice().getName());}
                Log.e("DeviceStatus", "Device connected");
                map.put(device.getName(), new DevicePair(device, "Connected"));
            } else {
                Log.e("DeviceStatus","Device paired");
                map.put(device.getName(), new DevicePair(device, "Paired"));
            }

        }
        for (BluetoothDevice device : discoveredDevices) {
                map.put(device.getName(), new DevicePair(device, ""));
        }
        Log.e("Updated map", "Discovered: "+discoveredDevices + "Paired: "+pairedDevicesList );
        return map;
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

    public void addDiscoveredDeviceList(BluetoothDevice device) {
        discoveredDevices.add(device);
    }

    public void removeDiscoveredDeviceList(BluetoothDevice device) {
        discoveredDevices.remove(device);
    }

    public void updatePairedDeviceList(BluetoothDevice device) {
        pairedDevicesList.add(device);
    }

    public void clearDiscoveredDeviceList() {
        discoveredDevices.clear();
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
