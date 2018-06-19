package com.example.mysqlexampleproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ChangeDataActivity extends Activity {

    TextView PAMTextView;
    TextView PAMTextViewData;
    TextView VCLTextView;
    TextView VCLTextViewData;
    TextView FPSTextView;
    TextView FPSTextViewData;
    TextView RESTextView;
    TextView RESTextViewData;
    String password;
    Bluetooth bluetooth = new Bluetooth();

    //Initiates the activity and gets the password sent from MainActivity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        password = getIntent().getStringExtra("Password");
        changeContentChangeData();

    }

    //Initiates the UI, buttons, drop downs, TextViews and other things in the ui
    public void changeContentChangeData() {
        setContentView(R.layout.change_data2);
        final Spinner pamDropDown = findViewById(R.id.pamDropDown);
        final Spinner RESDropDown = findViewById(R.id.RESDropDown);
        PAMTextView = findViewById(R.id.PAMTextView);
        PAMTextViewData = findViewById(R.id.PAMTextViewData);
        VCLTextView = findViewById(R.id.VCLTextView);
        VCLTextViewData = findViewById(R.id.VCLTextViewData);
        FPSTextView = findViewById(R.id.FPSTextView);
        FPSTextViewData = findViewById(R.id.FPSTextViewData);
        RESTextView = findViewById(R.id.RESTextView);
        RESTextViewData = findViewById(R.id.RESTextViewData);

        final EditText FPSEdit = findViewById(R.id.FPSInput);
        final EditText VCLEdit = findViewById(R.id.VCLInput);

        GetCurrentData retrieveData = new GetCurrentData();
        retrieveData.execute("");

        String[] pamItems = new String[]{"2 PAM", "4 PAM", "8 PAM"};
        ArrayAdapter<String> pamAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, pamItems);
        pamDropDown.setAdapter(pamAdapter);

        String[] RESItems = new String[]{"144", "480", "720", "1080"};
        ArrayAdapter<String> RESAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, RESItems);
        RESDropDown.setAdapter(RESAdapter);

        Button sendDataPam = findViewById(R.id.sendDataPam);
        sendDataPam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Sends changes to the arduino when pressing button
                String[] split = pamDropDown.getSelectedItem().toString().split(" ");
                bluetooth.getConnecting().write(password+"-{MOD:"+split[0]+"}");

            }
        });

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Not valid input");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        final AlertDialog alert11 = builder1.create();


        Button sendDataVCL = findViewById(R.id.sendDataVCL);
        sendDataVCL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Sends changes to the arduino when pressing button and checking whether its a valid input
                String temp = VCLEdit.getText().toString();
                try {
                    Double tempInt = Double.parseDouble(temp);
                    if (tempInt < 1.0 || tempInt > 100.0 || tempInt%1 != 0) {
                        throw new NumberFormatException();
                    }
                    bluetooth.getConnecting().write(password+"-{VCL:"+temp+"}");

                } catch (NumberFormatException e) {
                    alert11.show();
                }


            }
        });

        Button sendDataRES = findViewById(R.id.sendDataRES);
        sendDataRES.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Sends changes to the arduino when pressing button
                bluetooth.getConnecting().write(password+"-{RES:"+RESDropDown.getSelectedItem().toString()+"}");
            }
        });

        Button sendDataFPS = findViewById(R.id.sendDataFPS);
        sendDataFPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Sends changes to the arduino when pressing button and checking whether its a valid input
                String temp = FPSEdit.getText().toString();
                try {
                    Double tempInt = Double.parseDouble(temp);
                    if (tempInt < 1.0 || tempInt > 60.0 || tempInt%1 != 0) {
                        throw new NumberFormatException();
                    }
                    bluetooth.getConnecting().write(password+"-{FPS:"+temp+"}");

                } catch (NumberFormatException e) {
                    alert11.show();
                }
            }
        });


        Button backButton = findViewById(R.id.getBack);
        backButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                //Returns to mainactivity and ends this activity when pressing backbutton.
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });



    }

    //Gets data from the database and displays it. Almost the same class is used and explained in greater details in getDataActivity
    private class GetCurrentData extends AsyncTask<String, String, String> {

        String msg = "";

        static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        static final String DB_URL = "jdbc:mysql://" +
                DbStrings.DATABASE_URL + "/" +
                DbStrings.DATABASE_NAME;


        @Override
        protected String doInBackground(String... strings) {

            Connection conn = null;
            Statement stmt = null;
            String[] databasequery = {"PAM", "VCL", "FPS", "RES"};
            try {
                Class.forName(JDBC_DRIVER);
                conn = DriverManager.getConnection(DB_URL, DbStrings.USERNAME, password);

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
                }



                msg = "Process complete.";

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

        }

    }

}
