package com.example.signin_sample;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i("MainActivity", "Starting DeviceManager");

        // THIS IS WHERE THE DEVICE MANAGER STARTS
        new Thread(new DeviceManager(getString(R.string.server))).start();
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, DevicePulseActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onStart() {
        super.onStart();
    }

}
