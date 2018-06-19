package com.example.mysqlexampleproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ChangeStatistics extends Activity {

    private Context thisContext;
    private Activity thisActivity;
    ListView avgListView;
    ArrayList<DataPoint> BERseries;
    ArrayList<DataPoint> UTIseries;
    ArrayList<DataPoint> FPSseries;

    HashMap<String, AveragePair> averageMap = new HashMap<>();
    HashMap<String, AveragePair> mostUsedMap = new HashMap<>();
    ListView mostUsedListView;
    Map<String, String> personMap = new LinkedHashMap<>();
    String password;
    Spinner StatDropDown;
    EditText statEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisContext= this;
        thisActivity=this;
        BERseries = (ArrayList<DataPoint>) getIntent().getExtras().getSerializable("BERSeries");
        UTIseries = (ArrayList<DataPoint>) getIntent().getExtras().getSerializable("UTISeries");
        FPSseries = (ArrayList<DataPoint>) getIntent().getExtras().getSerializable("FPSSeries");
        averageMap = (HashMap<String, AveragePair>) getIntent().getExtras().getSerializable("AvgMap");
        mostUsedMap = (HashMap<String, AveragePair>) getIntent().getExtras().getSerializable("CommonMap");
        setPassword(getIntent().getStringExtra("Password"));
        changeContentSeeStat();
    }

    public void setPassword(String passwordString) {
        this.password = passwordString;
    }

    private void changeContentSeeStat() {
        setContentView(R.layout.statistics);

        this.avgListView = findViewById(R.id.avgListView);
        StatDropDown = findViewById(R.id.StatDropDown);
        String[] StatItems = new String[]{"Min", "Hours", "Days", "Weeks"};
        ArrayAdapter<String> StatAdapter = new ArrayAdapter<>(thisContext, android.R.layout.simple_spinner_dropdown_item, StatItems);
        StatDropDown.setAdapter(StatAdapter);
        StatDropDown.setSelection(3);
        statEdit = findViewById(R.id.statInput);

        if (averageMap.size() > 0 ) {

            ItemStatisticsAdapter itemStatisticsAdapter = new ItemStatisticsAdapter(thisContext, averageMap);
            avgListView.setAdapter(itemStatisticsAdapter);
        }

        this.mostUsedListView = findViewById(R.id.commonListView);

        if (mostUsedMap.size() > 0 ) {

            ItemStatisticsAdapter itemStatisticsAdapter = new ItemStatisticsAdapter(thisContext, mostUsedMap);
            mostUsedListView.setAdapter(itemStatisticsAdapter);
        }

        Button updateButton = findViewById(R.id.updateStat);
        updateButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                initiateGetDataStatistics();
            }
        });

        Button mainMenuButton = findViewById(R.id.mainMenuButton);
        mainMenuButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });

        Button getDataButton = findViewById(R.id.backToGetData);
        getDataButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        Button goToGraphsButton = findViewById(R.id.graphButton);
        goToGraphsButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                changeContentGetGraphs();
            }
        });

    }

    public void initiateGetDataStatistics() {
        int result = 168;
        switch (StatDropDown.getSelectedItem().toString()) {
            case "Min": result = (Integer.parseInt(statEdit.getText().toString()))/60; break;
            case "Hours": result = Integer.parseInt(statEdit.getText().toString()); break;
            case "Days": result = Integer.parseInt(statEdit.getText().toString())*24; break;
            case "Weeks": result = Integer.parseInt(statEdit.getText().toString())*7*24; break;
        }
        Log.e("Result", result+"");
        GetData retrieveData = new GetData(result);
        retrieveData.execute("");
    }

    public Adapter getMoustUsedAdapter() {
        return mostUsedListView.getAdapter();
    }

    public Adapter getAverageAdapter() {
        return avgListView.getAdapter();
    }




    public void changeContentGetGraphs() {
        Intent intent = new Intent(thisActivity, GraphTabs.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("BERSeries", BERseries);
        bundle.putSerializable("UTISeries", UTIseries);
        bundle.putSerializable("FPSSeries", FPSseries);
        intent.putExtras(bundle);
        startActivityForResult(intent, 4);
    }


    //Gets data from the database and displays it. Almost the same class is used and explained in greater details in getDataActivity

    private class GetData extends AsyncTask<String, String, String> {

        static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        static final String DB_URL = "jdbc:mysql://" +
                DbStrings.DATABASE_URL + "/" +
                DbStrings.DATABASE_NAME;
        final int hours;

        public GetData(int hours) {
            this.hours = hours;
        }

        @Override
        protected String doInBackground(String... strings) {
            mostUsedMap.clear();
            averageMap.clear();
            Connection conn = null;
            Statement stmt = null;
            String[] databasequery = {"PAM","BER", "ERR", "SYN", "UTI", "VCL", "FPS", "RES", "JIT", "PLT", "PDT"};
            String[] avgDatabaseQuery = {"BER", "UTI", "FPS"};
            String[] mostUsedDatabaseQuery = {"VCL", "RES", "PAM"};
            try {
                Class.forName(JDBC_DRIVER);
                conn = DriverManager.getConnection(DB_URL, DbStrings.USERNAME, "skod");
                stmt = conn.createStatement();
                String sql = "CALL GetCurrentValues()";
                ResultSet rs = stmt.executeQuery(sql);
                while(rs.next()) {
                    int i = 1;
                    for (String string : databasequery) {
                        String value = rs.getString(i);
                        Log.e("VALUE: ", string + " " + value);
                        personMap.put(string, value);
                        i++;
                    }
                }
                rs.close();
                sql = "Call AVGValues("+hours+");";
                rs = stmt.executeQuery(sql);

                while(rs.next()) {
                    int i = 1;
                    for (String string : avgDatabaseQuery) {
                        Double value = rs.getDouble(i);
                        Log.e("Avg Var: ", "" + string);
                        averageMap.put(string, new AveragePair(value, Double.parseDouble(personMap.get(string))));
                        i++;
                    }
                }
                rs.close();

                sql = "Call MostUsedVar("+hours+");";
                rs = stmt.executeQuery(sql);

                if (!rs.isBeforeFirst()) {
                    for (String string : mostUsedDatabaseQuery) {
                        mostUsedMap.put(string, new AveragePair(-1.0, Double.parseDouble(personMap.get(string))));
                    }
                }

                while(rs.next()) {
                    int i = 1;
                    for (String string : mostUsedDatabaseQuery) {
                        Double value = rs.getDouble(i);
                        Log.e("VALUE: ", "" + string);
                        if (string == "PAM") { value = Math.pow(2,value+1); }
                        mostUsedMap.put(string, new AveragePair(value, Double.parseDouble(personMap.get(string))));
                        Log.e("PersonMap", personMap.get(string));
                        i++;
                    }
                }
                rs.close();


                stmt.close();
                conn.close();

            } catch (SQLException connError) {
                connError.printStackTrace();;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {

                try {
                    if (stmt != null) {
                        stmt.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }


            return null;
        }

        @Override
        protected void onPostExecute(String msg) {


                if (averageMap.size() > 0) {

                    ItemStatisticsAdapter itemStatisticsAdapter = new ItemStatisticsAdapter(thisContext, averageMap);
                    avgListView.setAdapter(itemStatisticsAdapter);
                }


                if (mostUsedMap.size() > 0) {

                    ItemStatisticsAdapter itemStatisticsAdapter = new ItemStatisticsAdapter(thisContext, mostUsedMap);
                    mostUsedListView.setAdapter(itemStatisticsAdapter);
                }


        }

    }



}
