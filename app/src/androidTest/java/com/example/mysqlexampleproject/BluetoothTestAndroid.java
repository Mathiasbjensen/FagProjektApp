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
        assertThat(Bluetooth.isBluetoothEnabled(),is(true));
    }
    @Test
    public void setBluetoothAdapter() {
        mBluetooth.setBluetoothAdapter(BluetoothAdapter.getDefaultAdapter());
        assertThat(mBluetooth.getBluetoothAdapter(), is(BluetoothAdapter.getDefaultAdapter()));

    }

    @Test
    public void checkBluetoothAvailability() {
        assertThat(Bluetooth.checkBluetoothAvailability(), is(true));
    }







    /*Test
    public void discoverDevices() {
        mBluetooth.discoverDevices();
        assertThat(mBluetooth.isBluetoothAdapterDiscovering(), is(true));
    }*/






}