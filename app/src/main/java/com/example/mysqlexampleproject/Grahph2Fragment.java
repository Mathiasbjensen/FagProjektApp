package com.example.mysqlexampleproject;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.List;

public class Grahph2Fragment extends android.support.v4.app.Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.graphs2, container,false);
        LineGraphSeries<DataPoint> UTIGraphSeries = new LineGraphSeries<DataPoint>();
        List<DataPoint> UTIseries = (List<DataPoint>) getArguments().getSerializable("UTISeries");
        for (DataPoint data : UTIseries) {
            UTIGraphSeries.appendData(data, true, UTIseries.size());
        }
        GraphView graph = (GraphView) view.findViewById(R.id.graph1);
        graph.addSeries(UTIGraphSeries);
        
        return view;
    }
}
