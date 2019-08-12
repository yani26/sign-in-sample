package com.example.signin_sample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class DevicePulseActivity extends AppCompatActivity {
    private static final int REQ_ActInputName = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_pulse);

        if (!DeviceManager.g().isDeviceRegistered()) {
            Intent intent = new Intent();
            intent.setClass(DevicePulseActivity.this, DeviceNameActivity.class);
            startActivityForResult(intent, REQ_ActInputName, new Bundle());
        }
    }

}
