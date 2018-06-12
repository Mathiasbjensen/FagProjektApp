package com.example.mysqlexampleproject;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class BluetoothTestAndroid {
    private Bluetooth mBluetooth;
    Activity mainActivity;

    @Rule
    public ActivityTestRule<MainActivity> mainActivityRule = new ActivityTestRule<>(MainActivity.class);



    @Before
    public void createBluetooth() {
        mainActivity = mainActivityRule.getActivity();
        mBluetooth = new Bluetooth();

    }



    @Test
    public void enableBluetooth() {
        mBluetooth.enableBluetooth(mainActivity);
        assertThat(mBluetooth.isBluetoothEnabled(),is(true));
    }
    @Test
    public void setBluetooth() {
        mBluetooth.setBluetoothAdapter(BluetoothAdapter.getDefaultAdapter());
        assertThat(mBluetooth.getBluetoothAdapter(), is(BluetoothAdapter.getDefaultAdapter()));

    }

    @Test
    public void checkBluetoothAvailability() {
        assertThat(Bluetooth.checkBluetoothAvailability(), is(false));
    }

    @Test
    public void getBluetoothAdapter() {

        android.bluetooth.BluetoothAdapter result = BluetoothAdapter.getDefaultAdapter();
        assertThat(mBluetooth.getBluetoothAdapter(),is(result));
    }

    @Test
    public void addToDiscoverList() {

        Set<BluetoothDevice> resultSet = mBluetooth.getBluetoothAdapter().getBondedDevices();
        for (BluetoothDevice device : resultSet) {
            mBluetooth.addDiscoveredDeviceList(device);
        }
        List<BluetoothDevice> result = new ArrayList<>();
        result.addAll(resultSet);
        assertThat(mBluetooth.getDiscoveredDevices(), is(result));
    }

    @Test
    public void addToPairedList() {

        Set<BluetoothDevice> resultSet = mBluetooth.getBluetoothAdapter().getBondedDevices();
        for (BluetoothDevice device : resultSet) {
            mBluetooth.updatePairedDeviceList(device);
        }
        List<BluetoothDevice> result = new ArrayList<>();
        result.addAll(resultSet);
        assertThat(mBluetooth.getPairedDevicesList(), is(result));
    }

    @Test
    public void updateMap() {
        Map<String, DevicePair> map1= Collections.emptyMap();
        for (BluetoothDevice device : mBluetooth.getPairedDevicesList()) {
            map1.put(device.getName(), new DevicePair(device,"Paired"));
        }
        for (BluetoothDevice device : mBluetooth.getDiscoveredDevices()) {
            map1.put(device.getName(), new DevicePair(device, ""));
        }
        Map<String, DevicePair> map2 = Collections.emptyMap();
        map2 = mBluetooth.updateMap(map2);

        assertThat(map2, is(map1));

    }


    /*Test
    public void discoverDevices() {
        mBluetooth.discoverDevices();
        assertThat(mBluetooth.isBluetoothAdapterDiscovering(), is(true));
    }*/






}