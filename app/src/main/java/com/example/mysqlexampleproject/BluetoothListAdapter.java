package com.example.mysqlexampleproject;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class BluetoothListAdapter extends BaseAdapter {

    LayoutInflater mInflator;
    Map<String, DevicePair> map;
    List<String> names;
    List<DevicePair> devices;

    //Initialises an adapter which contains the devices and wether they are paired, connected or discovered.
    // This is put in a ListViewer.
    public BluetoothListAdapter(Context c,Map m){
        mInflator = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        map = m;
        names = new ArrayList<>(map.keySet());
        devices = new ArrayList<>(map.values());
    }
    // Implemented methods from the ItemAdapter class.
    @Override
    public int getCount() {
        return map.size();
    }

    @Override
    public BluetoothDevice getItem(int position) {
        return devices.get(position).getDevice();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = mInflator.inflate(R.layout.bluetooth_item_layout, null);
        // find each textvies
        TextView nameTextView = (TextView) v.findViewById(R.id.nameTextView);
        TextView deviceStatus = (TextView) v.findViewById(R.id.deviceStatus);
        // set values of names and ages referring to the lists.
        nameTextView.setText(names.get(position));
        deviceStatus.setText((devices.get(position)).getStatus());


        return v;
    }
}