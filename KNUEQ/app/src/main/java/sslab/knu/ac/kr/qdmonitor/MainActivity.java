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

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import sslab.knu.ac.kr.qdmonitor.packet.QDPacket;
import sslab.knu.ac.kr.qdmonitor.packet.QDReportPacket;

public class MainActivity extends AppCompatActivity {

    public final static String TAG = MainActivity.class.getSimpleName();

    LocalBroadcastManager mLocalBroadcastManager;
    QDListenerService mListenerService;
    boolean mBound = false;
    LineChart mChart;
    TextView mReportView;

    private String[] mLabels = {"X axis", "Y axis", "Z axis"};
    private int[] mColors = {Color.RED, Color.GREEN, Color.BLUE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                    if(((QDReportPacket) packet).getEventType().equals("Earthquake level")) {
                        Log.i(TAG, "paketlevel = " + packet.level);
                        if (packet.level != 0) {
                            Intent intent1 = new Intent(getApplicationContext(), PopupService.class);
                            intent1.putExtra("Level", String.valueOf(packet.level));
                            startActivity(intent1);
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
