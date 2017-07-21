package com.masato25.hellopapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.jar.Manifest;
import org.phoenixframework.channels.*;

public class MainActivity extends AppCompatActivity {

    public static int REQUEST_BLUETOOTH = 1;
    private TextView mTextMessage;
    private BluetoothAdapter BA;
    private Set<BluetoothDevice> pairedDevices;
    private Set <DeviceItem> deviceItemList;
    private ArrayAdapter<DeviceItem> mAdapter;
    private ListView listView;
    private ProgressBar statusBar;
    private ArrayList blueObj = new ArrayList();
    private ArrayList<Avatar> sensorList = new ArrayList<Avatar>();
    private ArrayList<Avatar> sensorListTmp = new ArrayList<Avatar>();
    private boolean isOpen;
    private WebAccessThread webAccess;
    private NetWorkingTmp httcli = new NetWorkingTmp();
    private LocationManager locationManager;
    protected double Latitude;
    protected double Longitude;
    protected Context context;
    protected int duration = Toast.LENGTH_SHORT;
    private Socket socket;
    private Channel channel;
    private CheckBox cb;
    private String webserver = "${myserver}";
    private Context mycontext;
    private ObjectNode ndf = new ObjectNode(JsonNodeFactory.instance);
    private UUID SERVICE_UUID = UUID.fromString("19b10011-e8f2-537e-4f6c-d104768a1214");
    private UUID CHARACTERISTIC_COUNTER_UUID = UUID.fromString("19b10011-e8f2-537e-4f6c-d104768a1214");
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            String action = intent.getAction();
            Log.v("AAA", "action: " + action);
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Log.v("AAA", "ACTION_FOUND");
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //only find BLE devices
                if(device.getName() != null && !device.getName().equals("")){
                    if((cb.isChecked() && device.getName().indexOf("PARP") >= 0) || !cb.isChecked()) {
                        // If it's already paired, skip it, because it's been listed already
                        // device.getBondState() != BluetoothDevice.BOND_BONDED
                        int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                        Date date = new Date();
                        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        myFormat.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
                        String formattedDate = myFormat.format(date);
                        blueObj.add(device.getName() + "\n" + device.getAddress() + "\n" + rssi + "dB" + "\n" + formattedDate);
                        final Avatar avatar = new Avatar(
                                device.getName(), device.getAddress(),
                                device.getBondState(), "", device.getType(), rssi, Latitude, Longitude);
                        BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
                            @Override
                            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                                super.onConnectionStateChange(gatt, status, newState);
                                if (newState == BluetoothProfile.STATE_CONNECTED) {
                                    Log.i("ddd", "Connected to GATT client. Attempting to start service discovery");
                                    gatt.discoverServices();
                                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                                    Log.i("ddd", "Disconnected from GATT client");
                                }
                            }
                            @Override
                            public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
                                super.onServicesDiscovered(gatt, status);
                                if (status != BluetoothGatt.GATT_SUCCESS) {
                                    Log.d("ddd-v", "GATT_SUCCESS not ok");
                                    // Handle the error
                                    return;
                                }else{
                                    Log.d("ddd-v", "GATT_SUCCESS ok");
                                }
                                for( BluetoothGattService gt : gatt.getServices()){
                                    Log.d("ddd-uuid", gt.getUuid().toString());
                                    for(final BluetoothGattCharacteristic gc : gt.getCharacteristics()){
                                        Log.d("ddd-ch-uuid", gc.getUuid().toString());
                                        if (gc.getUuid().equals(CHARACTERISTIC_COUNTER_UUID)){
                                            Log.d("ddd-cc", "checkme");
                                            gatt.readCharacteristic(gc);
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    gatt.readCharacteristic(gc);
                                                }
                                            }).start();
                                        }
                                    }
                                }

                                // Get the counter characteristic
//                                BluetoothGattCharacteristic characteristic = gatt
//                                        .getService(SERVICE_UUID)
//                                        .getCharacteristic(CHARACTERISTIC_COUNTER_UUID);
//                                gatt.readCharacteristic(characteristic);
                            }
                            @Override
                            public void onCharacteristicRead(BluetoothGatt gatt,
                                                             BluetoothGattCharacteristic characteristic, int status) {
                                byte[] data = characteristic.getValue();
                                if (data.length == 0) {
                                    Log.d("ddd-data", "data is null");
                                } else {
                                    int res = Ints.fromByteArray(data);
                                    avatar.SetStatus(res);
                                    sensorListTmp.add(avatar);
                                    Log.d("ddd-data", "@ " + res);
                                    gatt.disconnect();
                                }
                            }
                            @Override
                            public void onCharacteristicChanged(BluetoothGatt gatt,
                                                                BluetoothGattCharacteristic characteristic) {
                                this.onCharacteristicChanged(gatt, characteristic);
                                gatt.readCharacteristic(characteristic);
                            }

                        };
                        BluetoothGatt dt1 = device.connectGatt(mycontext, false, mGattCallback);
                        // deviceItemList.add(new DeviceItem(device.getName(),device.getAddress(), device.getBondState(), "false"));
                        Log.v("AAA", "device: " + device.getName());
                        listView.invalidateViews();
                    }
                }

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                try {
                    if (sensorListTmp.size() == 0 && sensorList.size() != 0) {
                        Log.v("parp", "sensorListTmp.size() == 0 && sensorList.size() != 0: " + sensorList.size());
                        for (Avatar avatar : sensorList) {
                            avatar.addMissingCount();
                            Toast.makeText(context, avatar.GetName() + ": " + avatar.getMissingCount(), duration).show();
                            Log.v("parp", "[addMissingCount-0] avatar.missingCount: " + avatar.getMissingCount());
                            if (avatar.isLost()) {
                                // set avatar's at_history status to leave
                                WebAccessThread webAccess = new WebAccessThread("http://" + webserver + "/api/avatar_leave_parking", httcli);
                                webAccess.setAvatarAddress(avatar.GetAddr());
                                Thread t = new Thread(webAccess);
                                t.start();
                                sensorList.remove(avatar);
                                Toast.makeText(context, avatar.GetName() + ": " + "leave", duration).show();
                            }
                        }
                    } else {
                        for (Avatar avatar : sensorListTmp) {
                            if(avatar.GetStatus() == 0){
                                // set avatar's at_history status to leave
                                WebAccessThread webAccess = new WebAccessThread("http://" + webserver + "/api/avatar_leave_parking", httcli);
                                webAccess.setAvatarAddress(avatar.GetAddr());
                                Thread t = new Thread(webAccess);
                                t.start();
                                Toast.makeText(context, avatar.GetName() + ": " + "leave", duration).show();
                            }else{
                                WebAccessThread webAccess = new WebAccessThread("http://" + webserver + "/api/avatars", httcli);
                                webAccess.setAvatar(avatar);
                                Thread t = new Thread(webAccess);
                                t.start();
                                Toast.makeText(context, avatar.GetName() + ": " + "parking", duration).show();
                            }
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }

                if(sensorList.size() == 0) {
                    sensorList = sensorListTmp;
                }else{
                    for (Avatar avatar : sensorListTmp) {
                        // if not exist, add it!
                        if(!checkExist(sensorList, avatar.GetAddr())){
                            sensorList.add(avatar);
                        }
                    }
                }
                Log.v("parp", "list size: " + sensorList.size());
                statusBar.setVisibility(View.INVISIBLE);
                listView.invalidateViews();
                isOpen = false;
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                isOpen = true;
                sensorListTmp = new ArrayList<Avatar>();
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.v("AAA", "start disconver");
                statusBar.setVisibility(View.VISIBLE);
                // clean listView when listView is too many
                if(listView.getCount() > 7){
                    blueObj.clear();
                    listView.invalidateViews();
                    Log.v("tasks", "clean listView");
                }
            }
        }
    };

    protected boolean checkExist(ArrayList<Avatar> alist, String maddress){
        for ( Avatar avatar : alist){
            String atmp = avatar.GetAddr();
            if(atmp.equals(maddress)){
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        statusBar = (ProgressBar) findViewById(R.id.statusBar);
        statusBar.setVisibility(View.INVISIBLE);
        final ListAdapter adapter = new ArrayAdapter<>(this , android.R.layout.simple_list_item_1 ,blueObj);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        final Button scannerbutton = (Button) findViewById(R.id.scanner);
        Switch switch1 = (Switch) findViewById(R.id.switch1);
        mycontext = this.context;
        // get permission from  android 6.0
        if(!(ContextCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED)){
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.INTERNET}, 1);
        }
        if(!(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)){
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
        if(!(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)){
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        if(!(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED)){
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_NETWORK_STATE}, 1);
        }

        // get bluetooth instance
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BA = bluetoothManager.getAdapter();
        if (BA == null || !BA.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, REQUEST_BLUETOOTH);
        }

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerToReceive(mReceiver, filter);
        context = getApplicationContext();
        cb = (CheckBox) findViewById(R.id.checkBox1);
        cb.setChecked(true);

        // gps services
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                Log.d("location", location.getLatitude() + "," + location.getLongitude());
                Latitude = location.getLatitude();
                Longitude = location.getLongitude();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {

            }
        };
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        final Timer timer= new Timer();
        final TimerTask autoScanner = new ScannTask(BA, listView);

        // 開啟camara
        // Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        // startActivityForResult(intent,0);



        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) //Line A
            {
                Log.v("isChecked", "" + isChecked);
                if(isChecked){
                    timer.schedule(autoScanner, 0, 3000);
                }else{
                    timer.cancel();
                    timer.purge();
                }
            }
        });

        final BluetoothAdapter.LeScanCallback mLeScanCallback =
                new BluetoothAdapter.LeScanCallback() {
                    public void onLeScan(final BluetoothDevice device, int rssi,
                                         byte[] scanRecord) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });
                    }
                };

        scannerbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!BA.isDiscovering()) {
                    BA.startLeScan(mLeScanCallback);
                }
            }
        });

        final Button claerbutton = (Button) findViewById(R.id.clearbutton);
        claerbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                blueObj.clear();
                listView.invalidateViews();
            }
        });
    }

    protected void registerToReceive(BroadcastReceiver mReceiver , IntentFilter filter){
        registerReceiver(mReceiver, filter);
    }


}
