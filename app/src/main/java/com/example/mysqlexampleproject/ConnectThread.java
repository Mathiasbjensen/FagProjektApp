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

public class ConnectThread {
    private BluetoothSocket mmSocket;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final BluetoothAdapter mBluetoothAdapter;
    private OutputStream mmOutStream;
    private final int REQUEST_READ_PHONE_STATE = 1;
    private BluetoothDevice tempDevice;
    private BluetoothDevice connectedDevice;

    //Initialises a connection with a BluetoothDevice and the local devices BluetoothAdapter
    public ConnectThread(BluetoothDevice device, BluetoothAdapter tempBluetoothAdapter) {
        BluetoothSocket tmp = null;
        tempDevice = device;
        mBluetoothAdapter = tempBluetoothAdapter;
        try {
            //Creates socket with hardcoded UUID to the HC05. Same UUID used on the server side
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            Log.e(TAG, "Failed to create Socket", e);
        }

        mmSocket = tmp;
    }

    //Initiate connection with device by asking for permission
    public void initiateConnection(Activity activity) {
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
            Log.e(TAG, "OutputStream "+mmOutStream);
            mmOutStream.write(b);
            Log.e(TAG, "Sending Successful. Sending: "+stringToSend);
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when sending data", e);

        }
    }


    //connects to the soccet and cancels discovery of other devices.
    private void connectSocket() {
        mBluetoothAdapter.cancelDiscovery();
        try {

            mmSocket.connect();
            connectedDevice = tempDevice;

            Log.e("ConnectHC05Status", "Connected1");

            } catch (IOException connectException) {

                Log.e(TAG, "Unable to connect socket", connectException);
                connectedDevice = null;
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }


            initialiseOutputStream(mmSocket);
    }

    //Initialises and Outputstream which the app can send data through
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

    //Closes the socket
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the client socket", e);
        }
    }



    public BluetoothDevice getConnectedDevice() {
        return connectedDevice;
    }
}

