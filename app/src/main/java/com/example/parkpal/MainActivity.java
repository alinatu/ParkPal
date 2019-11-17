package com.example.parkpal;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.parkpal.ui.main.MainFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_fragment);

        final Button park_btn = findViewById(R.id.park_btn);
        park_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onMapButtonClick();
            }
        });

        final Button park_search_btn = findViewById(R.id.park_search_btn);
        park_search_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onMapSearchButtonClick();
            }
        });

    }

    public void onMapButtonClick() {
        Intent i = new Intent(this, AllParksMapsActivity.class);
        startActivity(i);
    }

    public void onMapSearchButtonClick() {
        Intent i = new Intent(this, Park_Search_Activity.class);
        startActivity(i);
    }


}
