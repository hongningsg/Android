package com.example.user.wifiscanner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WifiScan extends AppCompatActivity {
    private WifiManager wifiManager;
    private ListView listView;
    private Button button;
    private List<ScanResult> results;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter adapter;
    private WifiInfo wifiInfo;
    private HashMap<String, Integer> wifimap = new HashMap<>();
    private String HardCodePassWord = "wildcats";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_scan);
        String[] PERMS_INITIAL={
                Manifest.permission.ACCESS_FINE_LOCATION,
        };
        ActivityCompat.requestPermissions(this, PERMS_INITIAL, 127);
        button = (Button) findViewById(R.id.scanBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanWiFi();
            }
        });
        listView = (ListView) findViewById(R.id.wifiList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                connectWifi(i);
            }
        });
        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()){
            Toast.makeText(this,"WiFi is disabled", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
            //if (wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLING) wifiManager.setWifiEnabled(true);
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);
        wifiInfo = wifiManager.getConnectionInfo();
        scanWiFi();
    }

    private void connectWifi(int position)
    {
        String rawStr = arrayList.get(position);
        String[] parts = rawStr.split("--");
        String networkSSID = parts[0];
        //String networkSSID = arrayList.get(position);
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + networkSSID + "\"";
        conf.preSharedKey = "\""+ HardCodePassWord +"\"";
        wifiManager.addNetwork(conf);
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for( WifiConfiguration i : list ) {
            if(i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();
                wifiInfo = wifiManager.getConnectionInfo();
                int ip = wifiInfo.getIpAddress();
                String ipAddress = Formatter.formatIpAddress(ip);
                Toast.makeText(this,networkSSID + " " + ipAddress, Toast.LENGTH_LONG).show();
                break;
            }
        }
    }

    private void scanWiFi(){
        arrayList.clear();
        wifiManager.startScan();
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        Toast.makeText(this, "Scanning...", Toast.LENGTH_SHORT).show();
    }

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            results = wifiManager.getScanResults();
            unregisterReceiver(this);
            wifimap.clear();
            for (ScanResult scanResult : results){
                int level = WifiManager.calculateSignalLevel(scanResult.level,100);
                if (wifimap.containsKey(scanResult.SSID)){
                    if (wifimap.get(scanResult.SSID) < level)
                    wifimap.put(scanResult.SSID, level);
                }else {
                    wifimap.put(scanResult.SSID, level);
                }
                //arrayList.add(scanResult.SSID + "--" + level);
                //adapter.notifyDataSetChanged();
            }
            for (String key : wifimap.keySet()){
                arrayList.add(key + "--" + wifimap.get(key));
                adapter.notifyDataSetChanged();
            }
            adapter.notifyDataSetChanged();
            wifiManager.startScan();
        }
    };
}
