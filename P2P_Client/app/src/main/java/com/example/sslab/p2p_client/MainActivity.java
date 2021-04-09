package com.example.sslab.p2p_client;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;


public class MainActivity extends AppCompatActivity  implements SensorEventListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    private Socket socket;
    private String IP;
    private int Port;
    private BufferedInputStream Reader;
    private BufferedReader networkReader;
    private Packet message = new Packet();
    private byte[] Buffer = new byte[16];
    private int temparr;
    private int bitWise = 1;
    private byte tempbyte;
    private int SaveFlag = 1;
    private int data=0;
    public String filepath;
    public String txtname = "AccData.txt";
    //Graph
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private LineChart mChart;
    private Thread thread;
    private boolean plotData = true;
    private String name[] = {"X","Y","Z"};
    private int color[] = {Color.RED,Color.GREEN,Color.YELLOW};

    EditText IPaddress;
    EditText PortNum;
    TextView Viewer;
    TextView StatusViewer;
    Button SaveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this.getIntent());
            IP = intent.getStringExtra("IP");
            Port = Integer.parseInt(intent.getStringExtra("Port"));
        Viewer = (TextView) findViewById(R.id.textViewer);
        StatusViewer = (TextView) findViewById(R.id.StatusViewer);
        SaveButton = (Button) findViewById(R.id.SaveButton);
        ActionBar Appbar;
        //make Sensordata folder
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File folder = new File(Environment.getExternalStorageDirectory(), "SensorData");
            Log.i(TAG,"make folder : "+Environment.getExternalStorageDirectory()+"\n");
            if (!folder.exists()) {
                Log.i(TAG,"Success Create folder\n");
                if(!folder.mkdirs())
                    Log.i(TAG,"Create folder fail\n");
            }
        }
        WriteTextFile(txtname);
        filepath = Environment.getExternalStorageDirectory()+"/SensorData/"+txtname;
        SaveButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){

                SaveFlag = 1;
                final Toast toast = Toast.makeText(MainActivity.this, "Save Data", Toast.LENGTH_LONG);
                toast.show();
            }
        });
        StatusViewer.setText("Status: ");

        //Connect server
        Viewer.append("Try to Connect with\n" + "IP Address:" + IP + "\n" + "Port Number :"+ Port +"\n");
        try{
            SocketThread thread = new SocketThread();
            thread.setDaemon(true);
            thread.start();
        }catch (Exception E){
            E.printStackTrace();
            Viewer.append("Connect Fail\n");
        }
        Viewer.append("Connect Success\n");

        //Graph
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        if(mAccelerometer != null) {
            mSensorManager.registerListener(this , mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
        mChart = (LineChart) findViewById(R.id.chart1);

        mChart.getDescription().setEnabled(true);
        mChart.getDescription().setText("Real Time Accelerometer Data Plot");

        mChart.setTouchEnabled(false);
        mChart.setDragEnabled(false);
        mChart.setScaleEnabled(false);
        mChart.setDrawGridBackground(false);
        mChart.setPinchZoom(false);
        mChart.setBackgroundColor(Color.BLACK);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);
        mChart.setData(data);

        Legend l = mChart.getLegend();

        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);

        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(true);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMaximum(3f);
        leftAxis.setAxisMinimum(-3f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.setDrawBorders(false);
    }

    private  void addEntry(SensorEvent event){
        LineData data = mChart.getData();
        if(data != null){
            ILineDataSet set = data.getDataSetByIndex(0);
            ILineDataSet set2 = data.getDataSetByIndex(1);
            ILineDataSet set3 = data.getDataSetByIndex(2);
            if(set==null){
                set = createSet(0);
                set2 = createSet(1);
                set3 = createSet(2);
                data.addDataSet(set);
                data.addDataSet(set2);
                data.addDataSet(set3);
            }
            Log.i(TAG,"\n"+"X:"+event.values[0]+"\n"+"Y:"+event.values[1]+"\n"+"Z:"+event.values[2]+"\n");
            data.addEntry(new Entry(set.getEntryCount(),  event.values[0]), 0);
            data.addEntry(new Entry(set2.getEntryCount(), event.values[1]), 1);
            data.addEntry(new Entry(set3.getEntryCount(), event.values[2]), 2);

            if(SaveFlag == 1) {
                try {
                    FileWriter fw = new FileWriter(filepath, true);
                    fw.write(event.values[0] + ", " + event.values[1] + ", "+ event.values[2] +"\n");
                    fw.close();
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }
            data.notifyDataChanged();
            mChart.notifyDataSetChanged();
            mChart.setVisibleXRangeMaximum(50);
            mChart.moveViewToX(data.getEntryCount());
        }
    }

    private LineDataSet createSet(int index){
        LineDataSet set = new LineDataSet(null,  name[index]);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3f);
        set.setColor(color[index]);
        set.setHighlightEnabled(false);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
            addEntry(event);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    //connect server
    public void setSocket(String ip, int port) throws IOException{
        try{
            socket = new Socket(ip,port);
            //networkReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Reader = new BufferedInputStream(socket.getInputStream());
        }catch (IOException E){
            E.printStackTrace();
        }
    }

    class SocketThread extends Thread{
        public void run(){
            try{
                setSocket(IP,Port);
            }catch(IOException E){
                E.printStackTrace();
            }
            try{
                while(true){
                    Reader.read(Buffer);
                    System.arraycopy(Buffer,0,message.status,0,1);
                    System.arraycopy(Buffer,1,message.Level,0,1);
                    System.arraycopy(Buffer,2,message.Padding,0,2);
                    System.arraycopy(Buffer,4,message.ANN,0,4);
                    System.arraycopy(Buffer,8,message.PGA,0,4);
                    System.arraycopy(Buffer,12,message.STA_LTA,0,4);
                    tempbyte = message.status[0];
                    temparr = (int)(0xFF&tempbyte);
                    Viewer.post(new Runnable() {
                        @Override
                        public void run() {
                            for(int i = 7; i >= 0; i--){
                                if((bitWise & temparr)>0){
                                    if(i==4)
                                        StatusViewer.append("Earthquake Detected");
                                    else if(i==3)
                                        StatusViewer.append("Not earthquake");
                                    else if(i==2)
                                        StatusViewer.append("Turn to Processing");
                                    else if(i==1)
                                        StatusViewer.append("Turn to Trigger");
                                    else if(i==0)
                                        StatusViewer.append("Turn to Steady");
                                    else
                                        StatusViewer.append("");
                                }
                                bitWise = (bitWise<<1)&0xFE;
                                //StatusViewer.append("\n");
                            }
                            Viewer.append("Level: "+new String(message.Level)+"\n"
                                    +"Padding: "+new String(message.Padding)+"\n");

                            Viewer.append("ANN:"+Float.toString(ByteToFloat(message.ANN))+"\n"
                                    +"PGA:"+Float.toString(ByteToFloat(message.PGA))+"\n"
                                    +"STA/LTA:"+Float.toString(ByteToFloat(message.STA_LTA))+"\n");
                            Viewer.append("---------------------------\n");
                            bitWise = 1;
                        }
                    });
                }
            }catch(Exception E){
                E.printStackTrace();
            }
        }
    }
    public int ByteToInt(byte bytes[]){
        return ((((int)bytes[0] & 0xff) << 24) |
                (((int)bytes[1] & 0xff) << 16) |
                (((int)bytes[2] & 0xff) << 8) |
                (((int)bytes[3] & 0xff)));
    }

    public float ByteToFloat(byte bytes[]){
        int value = ByteToInt(bytes);
        return Float.intBitsToFloat(value);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar, menu) ;
        return true ;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_Discconect :
                try{
                    socket.close();
                    socket = null;
                    mSensorManager.unregisterListener(MainActivity.this);
                    thread.interrupt();
                } catch(IOException E){
                    E.printStackTrace();
                }
                return true;
            case R.id.action_AccessInfo :
                Intent intent = new Intent(
                        getApplicationContext(),
                        AccInfopopup.class);
                intent.putExtra("IP",String.valueOf(IP));
                intent.putExtra("Port",String.valueOf(Port));
                startActivity(intent);
                return true;
            default:
                return false;
        }
    }
    public void WriteTextFile(String filename) {
        try {
            File datafile = new File(Environment.getExternalStorageDirectory()+"/SensorData",filename);
            FileWriter fw = new FileWriter(datafile,false);
            fw.write("x,y,z,timestamp\n");
            fw.close();
        } catch(IOException e) {
            Log.i(TAG,"oops\n");
            e.printStackTrace();
        }
    }
}
