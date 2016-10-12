package com.example.dhruvmalhotra.processautomation;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {
    /**
     * Variables used to display
     */
    EditText loginId, passwd;
    Button submitAuth;
    String idEntered, passwdEntered;
    HashMap mpId = new HashMap();
    HashMap mpPasswd = new HashMap();

    /**
     * url of the database where loginIDs are stored
     */
    String urlLoginID = "https://processautomation-dd09a.firebaseio.com/loginID.json";
    String urlPasswd = "https://processautomation-dd09a.firebaseio.com/passwd.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        loginId = (EditText) findViewById(R.id.edit_login_id);
        passwd = (EditText) findViewById(R.id.edit_passwd);

        submitAuth = (Button) findViewById(R.id.buttonSubmitAuth);

        submitAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                idEntered = loginId.getText().toString();
                passwdEntered = passwd.getText().toString();
                Authentication AuthObj = new Authentication();
                AuthObj.execute();
            }
        });
    }

    private class Authentication extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            try {
                URL urlp = new URL(urlPasswd);

                HttpURLConnection httpURLConnectionPasswd = (HttpURLConnection) urlp.openConnection();
                httpURLConnectionPasswd.setRequestMethod("GET");
                httpURLConnectionPasswd.connect();
                InputStream inputStreamPasswd = httpURLConnectionPasswd.getInputStream();
                StringBuilder outputPasswd = new StringBuilder();
                if (inputStreamPasswd != null) {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStreamPasswd, Charset.forName("UTF-8"));
                    BufferedReader reader = new BufferedReader(inputStreamReader);
                    String line = reader.readLine();
                    while (line != null) {
                        outputPasswd.append(line);
                        line = reader.readLine();
                    }
                }
                String jsonResponsePasswd = null;
                jsonResponsePasswd = outputPasswd.toString();

                JSONObject jsonObjectPasswd = new JSONObject(jsonResponsePasswd);

                Iterator<String> itr = jsonObjectPasswd.keys();
                while (itr.hasNext()) {
                    String key = itr.next();

                    String value = jsonObjectPasswd.getString(key);

                    mpPasswd.put(key,value);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                URL url = new URL(urlLoginID);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();
                InputStream inputStreamPasswd = httpURLConnection.getInputStream();
                StringBuilder output = new StringBuilder();
                if (inputStreamPasswd != null) {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStreamPasswd, Charset.forName("UTF-8"));
                    BufferedReader reader = new BufferedReader(inputStreamReader);
                    String line = reader.readLine();
                    while (line != null) {
                        output.append(line);
                        line = reader.readLine();
                    }
                }
                String jsonResponse = null;
                jsonResponse = output.toString();

                JSONObject jsonObject = new JSONObject(jsonResponse);

                Iterator<String> itr = jsonObject.keys();
                while (itr.hasNext()) {
                    String key = itr.next();

                    String value = jsonObject.getString(key);

                    mpId.put(key,value);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(mpId.containsValue(idEntered)&&mpPasswd.containsValue(passwdEntered)){
                Intent locAct = new Intent(MainActivity.this,LocationActivity.class);
                startActivity(locAct);
            }
            else{
                Context context = getApplicationContext();
                CharSequence text = "Wrong Credentials";
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        }
    }
}
