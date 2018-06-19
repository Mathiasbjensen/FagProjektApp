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

public class MainActivity extends Activity {


    static Context thisContext;
    static Activity thisActivity;

    private String password;

    private Bluetooth bluetooth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisContext = this;
        thisActivity = this;
        bluetooth = new Bluetooth();
        changeContentMainMenu();

    }

    //initialises UI and button listeners
    public void changeContentMainMenu() {
        setContentView(R.layout.main_menu);
        Button goToGet = findViewById(R.id.goToGetData);
        Button goToBlue = findViewById(R.id.goToBluetooth);
        Button goToChange = findViewById(R.id.goToChangeData);


        goToGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(thisActivity, getDataActivity.class);
                intent.putExtra("Password", password);
                startActivityForResult(intent, 3);
            }
        });

        goToBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(thisActivity, FindBluetoothActivity.class);
                intent.putExtra("Bluetooth", bluetooth);
                startActivityForResult(intent, 1);
            }
        });

        goToChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetooth.getConnecting() != null && bluetooth.getConnecting().getConnectedDevice() != null) {
                    Intent intent = new Intent(thisActivity, ChangeDataActivity.class);
                    intent.putExtra("Password", password);
                    startActivityForResult(intent, 2);
                }
            }
        });

        Intent intent = new Intent(thisActivity, passwordPopUp.class);
        startActivityForResult(intent, 4);


    }






    protected void onDestroy() {
        super.onDestroy();
        if (bluetooth.getBluetoothAdapter() != null && bluetooth.getConnecting().getConnectedDevice() != null) {
            bluetooth.getConnecting().cancel();
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 4) {
                Log.e("Password initialised", "Password is initialised as: "+ data.getStringExtra("Password"));
                //saves the password put in in passwordPopUp if it's accepted
                if(resultCode == Activity.RESULT_OK){
                    password = (String) data.getStringExtra("Password");
                }
                if (resultCode == Activity.RESULT_CANCELED) {
                    //Write your code if there's no result
                }
        }
    }//onActivityResult



} // End of MainActivity
