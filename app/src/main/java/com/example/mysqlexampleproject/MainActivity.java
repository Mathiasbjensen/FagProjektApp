package com.example.mysqlexampleproject;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ItemAdapter itemAdapter;
    BluetoothAdapter bluetoothAdapter;
    static Context thisContext;
    static Activity thisActivity;
    ListView myListView;
    ListView bluetoothListView;
    ListView avgListView;
    ListView mostUsedListView;
    TextView progressTextView;
    TextView PAMTextView;
    TextView PAMTextViewData;
    TextView VCLTextView;
    TextView VCLTextViewData;
    TextView FPSTextView;
    TextView FPSTextViewData;
    TextView RESTextView;
    TextView RESTextViewData;
    Boolean recieverRegistered = false;
    LineGraphSeries<DataPoint> BERseries;
    LineGraphSeries<DataPoint> UTIseries;
    LineGraphSeries<DataPoint> FPSseries;
    Map<String, String> personMap =
            new LinkedHashMap<>();
    private Bluetooth bluetooth;
    Map<String, DevicePair> bluetoothDiscoveredMap = new LinkedHashMap<>();
    Map<String, AveragePair> averageMap = new LinkedHashMap<>();
    Map<String, AveragePair> mostUsedMap = new LinkedHashMap<>();


    //Laver en receiver, skal være der for at kunne discover devices
    public final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!(bluetooth.getPairedDevicesList().contains(device) || bluetooth.getDiscoveredDevices().contains(device) || device.getName()==null)) {
                    Log.e("Found device", "" + device.getName());
                    bluetooth.addDiscoveredDeviceList(device);
                    updateBluetoothDevicesList();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisContext = this;
        thisActivity = this;
        bluetooth = new Bluetooth();
        changeContentMainMenu();

    }


    public void changeContentMainMenu() {
        setContentView(R.layout.main_menu);
        Button goToGet = findViewById(R.id.goToGetData);
        Button goToBlue = findViewById(R.id.goToBluetooth);
        Button goToChange = findViewById(R.id.goToChangeData);


        goToGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeContentGetData();
            }
        });

        goToBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeContentGetBluetooth();
            }
        });

        goToChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetooth.getConnecting() != null) {
                    changeContentChangeData();
                }
            }
        });
    }

    public void changeContentChangeData() {
        setContentView(R.layout.change_data2);
        final Spinner pamDropDown = findViewById(R.id.pamDropDown);
        final Spinner VCLDropDown = findViewById(R.id.VCLDropDown);
        final Spinner FPSDropDown = findViewById(R.id.FPSDropDown);
        PAMTextView = findViewById(R.id.PAMTextView);
        PAMTextViewData = findViewById(R.id.PAMTextViewData);
        VCLTextView = findViewById(R.id.VCLTextView);
        VCLTextViewData = findViewById(R.id.VCLTextViewData);
        FPSTextView = findViewById(R.id.FPSTextView);
        FPSTextViewData = findViewById(R.id.FPSTextViewData);
        RESTextView = findViewById(R.id.RESTextView);
        RESTextViewData = findViewById(R.id.RESTextViewData);

        GetCurrentData retrieveData = new GetCurrentData();
        retrieveData.execute("");

        String[] pamItems = new String[]{"2 PAM", "4 PAM", "8 PAM"};
        ArrayAdapter<String> pamAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, pamItems);
        pamDropDown.setAdapter(pamAdapter);

        String[] VCLItems = new String[]{"1", "2", "3"};
        ArrayAdapter<String> VCLAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, VCLItems);
        VCLDropDown.setAdapter(VCLAdapter);

        String[] FPSItems = new String[]{"1", "2", "3"};
        ArrayAdapter<String> FPSAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, FPSItems);
        FPSDropDown.setAdapter(FPSAdapter);

        Button sendDataPam = findViewById(R.id.sendDataPam);
        sendDataPam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    bluetooth.getConnecting().write(whatToSend(pamDropDown.getSelectedItem().toString()));

            }
        });

        Button sendDataVCL = findViewById(R.id.sendDataVCL);
        sendDataVCL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    bluetooth.getConnecting().write(whatToSend(VCLDropDown.getSelectedItem().toString()));
            }
        });

        Button sendDataFPS = findViewById(R.id.sendDataFPS);
        sendDataFPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    bluetooth.getConnecting().write(whatToSend(FPSDropDown.getSelectedItem().toString()));
            }
        });


        Button backButton = findViewById(R.id.getBack);
        backButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                changeContentMainMenu();
            }
        });



    }

    public void changeContentGetBluetooth() {
        setContentView(R.layout.activity_connect_to_bluetooth);
        bluetoothListView = findViewById(R.id.DeviceListView);
        final TextView bluetoothTextView = findViewById(R.id.bluetoothStatus);
        bluetoothTextView.setText("");
        updateBluetoothDevicesList();


        Button btnBluetooth = findViewById(R.id.FindBluetooth);
        btnBluetooth.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (bluetooth.checkBluetoothAvailability()) {
                    initialiseBluetooth();
                    bluetoothTextView.setText("Started discovering devices");
                    bluetooth.discoverDevices(thisActivity);
                } else {
                    bluetoothTextView.setText("The device doesn't support bluetooth");
                }
            }
        });

        bluetoothListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Object device = adapterView.getItemAtPosition(position);
                if (bluetooth.getConnecting() == null) {
                    bluetooth.connectToUnit((BluetoothDevice) device, thisActivity);
                } else {
                    bluetooth.getConnecting().cancel();
                    bluetooth.setConnecting(null);
                    bluetooth.connectToUnit((BluetoothDevice) device, thisActivity);
                }
                updateBluetoothDevicesList();
            }

        });

        Button backButton = findViewById(R.id.getBack);
        backButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                bluetooth.clearDiscoveredDeviceList();
                changeContentMainMenu();
            }
        });

    }

    public void changeContentGetData() {
        setContentView(R.layout.activity_main);
        // When main activity is loaded we give som starting values:
        myListView = findViewById(R.id.myListView);
        progressTextView = findViewById(R.id.progressTextView);
        thisContext = this;

        progressTextView.setText("");
        Button btn = (Button) findViewById(R.id.getDataButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GetData retrieveData = new GetData(168, "getContent");
                retrieveData.execute("");
            }
        });

        Button backButton = findViewById(R.id.getBack);
        backButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                changeContentMainMenu();
            }
        });

        Button statisticsButton = findViewById(R.id.statisticsButton);
        statisticsButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                changeContentSeeStat();

            }
        });
    }

    private void changeContentSeeStat() {
        setContentView(R.layout.statistics);

        avgListView = findViewById(R.id.avgListView);
        final Spinner StatDropDown = findViewById(R.id.StatDropDown);
        String[] StatItems = new String[]{"Min", "Hours", "Days", "Weeks"};
        ArrayAdapter<String> StatAdapter = new ArrayAdapter<>(thisContext, android.R.layout.simple_spinner_dropdown_item, StatItems);
        StatDropDown.setAdapter(StatAdapter);
        StatDropDown.setSelection(3);
        final EditText statEdit = findViewById(R.id.statInput);

        if (averageMap.size() > 0 ) {

            ItemStatisticsAdapter itemStatisticsAdapter = new ItemStatisticsAdapter(thisContext, averageMap);
            avgListView.setAdapter(itemStatisticsAdapter);
        }

        mostUsedListView = findViewById(R.id.commonListView);

        if (mostUsedMap.size() > 0 ) {

            ItemStatisticsAdapter itemStatisticsAdapter = new ItemStatisticsAdapter(thisContext, mostUsedMap);
            mostUsedListView.setAdapter(itemStatisticsAdapter);
        }

        Button updateButton = findViewById(R.id.updateStat);
        updateButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                int result = 168;
                switch (StatDropDown.getSelectedItem().toString()) {
                    case "Min": result = (Integer.parseInt(statEdit.getText().toString()))/60; break;
                    case "Hours": result = Integer.parseInt(statEdit.getText().toString()); break;
                    case "Days": result = Integer.parseInt(statEdit.getText().toString())*24; break;
                    case "Weeks": result = Integer.parseInt(statEdit.getText().toString())*7*24; break;
                }
                GetData retrieveData = new GetData(result, "changeStat");
                retrieveData.execute("");
            }
        });

        Button mainMenuButton = findViewById(R.id.mainMenuButton);
        mainMenuButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                changeContentMainMenu();
            }
        });

        Button getDataButton = findViewById(R.id.backToGetData);
        getDataButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                changeContentGetData();
            }
        });

        Button goToGraphsButton = findViewById(R.id.graphButton);
        goToGraphsButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                changeContentGetGraphs();
            }
        });

    }

    public void changeContentGetGraphs() {
        setContentView(R.layout.graphs);

        GraphView graph = (GraphView) findViewById(R.id.graph1);
        graph.addSeries(BERseries);

        Button goToGraph2 = findViewById(R.id.graph2button);
        goToGraph2.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                changeContentGetUTIGraph();
            }
        });
        Button goToGraph3 = findViewById(R.id.graph3button);
        goToGraph3.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                changeContentGetFPSGraph();
            }
        });

    }

    public void changeContentGetUTIGraph() {
        setContentView(R.layout.graphs2);

        GraphView graph = (GraphView) findViewById(R.id.graph1);
        graph.addSeries(UTIseries);

        Button goToGraph1 = findViewById(R.id.graph1button);
        goToGraph1.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                changeContentGetGraphs();
            }
        });
        Button goToGraph3 = findViewById(R.id.graph3button);
        goToGraph3.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                changeContentGetFPSGraph();
            }
        });


    }

    public void changeContentGetFPSGraph() {
        setContentView(R.layout.graphs3);

        GraphView graph = (GraphView) findViewById(R.id.graph1);
        graph.addSeries(FPSseries);

        Button goToGraph2 = findViewById(R.id.graph2button);
        goToGraph2.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                changeContentGetUTIGraph();
            }
        });
        Button goToGraph1 = findViewById(R.id.graph1button);
        goToGraph1.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                changeContentGetGraphs();
            }
        });

    }



    public String whatToSend(String string) {
        String result = "";
        switch (string) {
            case "2 PAM": result = "{MOD:0}"; break;
            case "4 PAM": result = "{MOD:1}"; break;
            case "8 PAM": result = "{MOD:2}"; break;
            case "1 VCL": result = "{VCL:0}"; break;
            case "2 VCL": result = "{VCL:1}"; break;
            case "3 VCL": result = "{VCL:2}"; break;
            case "1 SHIT": result = "{SHIT:0}"; break;
            case "2 SHIT": result = "{SHIT:1}"; break;
            case "3 SHIT": result = "{SHIT:2}"; break;
        }
        return result;
    }

    public void initialiseBluetooth() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
        recieverRegistered = true;
    }

    public void updateBluetoothDevicesList() {
            bluetoothDiscoveredMap = bluetooth.updateMap(bluetoothDiscoveredMap);
            bluetoothAdapter = new BluetoothAdapter(thisContext, bluetoothDiscoveredMap);
            bluetoothListView.setAdapter(bluetoothAdapter);
    }

    protected void onDestroy() {
        super.onDestroy();
        if(recieverRegistered) {
            unregisterReceiver(mReceiver);
        }
            if (bluetooth.getBluetoothAdapter() != null && bluetooth.getConnecting() != null) {
                bluetooth.getConnecting().cancel();
            }
    }

    private class GetData extends AsyncTask<String, String, String> {

        String msg = "";
        // JDBC driver name and database URL
        static final String JDBC_DRIVER = "com.mysql.jdbc.Driver"; // from within the library folder of the jdbc downloaded.
        //static final String DB_URL = "jdbc:mysql://network-project.mysql.database.azure.com:3306/test?useSSL=true&requireSSL=false";
        static final String DB_URL = "jdbc:mysql://" +
                DbStrings.DATABASE_URL + "/" +
                DbStrings.DATABASE_NAME;
        final int hours;
        final String method;

        public GetData(int hours, String method) {
            this.hours = hours;
            this.method = method;
        }
        // just prints to let the user know what's going on.
        @Override
        protected void onPreExecute() {
            progressTextView.setText("Connecting to database...");
        }

        @Override
        protected String doInBackground(String... strings) {

            Connection conn = null;
            Statement stmt = null;
            String[] databasequery = {"PAM","BER", "ERR", "SYN", "UTI", "VCL", "FPS", "RES"};
            String[] avgDatabaseQuery = {"BER", "UTI", "FPS"};
            String[] mostUsedDatabaseQuery = {"VCL", "RES", "PAM"};
            try {
                Class.forName(JDBC_DRIVER);
                conn = DriverManager.getConnection(DB_URL, DbStrings.USERNAME, DbStrings.PASSWORD);

                stmt = conn.createStatement();
                for (String string : databasequery) {
                    String sql = "SELECT " + string + " FROM CurrentValues";
                    ResultSet rs = stmt.executeQuery(sql);
                    while(rs.next()) {
                        String value = rs.getString(1);
                        Log.e("VALUE: ", string + " " + value);
                        personMap.put(string, value);
                    }
                    rs.close();

                    //double age = rs.getDouble("timestmp");
                }


                String sql = "Call AVGValues("+hours+");";
                ResultSet rs = stmt.executeQuery(sql);

                while(rs.next()) {
                    int i = 1;
                    for (String string : avgDatabaseQuery) {
                        Double value = rs.getDouble(i);
                        Log.e("VALUE: ", "" + string);
                        averageMap.put(string, new AveragePair(value, Double.parseDouble(personMap.get(string))));
                        i++;
                    }
                }
                rs.close();

                sql = "Call MostUsedVar("+168+");";
                rs = stmt.executeQuery(sql);

                while(rs.next()) {
                    int i = 1;
                    for (String string : mostUsedDatabaseQuery) {
                        Double value = rs.getDouble(i);
                        Log.e("VALUE: ", "" + string);
                        mostUsedMap.put(string, new AveragePair(value, Double.parseDouble(personMap.get(string))));
                        i++;
                    }
                }
                rs.close();

                sql = "SELECT * FROM BitErrorRate";
                rs = stmt.executeQuery(sql);
                double x = 0;
                rs.last();
                int size = rs.getRow();
                rs.beforeFirst();
                BERseries = new LineGraphSeries<DataPoint>();
                while(rs.next()) {
                    x = x+0.1;
                    Double value = rs.getDouble(1);
                    Log.e("VALUE: ", "" + value);
                    BERseries.appendData(new DataPoint(x, value), true, size);
                }
                rs.close();

                sql = "SELECT * FROM FPS";
                rs = stmt.executeQuery(sql);
                x = 0;
                rs.last();
                size = rs.getRow();
                rs.beforeFirst();
                FPSseries = new LineGraphSeries<DataPoint>();
                while(rs.next()) {
                    x = x+0.1;
                    Double value = rs.getDouble(1);
                    Log.e("VALUE: ", "" + value);
                    FPSseries.appendData(new DataPoint(x, value), true, size);
                }
                rs.close();


                sql = "SELECT * FROM FrameUtilizationValue";
                rs = stmt.executeQuery(sql);
                x = 0;
                rs.last();
                size = rs.getRow();
                rs.beforeFirst();
                UTIseries = new LineGraphSeries<DataPoint>();
                while(rs.next()) {
                    x = x+0.1;
                    Double value = rs.getDouble(1);
                    Log.e("VALUE: ", "" + value);
                    UTIseries.appendData(new DataPoint(x, value), true, size);
                }
                rs.close();




                //double age = rs.getDouble("timestmp");




                msg = "Process complete.";

                // Closing the open resources, no idea why.

                stmt.close();
                conn.close();

            } catch (SQLException connError) {
                msg = "An exception was thrown for JDBC.";
                connError.printStackTrace();;
            } catch (ClassNotFoundException e) {
                msg = "A class not found exception was thrown";
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
            if (method=="getContent") {
                progressTextView.setText(this.msg);

                if (personMap.size() > 0) {

                    itemAdapter = new ItemAdapter(thisContext, personMap);
                    myListView.setAdapter(itemAdapter);
                }
            }
            if (method=="changeStat") {
                if (averageMap.size() > 0) {

                    ItemStatisticsAdapter itemStatisticsAdapter = new ItemStatisticsAdapter(thisContext, averageMap);
                    avgListView.setAdapter(itemStatisticsAdapter);
                }

                mostUsedListView = findViewById(R.id.commonListView);

                if (mostUsedMap.size() > 0) {

                    ItemStatisticsAdapter itemStatisticsAdapter = new ItemStatisticsAdapter(thisContext, mostUsedMap);
                    mostUsedListView.setAdapter(itemStatisticsAdapter);
                }
            }

        }

    }

    private class GetCurrentData extends AsyncTask<String, String, String> {

        String msg = "";
        // JDBC driver name and database URL
        static final String JDBC_DRIVER = "com.mysql.jdbc.Driver"; // from within the library folder of the jdbc downloaded.
        //static final String DB_URL = "jdbc:mysql://network-project.mysql.database.azure.com:3306/test?useSSL=true&requireSSL=false";
        static final String DB_URL = "jdbc:mysql://" +
                DbStrings.DATABASE_URL + "/" +
                DbStrings.DATABASE_NAME;

        // just prints to let the user know what's going on.
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... strings) {

            Connection conn = null;
            Statement stmt = null;
            String[] databasequery = {"PAM", "VCL", "FPS", "RES"};
            try {
                Class.forName(JDBC_DRIVER);
                conn = DriverManager.getConnection(DB_URL, DbStrings.USERNAME, DbStrings.PASSWORD);

                stmt = conn.createStatement();
                for (String string : databasequery) {
                    String sql = "SELECT " + string + " FROM CurrentValues";
                    ResultSet rs = stmt.executeQuery(sql);
                    while(rs.next()) {
                        String value = rs.getString(1);
                        Log.e("VALUE: ", value);
                        this.publishProgress(value, string);
                    }
                    rs.close();
                    //double age = rs.getDouble("timestmp");
                }



                msg = "Process complete.";

                // Closing the open resources, no idea why.
                stmt.close();
                conn.close();

            } catch (SQLException connError) {
                msg = "An exception was thrown for JDBC.";
                connError.printStackTrace();;
            } catch (ClassNotFoundException e) {
                msg = "A class not found exception was thrown";
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
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            switch (values[1]) {
                case "PAM": PAMTextViewData.setText(values[0]); break;
                case "VCL": VCLTextViewData.setText(values[0]); break;
                case "FPS": FPSTextViewData.setText(values[0]); break;
                case "RES": RESTextViewData.setText(values[0]); break;
            }
        }

        @Override
        protected void onPostExecute(String msg) {


            if (personMap.size() > 0 ) {

                itemAdapter = new ItemAdapter(thisContext, personMap);
                myListView.setAdapter(itemAdapter);
            }


        }

    }

} // End of MainActivity
