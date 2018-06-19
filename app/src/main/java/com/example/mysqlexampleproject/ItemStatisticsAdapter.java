package com.example.mysqlexampleproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mysqlexampleproject.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Mathi on 24-04-2018.
 */

public class ItemStatisticsAdapter extends BaseAdapter {

    LayoutInflater mInflator;
    Map<String, AveragePair> map;
    List<String> variables;
    List<AveragePair> avgPair;

    //Initialises an adapter which contains information from the database. This is put in a ListViewer.
    public ItemStatisticsAdapter(Context c,Map m){
        mInflator = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        map = m;
        variables = new ArrayList<String>(map.keySet());
        avgPair = new ArrayList<AveragePair>(map.values());
    }

    // Implemented methods from the ItemAdapter class.
    @Override
    public int getCount() {
        return map.size();
    }

    @Override
    public Object getItem(int position) {
        return variables.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = mInflator.inflate(R.layout.listrow, null);
        // find each textvies
        TextView column1 = (TextView) v.findViewById(R.id.column1);
        TextView column2 = (TextView) v.findViewById(R.id.column2);
        TextView column3 = (TextView) v.findViewById(R.id.column3);
        // set values of names and ages referring to the lists.
        column1.setText(variables.get(position));
        column2.setText((avgPair.get(position).getAvg()) + "");
        column3.setText((avgPair.get(position).getCurrent()) + "");

        return v;
    }
}
