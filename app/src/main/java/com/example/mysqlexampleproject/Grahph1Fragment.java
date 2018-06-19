package com.example.mysqlexampleproject;

import android.app.Fragment;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;

public class Grahph1Fragment extends android.support.v4.app.Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.graphs, container,false);
        LineGraphSeries<DataPoint> BERGraphSeries = new LineGraphSeries<DataPoint>();
        List<DataPoint> BERseries = (List<DataPoint>) getArguments().getSerializable("BERSeries");
        for (DataPoint data : BERseries) {
            BERGraphSeries.appendData(data, true, BERseries.size());
        }
        GraphView graph = (GraphView) view.findViewById(R.id.graph1);
        graph.addSeries(BERGraphSeries);

        return view;
    }

}
