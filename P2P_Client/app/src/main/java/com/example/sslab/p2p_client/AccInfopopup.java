package com.example.sslab.p2p_client;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class AccInfopopup extends Activity {
    TextView infoView;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.accinfopopup);
        infoView = (TextView)findViewById(R.id.info);

        Intent intent = getIntent();
        String IP = intent.getStringExtra("IP");
        int Port = Integer.parseInt(intent.getStringExtra("Port"));
        infoView.setText("IP address : ");
        infoView.append(IP+"\n");
        infoView.append("Port Number : "+Port);

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
