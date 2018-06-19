package com.example.mysqlexampleproject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jjoe64.graphview.series.DataPoint;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class passwordPopUp extends Activity {
    TextView passwordTextView;
    EditText passwordInput;
    String password = "";

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_pop_up);

        DisplayMetrics me = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(me);

        int width = me.widthPixels;
        int height = me.heightPixels;

        getWindow().setLayout((int)(width*0.6),(int) (height*0.3));

        passwordInput = findViewById(R.id.passwordInput);
        passwordTextView = findViewById(R.id.passwordTextView);

        Button loginButton = findViewById(R.id.checkPasswordbutton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TestPassword retrieveData = new TestPassword();
                retrieveData.execute("");
            }
        });


    }

    private class TestPassword extends AsyncTask<String, String, String> {

        // JDBC driver name and database URL
        static final String JDBC_DRIVER = "com.mysql.jdbc.Driver"; // from within the library folder of the jdbc downloaded.
        //static final String DB_URL = "jdbc:mysql://network-project.mysql.database.azure.com:3306/test?useSSL=true&requireSSL=false";
        static final String DB_URL = "jdbc:mysql://" +
                DbStrings.DATABASE_URL + "/" +
                DbStrings.DATABASE_NAME;

        // just prints to let the user know what's going on.
        @Override
        protected void onPreExecute() {
            passwordTextView.setText("Testing password...");
        }

        @Override
        protected String doInBackground(String... strings) {
            password = passwordInput.getText().toString();
            Connection conn = null;

            try {
                Class.forName(JDBC_DRIVER);
                conn = DriverManager.getConnection(DB_URL, DbStrings.USERNAME, password);
                passwordTextView.setText("Password accepted");
                Intent returnIntent = new Intent();
                returnIntent.putExtra("Password", password);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
                conn.close();
            } catch (SQLException connError) {
                passwordTextView.setText("Connection error. Password might be wrong");
                connError.printStackTrace();;
            } catch (ClassNotFoundException e) {

                passwordTextView.setText("A class not found exception was thrown");
                e.printStackTrace();

            }
            return null;
        }



    }
}
