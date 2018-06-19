package com.example.mysqlexampleproject;

import android.bluetooth.BluetoothDevice;

public class DevicePair {

    private final BluetoothDevice device;

    private final String status;

    //And object that contains a Bluetooth device and a string saying whether it is connected, paired or discovered
    public DevicePair(BluetoothDevice device, String status) {
        this.device = device;
        this.status = status;

    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public String getStatus() {
        return status;
    }


}
