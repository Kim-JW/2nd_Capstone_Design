package com.example.sslab.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    int Level = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent_ = new Intent(getApplicationContext(), popup.class);
        intent_.putExtra("Level",Level);
        startActivity(intent_);
    }
}
