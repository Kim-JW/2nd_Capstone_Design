package sslab.knu.ac.kr.qdmonitor;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.util.Log;

public class BleAdvertising extends Service {
    private static final String TAG = BleAdvertising.class.getSimpleName();
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;
    @Override
    public void onCreate() {
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId){
        int i = super.onStartCommand(intent, flags, startId);
        String Level = intent.getStringExtra("Level");
        switch (Level){
            case "1":
                startAdvertising(new byte[]{0x01});
                break;
            case "2":
                startAdvertising(new byte[]{0x02});
                break;
            case "3":
                startAdvertising(new byte[]{0x03});
                break;
            case "99":
                stopAdvertising();
                stopSelf();
                break;
        }
        return i;
    }

    private void startAdvertising(byte[] sendData) {
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        mBluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
        if (mBluetoothLeAdvertiser == null) {
            Log.w(TAG, "Failed to create advertiser");
            return;
        }

        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                .setConnectable(true)
                .setTimeout(0)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                .build();

        AdvertiseData data = new AdvertiseData.Builder()
                .addServiceData(shortUUID("9925"),sendData)
                .setIncludeDeviceName(true)
                .build();

        mBluetoothLeAdvertiser
                .startAdvertising(settings, data, mAdvertiseCallback);
        Log.i("BLE","send data = "+data.toString());
    }
    public ParcelUuid shortUUID(String s) {
        return ParcelUuid.fromString("0000" + s + "-0000-1000-8000-00805F9B34FB");
    }

    private void stopAdvertising() {
        if (mBluetoothLeAdvertiser == null) return;
        mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback);
    }

    private AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            Log.i(TAG, "LE Advertise Started.");
        }
        @Override
        public void onStartFailure(int errorCode) {
            Log.w(TAG, "LE Advertise Failed: "+errorCode);
        }
    };

    public IBinder onBind(Intent intent) {
        return null;
    }
}
