package sslab.knu.ac.kr.qdmonitor;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Locale;

import sslab.knu.ac.kr.qdmonitor.packet.QDDataPacket;
import sslab.knu.ac.kr.qdmonitor.packet.QDPacket;
import sslab.knu.ac.kr.qdmonitor.packet.QDReportPacket;

public class QDListenerService extends Service {

    public static final String TAG = QDListenerService.class.getSimpleName();

    Socket mSocket;
    DataInputStream mSocketIn;

    LocalBroadcastManager mLocalBroadcastManager;

    public static final String SERVER_IP = "192.168.10.1";
    public static final int SERVER_PORT = 2222;

    public static final String ACTION_UPDATE_DATA = "sslab.knu.ac.kr.qdmonitor.action_update_data";
    public static final String DATA_PACKET = "sslab.knu.ac.kr.qdmonitor.data_packet";
    public static final String ACTION_UPDATE_EVENT = "sslab.knu.ac.kr.qdmonitor.action_update_event";
    public static final String ACTION_CONNECTION_REFUSED = "sslab.knu.ac.kr.qdmonitor.action_connection_refused";
    public static final String ACTION_CONNECTION_CLOSED = "sslab.knu.ac.kr.qdmonitor.action_connection_closed";
    private final IBinder mBinder = new LocalBinder();
    boolean flag = true;
    public class LocalBinder extends Binder {
        QDListenerService getService() {
            return QDListenerService.this;
        }
    }

    private class QDListenThread extends Thread {
        @Override
        public void run() {
            boolean result = setConnection(SERVER_IP, SERVER_PORT);
            if(!result) {
                Log.d(TAG, "Connection failed");
                return;
            } else {
                Log.d(TAG, "Connected!");
            }
            while(flag) {
                try {
                    Log.i("QDLS","Thread running");
                    /*
                    if (interrupted()) {
                        Log.i("QDLS","interrupt call");
                        cleanupSocket();
                        Intent intent = new Intent();
                        intent.setAction(ACTION_CONNECTION_CLOSED);
                        mLocalBroadcastManager.sendBroadcast(intent);
                        return;
                    }*/
                    byte[] values = new byte[24];
                    int rx = 0;
                    while (rx < 24) {
                        int read = mSocketIn.read(values, rx, values.length - rx);
                        rx = read + rx;
                    }
                    QDPacket packet = QDPacket.QDPacketBuilder(values);
                    Intent intent = new Intent();
                    if (packet instanceof QDDataPacket) {
                        intent.setAction(ACTION_UPDATE_DATA);

                    } else if (packet instanceof QDReportPacket) {
                        intent.setAction(ACTION_UPDATE_EVENT);
                    }
                    intent.putExtra(DATA_PACKET, packet);
                    mLocalBroadcastManager.sendBroadcast(intent);
                } catch (IOException e) {
                    e.printStackTrace();
                    cleanupSocket();
                    Intent intent = new Intent();
                    intent.setAction(ACTION_CONNECTION_REFUSED);
                    mLocalBroadcastManager.sendBroadcast(intent);
                }
            }
        }
    }

    QDListenThread mThread;

    private boolean setConnection(String ip, int port) {
        try {
            mSocket = new Socket(SERVER_IP, SERVER_PORT);
            mSocket.setReuseAddress(true);
            mSocketIn = new DataInputStream(mSocket.getInputStream());
            return true;
        } catch(IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean startListen() {
        if(mThread == null) {
            mThread = new QDListenThread();
            mThread.setDaemon(true);
            mThread.start();
        } else {
            Log.d(TAG, "Already connected");
        }
        return true;
    }

    public void stopListen() {
        Log.i("QDLS","StopListen call");
        flag = false;
        //mThread.interrupt();
        //mThread = null;
    }

    private void cleanupSocket() {
        try {
            if(mSocketIn != null) {
                mSocketIn.close();
                mSocketIn = null;
            }
            if(mSocket != null) {
                mSocket.close();
                mSocket = null;
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("QDLS","onDestroy call");
        stopListen();
        cleanupSocket();
    }
}
