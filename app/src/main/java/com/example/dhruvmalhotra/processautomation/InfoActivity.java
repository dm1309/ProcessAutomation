package com.example.dhruvmalhotra.processautomation;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Dhruv.Malhotra on 10/11/2016.
 */

public class InfoActivity extends Activity {

    TextView txtInstruction;
    Button btnInstruction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        txtInstruction = (TextView) findViewById(R.id.instruction_pictures);
        btnInstruction = (Button) findViewById(R.id.taking_pics);
    }
}
