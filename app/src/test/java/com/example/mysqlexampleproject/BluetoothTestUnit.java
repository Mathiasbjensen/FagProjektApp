package com.example.mysqlexampleproject;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replay;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.riis.androidarduino.lib.BluetoothComm;
import com.riis.androidarduino.lib.FlagMsg;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ BluetoothSocket.class, BluetoothDevice.class, BluetoothAdapter.class, Activity.class })
public class BluetoothTestUnit {

    private BluetoothAdapter mockAdapter;
    private BluetoothSocket mockSocket;
    private ByteArrayOutputStream mockOutputStream;

    @Before
    public void setup() {
        mockAdapter = createMock(BluetoothAdapter.class);
        mockStatic(BluetoothAdapter.class);
        expect(BluetoothAdapter.getDefaultAdapter()).andReturn(mockAdapter);
    }


    @Test
    public void setBluetooth() {
        Bluetooth mBluetooth = new Bluetooth();
        mBluetooth.setBluetoothAdapter(BluetoothAdapter.getDefaultAdapter());
        assertThat(mBluetooth.getBluetoothAdapter(), is(BluetoothAdapter.getDefaultAdapter()));

    }

    @Test
    public void checkBluetoothAvailability() {
        assertThat(Bluetooth.checkBluetoothAvailability(), is(false));
    }

    @Test
    public void getBluetoothAdapter() {
        Bluetooth mBluetooth = new Bluetooth();
        android.bluetooth.BluetoothAdapter result = BluetoothAdapter.getDefaultAdapter();
        assertThat(mBluetooth.getBluetoothAdapter(),is(result));
    }

    @Test
    public void addToDiscoverList() {

        Bluetooth mBluetooth = new Bluetooth();
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

        Bluetooth mBluetooth = new Bluetooth();
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
        Bluetooth mBluetooth = new Bluetooth();
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

}
