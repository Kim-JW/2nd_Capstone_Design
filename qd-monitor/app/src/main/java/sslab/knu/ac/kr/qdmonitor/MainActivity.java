package sslab.knu.ac.kr.qdmonitor;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.SimpleDateFormat;
import java.util.Date;

import sslab.knu.ac.kr.qdmonitor.packet.QDPacket;
import sslab.knu.ac.kr.qdmonitor.packet.QDReportPacket;

public class MainActivity extends AppCompatActivity {

    public final static String TAG = MainActivity.class.getSimpleName();

    LocalBroadcastManager mLocalBroadcastManager;
    QDListenerService mListenerService;
    boolean mBound = false;
    LineChart mChart;
    TextView mReportView;
    Button logButton;
    Button inspectButton;
    View inspectView;
    TextView steadyView;
    TextView triggerView;
    TextView proView;
    View eqView;
    TextView eqTime;
    TextView eqLevel;
    TextView eqPGA;
    Button TestButton;
    private String[] mLabels = {"X axis", "Y axis", "Z axis"};
    private int[] mColors = {Color.RED, Color.GREEN, Color.BLUE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inspectView = (View) findViewById(R.id.pocket);
        steadyView = (TextView)findViewById(R.id.steadyView);
        triggerView = (TextView)findViewById(R.id.triggerView);
        proView = (TextView)findViewById(R.id.processingView);
        eqView = (View)findViewById(R.id.eqPocket);
        eqLevel = (TextView)findViewById(R.id.eqLevel);
        eqTime = (TextView)findViewById(R.id.eqTime);
        eqPGA = (TextView)findViewById(R.id.eqPGA);
        TestButton=(Button)findViewById(R.id.TestButton);
        final Button button = findViewById(R.id.buttonRecord);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mBound) {
                    boolean result = mListenerService.startListen();
                    Log.d(TAG, "start listen " + result);

                    LineData lineData = new LineData();
                    lineData.addDataSet(createDataSet(0));
                    lineData.addDataSet(createDataSet(1));
                    lineData.addDataSet(createDataSet(2));
                    mChart.setData(lineData);
                }
            }
        });

        mReportView = findViewById(R.id.tvReport);

        mChart = (LineChart) findViewById(R.id.chart);

        inspectButton = (Button)findViewById(R.id.InspectButton);
        logButton = (Button)findViewById(R.id.LogButton);
        inspectButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                inspectButton.setVisibility(View.INVISIBLE);
                logButton.setVisibility(View.VISIBLE);
                mReportView.setVisibility(View.INVISIBLE);
                inspectView.setVisibility(View.VISIBLE);
            }
        });
        logButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                logButton.setVisibility(View.INVISIBLE);
                inspectButton.setVisibility(View.VISIBLE);
                inspectView.setVisibility(View.INVISIBLE);
                mReportView.setVisibility(View.VISIBLE);
            }
        });

        TestButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent1 = new Intent(getApplicationContext(), BleAdvertising.class);
                intent1.putExtra("Level", String.valueOf(1));
                startService(intent1);
                Toast.makeText(MainActivity.this, "Advertising Start", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private LineDataSet createDataSet(int idx) {
        LineDataSet set = new LineDataSet(null, mLabels[idx]);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3f);
        set.setColor(mColors[idx]);
        set.setHighlightEnabled(false);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;
    }

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(QDListenerService.ACTION_UPDATE_DATA)) {
                QDPacket packet = intent.getParcelableExtra(QDListenerService.DATA_PACKET);
                addEntry(packet.vals[0], packet.vals[1], packet.vals[2], packet.ts);
            } else if (intent.getAction().equals(QDListenerService.ACTION_UPDATE_EVENT)) {
                QDPacket packet = intent.getParcelableExtra(QDListenerService.DATA_PACKET);
                String eventValue = ((QDReportPacket) packet).getEventType();
                if(eventValue != null) {
                    mReportView.setText(eventValue);
                    Log.d(TAG, "Event::" + ((QDReportPacket) packet).getEventType());
                    switch (eventValue){
                        case "Steady" :
                            steadyView.setBackgroundColor(Color.parseColor("#78E775"));
                            triggerView.setBackgroundColor(Color.parseColor("#00ff0000"));
                            proView.setBackgroundColor(Color.parseColor("#00ff0000"));
                            Log.d(TAG, "Current State: Steady");
                            break;
                        case "Trigger" :
                            steadyView.setBackgroundColor(Color.parseColor("#00ff0000"));
                            triggerView.setBackgroundColor(Color.parseColor("#F0DF4E"));
                            proView.setBackgroundColor(Color.parseColor("#00ff0000"));
                            Log.d(TAG, "Current State: Trigger");
                            break;
                        case "Processing" :
                            steadyView.setBackgroundColor(Color.parseColor("#00ff0000"));
                            triggerView.setBackgroundColor(Color.parseColor("#00ff0000"));
                            proView.setBackgroundColor(Color.parseColor("#4EB7F0"));
                            Log.d(TAG, "Current State: Processing");
                            break;
                    }
                    if(((QDReportPacket) packet).getEventType().equals("Earthquake level")) {
                        Log.i(TAG, "paketlevel = " + packet.level);
                        if (packet.level != 0) {
                            Intent intent1 = new Intent(getApplicationContext(), BleAdvertising.class);
                            intent1.putExtra("Level", String.valueOf(packet.level));
                            startService(intent1);
                            eqView.setBackgroundColor(Color.parseColor("#E24949"));
                            long now = System.currentTimeMillis();
                            Date date = new Date(now);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String curTime = sdf.format(date);
                            eqTime.setText(curTime);
                            eqPGA.setText(String.valueOf(packet.type));
                            eqLevel.setText(packet.level);
                        }
                        else{
                            eqView.setBackgroundColor(Color.parseColor("#00ff0000"));
                        }
                    }
                } else {
                    Log.d(TAG, "Event::Unexpected byte coming");
                }
            } else if (intent.getAction().equals(QDListenerService.ACTION_CONNECTION_CLOSED)) {
                Log.d(TAG, "Connection closed");
            } else if (intent.getAction().equals(QDListenerService.ACTION_CONNECTION_REFUSED)) {
                Log.d(TAG, "Connection refused");
            } else {
                Log.d(TAG, "Unknown event");
            }
        }
    };

    private void addEntry(float x, float y, float z, long ts) {
        LineData data = mChart.getData();
        int num = data.getDataSetByIndex(0).getEntryCount();
        data.addEntry(new Entry(num, x), 0);
        data.addEntry(new Entry(num, y), 1);
        data.addEntry(new Entry(num, z), 2);
        data.notifyDataChanged();
        mChart.notifyDataSetChanged();
        mChart.setVisibleXRangeMaximum(100);
        mChart.moveViewToX(ts);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, QDListenerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());

        IntentFilter filter = new IntentFilter();
        filter.addAction(QDListenerService.ACTION_UPDATE_EVENT);
        filter.addAction(QDListenerService.ACTION_UPDATE_DATA);
        filter.addAction(QDListenerService.ACTION_CONNECTION_CLOSED);
        filter.addAction(QDListenerService.ACTION_CONNECTION_REFUSED);
        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        mLocalBroadcastManager.unregisterReceiver(mBroadcastReceiver);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            QDListenerService.LocalBinder binder = (QDListenerService.LocalBinder) iBinder;
            mListenerService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }
    };

    public int ByteToInt(byte bytes[]){
        return ((((int)bytes[0] & 0xff) << 24) |
                (((int)bytes[1] & 0xff) << 16) |
                (((int)bytes[2] & 0xff) << 8) |
                (((int)bytes[3] & 0xff)));
    }
}
