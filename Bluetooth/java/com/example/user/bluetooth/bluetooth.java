package com.example.user.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class bluetooth extends AppCompatActivity {
    private Button checkbtn;
    private Button discbtn;
    private TextView checkTxt;
    private ListView listView;
    private ArrayAdapter adapter;
    private ArrayList<String> arrayList = new ArrayList<>();
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private ArrayList<BluetoothDevice> btDvs = new ArrayList<>();

    private final BroadcastReceiver Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)){

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                btDvs.add(device);
                arrayList.add(device.getName() + "\n" + device.getAddress());
                adapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        checkbtn = (Button)findViewById(R.id.checkbutton);
        discbtn = (Button)findViewById(R.id.discover);
        checkTxt = (TextView)findViewById(R.id.checktxt);
        listView = (ListView)findViewById(R.id.listtxt);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                connectBlueTooth(i);
            }
        });
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);
        checkbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bluetoothAdapter == null){
                    checkTxt.setText("Device not support bluetooth");
                }
                if (!bluetoothAdapter.isEnabled()){
                    checkTxt.setText("Bluetooth not enabled!");
                }else {
                    checkTxt.setText("Bluetooth enabled!");
                }
            }
        });

        discbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchBlueTooth();
            }
        });
    }

    public void connectBlueTooth(int position){
        Boolean ifSuccess=false;
        try {
            ifSuccess = createBond(btDvs.get(position));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(ifSuccess){
            Toast.makeText(bluetooth.this, "Success", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(bluetooth.this, "fail", Toast.LENGTH_LONG).show();
        }
        connect(btDvs.get(position));
    }

    private Boolean connect(BluetoothDevice bdDevice){
        Boolean bool = false;
        try {
            Class c1 = Class.forName("android.bluetooth.BluetoothDevice");
            Class[] par = {};
            Method method = c1.getMethod("createBond", par);
            Objects[] args = {};
            bool =(Boolean)method.invoke(bdDevice);
        }catch (Exception e){
            e.printStackTrace();
        }
        return bool.booleanValue();
    }

    public boolean createBond(BluetoothDevice btDevice)throws Exception{
        Class cls1 = Class.forName("android.bluetooth.BluetoothDevice");
        Method createBondMethod = cls1.getMethod("createBond");
        Boolean returnValue = (Boolean)createBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }

    private void searchBlueTooth(){
        arrayList.clear();
        btDvs.clear();
        if (bluetoothAdapter.isDiscovering()){
            bluetoothAdapter.cancelDiscovery();
            checkBTPermissions();
            bluetoothAdapter.startDiscovery();
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(Receiver, filter);
        }
        else{
            Toast.makeText(bluetooth.this, "Scanning...", Toast.LENGTH_LONG).show();
            checkBTPermissions();
            bluetoothAdapter.startDiscovery();
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(Receiver, filter);
        }

    }

    private void checkBTPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }else{
            Toast.makeText(bluetooth.this, "No Permission", Toast.LENGTH_LONG).show();
           // Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }
}
