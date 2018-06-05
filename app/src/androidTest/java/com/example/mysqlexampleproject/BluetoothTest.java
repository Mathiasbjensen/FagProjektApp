package com.example.mysqlexampleproject;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import org.junit.Before;
import org.junit.Test;
import android.support.test.runner.AndroidJUnit4;
import android.util.Pair;
import org.junit.Test;
import static org.hamcrest.Matchers.is;

import static org.junit.Assert.assertThat;
import static android.support.test.InstrumentationRegistry.getContext;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static org.junit.Assert.*;

public class BluetoothTest {
    private Bluetooth mBluetooth;

    @Before
    public void createBluetooth() {
        mBluetooth = new Bluetooth(getTargetContext());
    }

    @Test
    public void checkBluetoothAvailability() {
            assertThat(mBluetooth.checkBluetoothAvailability(), is(true));
    }

    @Test
    public void setBluetooth() {
        mBluetooth.setBluetoothAdapter(BluetoothAdapter.getDefaultAdapter());
        assertThat(mBluetooth.getBluetoothAdapter(), is(BluetoothAdapter.getDefaultAdapter()));

    }

    @Test
    public void isBluetoothEnabled() {
        assertThat(mBluetooth.isBluetoothEnabled(),is(true));
    }


}