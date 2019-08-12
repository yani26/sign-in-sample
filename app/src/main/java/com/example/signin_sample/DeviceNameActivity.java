package com.example.signin_sample;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class DeviceNameActivity extends AppCompatActivity {
    private final String TAG = "DeviceNameActivity";
    EditText edNickname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devicename);

        edNickname = findViewById(R.id.edNickname);

        findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edNickname.getText().equals("")) {
                    return;
                }

                String nickname = edNickname.getText().toString();
                Log.i(TAG, "  Nickname = " + nickname);
                SharedPreferences pref = getSharedPreferences(Const.Device_Pref_Key,MODE_PRIVATE);
                SharedPreferences.Editor ed = pref.edit();

                ed.putBoolean(Const.Device_Init_Key, true);
                ed.putString(Const.Device_Nickname_Key, nickname);
                ed.commit();

                DeviceManager.g().setNickname(nickname);
                setResult(RESULT_OK);
                finish();
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
