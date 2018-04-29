package com.example.mysqlexampleproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Mathi on 24-04-2018.
 */

public class ItemAdapter extends BaseAdapter {

    LayoutInflater mInflator;
    Map<String, Double> map;
    List<String> names;
    List<Double> ages;

    // Constructor, context = which screen opens it op. Map is essentially a dictionary.
    public ItemAdapter(Context c,Map m){
        mInflator = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        map = m;
        names = new ArrayList<String>(map.keySet());
        ages = new ArrayList<Double>(map.values());
    }
    // Implemented methods from the ItemAdapter class.
    @Override
    public int getCount() {
        return map.size();
    }

    @Override
    public Object getItem(int position) {
        return names.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    // How the information should be presented.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = mInflator.inflate(R.layout.item_layout, null);
        // find each textvies
        TextView nameTextView = (TextView) v.findViewById(R.id.nameTextView);
        TextView ageTextView = (TextView) v.findViewById(R.id.ageTextView);
        // set values of names and ages referring to the lists.
        nameTextView.setText(names.get(position));
        ageTextView.setText(ages.get(position).toString());

        return v;
    }
}
