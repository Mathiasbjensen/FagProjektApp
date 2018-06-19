package com.example.mysqlexampleproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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

public class getDataActivity extends Activity {

    ListView myListView;
    TextView progressTextView;
    Context thisContext;
    ArrayList<DataPoint> BERseries;
    ArrayList<DataPoint> UTIseries;
    ArrayList<DataPoint> FPSseries;
    //LineGraphSeries<DataPoint> BERseries;
    //LineGraphSeries<DataPoint> UTIseries;
    //LineGraphSeries<DataPoint> FPSseries;
    Map<String, String> personMap = new LinkedHashMap<>();
    ItemAdapter itemAdapter;
    HashMap<String, AveragePair> averageMap = new HashMap<>();
    HashMap<String, AveragePair> mostUsedMap = new HashMap<>();
    String password = "";
    Activity thisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setPassword(getIntent().getStringExtra("Password"));
        changeContentGetData();
        thisActivity = this;
    }

    public void setPassword(String string) {
        this.password = string;
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

               initiateGetData();
            }
        });

        Button backButton = findViewById(R.id.getBack);
        backButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });

        Button statisticsButton = findViewByid(R.id.statisticsButton);
        statisticsButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(thisActivity, ChangeStatistics.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("BERSeries", BERseries);
                bundle.putSerializable("UTISeries", UTIseries);
                bundle.putSerializable("FPSSeries", FPSseries);
                bundle.putSerializable("AvgMap", averageMap);
                bundle.putSerializable("CommonMap", mostUsedMap);
                intent.putExtra("Password", password);
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);


            }
        });
    }
    public void initiateGetData() {
        GetData retrieveData = new GetData(168, "getContent");
        retrieveData.execute("");
    }

    public Adapter getListViewAdapter() {
        return myListView.getAdapter();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_CANCELED) {
                finish();
            }
        }
    }//onActivityResult

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

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            progressTextView.setText(values[0]);
        }

        @Override
        protected String doInBackground(String... strings) {
            publishProgress("Connecting to database...");
            Connection conn = null;
            Statement stmt = null;
            String[] databasequery = {"PAM","BER", "ERR", "SYN", "UTI", "VCL", "FPS", "RES"};
            String[] avgDatabaseQuery = {"BER", "UTI", "FPS"};
            String[] mostUsedDatabaseQuery = {"VCL", "RES", "PAM"};
            try {
                Class.forName(JDBC_DRIVER);
                conn = DriverManager.getConnection(DB_URL, DbStrings.USERNAME, password);
                publishProgress("Fetching data...");

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

                    //double age = rs.getDouble("timestmp");


                sql = "Call AVGValues("+hours+");";
                rs = stmt.executeQuery(sql);

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

                if (!rs.isBeforeFirst()) {
                    for (String string : mostUsedDatabaseQuery) {
                        Log.e("Most used var: ", "" + null);
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
                        i++;
                    }
                }
                rs.close();

                sql = "SELECT * FROM BitErrorRate ORDER BY timestmp LIMIT 100";
                rs = stmt.executeQuery(sql);
                double x = 0;
                rs.last();
                int size = rs.getRow();
                rs.beforeFirst();
                BERseries = new ArrayList<>();
                while(rs.next()) {
                    x = x+1;
                    Double value = rs.getDouble(1);
                    BERseries.add(new DataPoint(x, value));
                }
                rs.close();

                sql = "SELECT * FROM FPS ORDER BY timestmp LIMIT 100";
                rs = stmt.executeQuery(sql);
                x = 0;
                rs.last();
                size = rs.getRow();
                rs.beforeFirst();
                FPSseries = new ArrayList<>();
                while(rs.next()) {
                    x = x+1;
                    Double value = rs.getDouble(1);
                    FPSseries.add(new DataPoint(x, value));
                }
                rs.close();


                sql = "SELECT * FROM Utilization ORDER BY timestmp LIMIT 100";
                rs = stmt.executeQuery(sql);
                x = 0;
                rs.last();
                size = rs.getRow();
                rs.beforeFirst();
                UTIseries = new ArrayList<DataPoint>();
                while(rs.next()) {
                    x = x+1;
                    Double value = rs.getDouble(1);
                    UTIseries.add(new DataPoint(x, value));
                }
                rs.close();

                msg = "Process complete.";

                // Closing the open resources, no idea why.

                stmt.close();
                conn.close();

            } catch (SQLException connError) {
                msg = "Could not connect to database. Password might not be initialised";
                connError.printStackTrace();
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

                if (personMap.size() > 0) {

                    itemAdapter = new ItemAdapter(thisContext, personMap);
                    myListView.setAdapter(itemAdapter);
                    Log.e("test", myListView.getAdapter()+"");
                }
        }

    }

}
