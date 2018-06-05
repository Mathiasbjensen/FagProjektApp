package com.example.mysqlexampleproject;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import static android.content.ContentValues.TAG;

public class ConnectThread extends Thread {
    private BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final BluetoothAdapter mBluetoothAdapter;
    private OutputStream mmOutStream;
    private final int REQUEST_READ_PHONE_STATE = 1;
    private final Activity activity;

    public ConnectThread(BluetoothDevice device, BluetoothAdapter tempBluetoothAdapter, Activity activity) {
        this.activity = activity;
        BluetoothSocket tmp = null;
        mmDevice = device;
        mBluetoothAdapter = tempBluetoothAdapter;
        try {
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            Log.e(TAG, "Failed to create Socket", e);
        }

        mmSocket = tmp;

    }

    public void run() {

        int permissionCheck = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        } else {
            //TODO
        }

        connectSocket();

    }


    public void write(String stringToSend) {
        byte[] b = stringToSend.getBytes();
        try {
            mmOutStream.write(b);
            Log.e(TAG, "Sending Succesfull. Sending: "+stringToSend);
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when sending data", e);

        }
    }

    // Closes the client socket and causes the thread to finish.

    private void connectSocket() {
        mBluetoothAdapter.cancelDiscovery();
        try {

            mmSocket.connect();
            Log.e("ConnectHC05Status", "Connected1");

            } catch (IOException connectException) {

                Log.e(TAG, "Unable to connect socket", connectException);
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }


        initialiseOutputStream(mmSocket);
    }

    public void initialiseOutputStream(BluetoothSocket socket) {

        OutputStream tmpOut = null;
        try {
            tmpOut = socket.getOutputStream();
            Log.e("OutputStreamStatus", "Connected");
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating output stream", e);
        }

        mmOutStream = tmpOut;
    }

    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the client socket", e);
        }
    }

}

