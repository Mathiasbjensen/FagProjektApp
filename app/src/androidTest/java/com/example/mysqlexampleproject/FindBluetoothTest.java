package com.example.mysqlexampleproject;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class FindBluetoothTest {
    private Bluetooth mBluetooth;
    FindBluetoothActivity bluetoothActivity;

    @Rule
    public ActivityTestRule<FindBluetoothActivity> mainActivityRule = new ActivityTestRule<>(FindBluetoothActivity.class);



    @Before
    public void createBluetooth() {
        bluetoothActivity = mainActivityRule.getActivity();
        mBluetooth = new Bluetooth();

    }

    @Test
    public void addToDiscoverList() {

        Set<BluetoothDevice> resultSet = mBluetooth.getBluetoothAdapter().getBondedDevices();
        for (BluetoothDevice device : resultSet) {
            bluetoothActivity.addDiscoveredDeviceList(device);
        }
        List<BluetoothDevice> result = new ArrayList<>();
        result.addAll(resultSet);
        assertThat(bluetoothActivity.getDiscoveredDevices(), is(result));
    }

    @Test
    public void addToPairedList() {

        Set<BluetoothDevice> resultSet = mBluetooth.getBluetoothAdapter().getBondedDevices();
        bluetoothActivity.updatePairedList();
        List<BluetoothDevice> result = new ArrayList<>();
        result.addAll(resultSet);
        assertThat(bluetoothActivity.getPairedDevicesList(), is(result));
    }

    @Test
    public void updateMap() {
        Map<String, DevicePair> map1 = new HashMap<>();
        for (BluetoothDevice device : bluetoothActivity.getPairedDevicesList()) {
            if (bluetoothActivity.bluetooth.getConnecting() != null && bluetoothActivity.bluetooth.getConnecting().getConnectedDevice() != null && bluetoothActivity.bluetooth.getConnecting().getConnectedDevice().equals(device)) {
                map1.put(device.getName(), new DevicePair(device, "Connected"));
            } else {
                map1.put(device.getName(), new DevicePair(device, "Paired"));
            }
        }
        for (BluetoothDevice device : bluetoothActivity.getDiscoveredDevices()) {
            map1.put(device.getName(), new DevicePair(device, ""));
        }
        Map<String, DevicePair> map2 = new HashMap<>();
        map2 = bluetoothActivity.updateMap(map2);

        assertThat(map2.size(), is(map1.size()));
    }

    @Test
    public void initialiseBluetoothTest() {
        bluetoothActivity.initialiseBluetooth();
        assertThat(bluetoothActivity.recieverRegistered,is(true));
    }

    @Test
    public void clearDiscoveredList() {
        bluetoothActivity.clearDiscoveredDeviceList();
        assertThat(bluetoothActivity.getDiscoveredDevices().isEmpty(), is(true));
    }


}
