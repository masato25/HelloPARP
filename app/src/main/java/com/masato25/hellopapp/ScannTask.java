package com.masato25.hellopapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;

import java.util.TimerTask;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.ListView;

/**
 * Created by masato on 2017/7/4.
 */

public class ScannTask extends TimerTask {
    private BluetoothAdapter BA;
    private ListView listView;

    public ScannTask(BluetoothAdapter ba, ListView lv) {
        this.BA = ba;
        this.listView = lv;
    }

    @Override
    public void run() {
        if (!BA.isDiscovering()) {
            BA.startDiscovery();
            Log.v("ddd-tasks", "BA.startDiscovery()");
        } else {
            Log.v("ddd-tasks", "not run");
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {

                }
            };
}
