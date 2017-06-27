package com.masato25.hellopapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    public static int REQUEST_BLUETOOTH = 1;
    private TextView mTextMessage;
    private BluetoothAdapter BA;
    private Set<BluetoothDevice> pairedDevices;
    private ArrayList <DeviceItem>deviceItemList;
    private ArrayAdapter<DeviceItem> mAdapter;
    private TextView mt;
    private ArrayList blueObj = new ArrayList();
    private boolean isOpen;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.v("AAA", "WILL GO");
            Log.v("AAA", "action: " + action);
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Log.v("AAA", "ACTION_FOUND");
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                // device.getBondState() != BluetoothDevice.BOND_BONDED
                blueObj.add(device.getName() + "\n" + device.getAddress());
                Log.v("AAA", "device: " + device.getName());
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (blueObj.size() == 0) {
                    blueObj.add("not found");
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.v("AAA", "start disconver");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION},2);

        BA = BluetoothAdapter.getDefaultAdapter();
        if (!BA.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, REQUEST_BLUETOOTH);
        }

        mt = (TextView) findViewById(R.id.msg);
        pairedDevices = BA.getBondedDevices();

        final ListAdapter adapter = new ArrayAdapter<>(this , android.R.layout.simple_list_item_1 ,blueObj);
        final ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        final Button button = (Button) findViewById(R.id.scanner);
        isOpen = true;
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                Log.v("AAA", "isOpen: " + isOpen);
                if (isOpen) {
                    registerReceiver(mReceiver, filter);
                    BA.startDiscovery();
                } else {
                    unregisterReceiver(mReceiver);
                    BA.cancelDiscovery();
                }
                isOpen = !isOpen;
                scanBuleTool();
                mt.append("a");
                //need a action for update listview
                listView.refreshDrawableState();
            }
        });
    }

    private void scanBuleTool() {
        Set<BluetoothDevice> pairedDevices = BA.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                blueObj.add(device.getName());
                DeviceItem newDevice= new DeviceItem(device.getName(),device.getAddress(),"false");
                deviceItemList.add(newDevice);
            }
        }
    }

}
