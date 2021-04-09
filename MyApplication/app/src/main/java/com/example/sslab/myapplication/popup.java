package com.example.sslab.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class popup extends Activity {
    TextView Levelview;
    ImageView imageView;
    TextView Countermeasuresview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);
        Levelview = (TextView)findViewById(R.id.Level);
        Countermeasuresview = (TextView)findViewById(R.id.Countermeasures);
        Intent intent = getIntent();
        int Level = intent.getIntExtra("Level",1);
        //int Level = Integer.parseInt(intent.getStringExtra("Level"));
        //String Level = intent.getStringExtra("Level");
        Log.i(popup.class.getSimpleName(),"EQLevel = "+Level+"\n");
        if(Level == 1){
            Levelview.setText("낮음\n낮음");
            Levelview.setTextColor(Color.parseColor("#3cd405"));
        }else if(Level == 2){
            Levelview.setText("보통\n보통");
            Levelview.setTextColor(Color.parseColor("#d40f05"));
        }else if(Level == 3){
            Levelview.setText("높음\n높음");
            Levelview.setTextColor(Color.parseColor("#d40f05"));
        }

    }

    public void mOnClose(View v){
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()== MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }
}
