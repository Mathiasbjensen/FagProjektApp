package com.example.mysqlexampleproject;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ItemAdapter itemAdapter;
    BluetoothAdapter bluetoothAdapter;
    static Context thisContext;
    ListView myListView;
    ListView bluetoothListView;
    TextView progressTextView;
    // LinkedHashMap is always stored in the same order, could use w/e i guess?
    Map<String, Double> personMap =
            new LinkedHashMap<>();
    private Bluetooth bluetooth;
    private BroadcastReceiver mReceiver;
    Map<String, DevicePair> bluetoothDiscoveredMap = new LinkedHashMap<>();
    Map<String, DevicePair> bluetoothPairedMap = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisContext = this;

        changeContentMainMenu();


        /*
        Button sendDataBluetooth = (Button) findViewById(R.id.button2);
        //final EditText mEdit   = (EditText)findViewById(R.id.VariableText);
        sendDataBluetooth.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (bluetooth.connecting != null) {
                    bluetooth.connecting.write("a");
                }
            }
        });*/

        

    }

    private void changeContentMainMenu() {
        setContentView(R.layout.main_menu);
        Button goToGet = (Button) findViewById(R.id.goToGetData);
        Button goToBlue = (Button) findViewById(R.id.goToBluetooth);
        Button goToChange = (Button) findViewById(R.id.goToChangeData);


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
    }

    private void changeContentGetBluetooth() {
        setContentView(R.layout.activity_connect_to_bluetooth);
        bluetoothListView = findViewById(R.id.DeviceListView);


        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.bluetooth = new Bluetooth();
        this.mReceiver = bluetooth.mReceiver;
        registerReceiver(mReceiver, filter);
        bluetooth.discoverDevices(this);

        Button btnBluetooth = findViewById(R.id.FindBluetooth);
        btnBluetooth.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                GetBluetoothDevices getBluetooth = new GetBluetoothDevices();
                getBluetooth.execute("");
            }
        });

        bluetoothListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Object device = adapterView.getItemAtPosition(position);
                bluetooth.connectToUnit((BluetoothDevice) device, (Activity) thisContext);
            }

        });

        Button backButton = findViewById(R.id.getBack);
        backButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                changeContentMainMenu();
            }
        });
    }

    private void changeContentGetData() {
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
                GetData retrieveData = new GetData();
                retrieveData.execute("");
            }
        });

        Button backButton = findViewById(R.id.getBack);
        backButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                changeContentMainMenu();
            }
        });
    }

    protected void onDestroy() {
        super.onDestroy();

        if (bluetooth != null) {
            bluetooth.unregisterReceiver(mReceiver);
            if (bluetooth.connecting != null) {
                bluetooth.connecting.cancel();
            }
        }
    }

    private class GetBluetoothDevices extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            for (BluetoothDevice device : bluetooth.pairedDevicesList) {
                bluetoothPairedMap.put(device.getName(), new DevicePair(device,"Paired"));
            }
            for (BluetoothDevice device : bluetooth.discoveredDevices) {
                bluetoothDiscoveredMap.put(device.getName(), new DevicePair(device,""));
            }
            return null;
        }


        protected void onPreExecute() {
        }
        protected void onPostExecute(String msg) {
            Log.e("Testing", Integer.toString(bluetoothDiscoveredMap.size()));
            if (bluetoothDiscoveredMap.size() > 0 ) {

                bluetoothAdapter = new BluetoothAdapter(thisContext, bluetoothDiscoveredMap);
                bluetoothListView.setAdapter(bluetoothAdapter);
            }

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

        // just prints to let the user know what's going on.
        @Override
        protected void onPreExecute() {
            progressTextView.setText("Connecting to database...");
        }

        @Override
        protected String doInBackground(String... strings) {

            Connection conn = null;
            Statement stmt = null;

            try {
                Class.forName(JDBC_DRIVER);
                conn = DriverManager.getConnection(DB_URL, DbStrings.USERNAME, DbStrings.PASSWORD);

                stmt = conn.createStatement();
                String sql = "SELECT * FROM person";
                ResultSet rs = stmt.executeQuery(sql);

                while (rs.next()) {
                    String name = rs.getString("navn");
                    double age = rs.getDouble("alder");

                    personMap.put(name, age);
                }

                msg = "Process complete.";

                // Closing the open resources, no idea why.
                rs.close();
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

            progressTextView.setText(this.msg);

            if (personMap.size() > 0 ) {

                itemAdapter = new ItemAdapter(thisContext, personMap);
                myListView.setAdapter(itemAdapter);
            }


        }

    }
} // End of MainActivity
