package com.example.sslab.p2p_client;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.Socket;

public class Login extends AppCompatActivity {
    private Socket socket;
    private String IP;
    private int Port;

    EditText IPaddress;
    EditText PortNum;

    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        IPaddress = (EditText)findViewById(R.id.IPaddress);
        PortNum = (EditText)findViewById(R.id.PortNum);
        Button ConnectButton = (Button) findViewById(R.id.ConnectButton);
        ActionBar Appbar;
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        ConnectButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if ( IPaddress.getText().toString().length() == 0 || PortNum.getText().toString().length() == 0){
                    final Toast toast = Toast.makeText(Login.this,"you must input\n"+"IP address and PortNumber",Toast.LENGTH_LONG);
                    toast.show();
                }else {
                    IP = IPaddress.getText().toString();
                    Port = Integer.parseInt(PortNum.getText().toString());
                    final Toast toast = Toast.makeText(Login.this, "Try to Connect with\n" + "IP Address:" + IP + "\n" + "Port Number :" + Port, Toast.LENGTH_LONG);
                    toast.show();
                    Intent intent = new Intent(
                            getApplicationContext(),
                            MainActivity.class);
                    intent.putExtra("IP",String.valueOf(IPaddress.getText()));
                    intent.putExtra("Port",String.valueOf(PortNum.getText()));
                    startActivity(intent);
                }
            }
        });
    }
}
