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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
    Integer hit = new Integer(0);
    static double latitudeReceived,longitudereceived;
    static String imgStore ="";

    /**
     * url of the database where loginIDs are stored
     */
    String urlLogin = "http://retailwatch.in/api/api_retail_login.php";

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        loginId = (EditText) findViewById(R.id.edit_login_id);
        passwd = (EditText) findViewById(R.id.edit_passwd);

        passwd.setTransformationMethod(new AsteriskPasswordTransformationMethod());

        submitAuth = (Button) findViewById(R.id.buttonSubmitAuth);

        /*session = new SessionManager(getApplicationContext());

        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(MainActivity.this, LocationActivity.class);
            startActivity(intent);
            finish();
        }*/

        submitAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                idEntered = loginId.getText().toString();
                passwdEntered = passwd.getText().toString();

                if (!idEntered.isEmpty() && !passwdEntered.isEmpty()) {

                    Authentication AuthObj = new Authentication();
                    AuthObj.execute();
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Please enter the credentials!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
    }

    private class Authentication extends AsyncTask<Void, Void, Void> {

        String response ="";

        @Override
        protected Void doInBackground(Void... params) {

            try {
                URL url = new URL(urlLogin);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(10000);
                httpURLConnection.setConnectTimeout(15000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);

                List<NameValuePair> parameters = new ArrayList<NameValuePair>();
                parameters.add(new BasicNameValuePair("edit_login_id",idEntered));
                parameters.add(new BasicNameValuePair("edit_passwd", passwdEntered));

                OutputStream os = httpURLConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getQuery(parameters));
                writer.flush();
                writer.close();
                os.close();

                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                StringBuilder output = new StringBuilder();
                if (inputStream != null) {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
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

                hit = jsonObject.getInt("status");
                latitudeReceived = jsonObject.getDouble("latitude");
                longitudereceived = jsonObject.getDouble("longitude");

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

            if(hit==1){

                imgStore = idEntered;
                Intent locAct = new Intent(MainActivity.this,LocationActivity.class);
                startActivity(locAct);
                loginId.setText("");
                passwd.setText("");
            }
            else{
                Context context = getApplicationContext();
                CharSequence text = "Wrong Credentials";
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        }

        private String getQuery(List<NameValuePair> parameters) throws UnsupportedEncodingException
        {
            StringBuilder result = new StringBuilder();
            boolean first = true;

            for (NameValuePair pair : parameters)
            {
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
            }

            return result.toString();
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);
        //finish();
    }
}
