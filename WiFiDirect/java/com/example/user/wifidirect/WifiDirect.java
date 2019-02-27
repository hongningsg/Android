package com.example.user.wifidirect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class WifiDirect extends AppCompatActivity {
    private Button checkBtn;
    private Button DiscoverBtn;
    private TextView enableTxt;
    private ListView listView;
    private WifiManager manager;
    private WifiP2pManager wifiManager;
    private WifiP2pManager.Channel channel;
    private BroadcastReceiver Receiver;
    private IntentFilter filter;
    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    private String[] deviceNameArray;
    private WifiP2pDevice[] deviceArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_direct);
        checkBtn = (Button)findViewById(R.id.check_button);
        DiscoverBtn =(Button)findViewById(R.id.scanBtn);
        enableTxt = (TextView)findViewById(R.id.enablecheck);
        listView = (ListView)findViewById(R.id.wifiList);
        wifiManager = (WifiP2pManager) getApplicationContext().getSystemService(Context.WIFI_P2P_SERVICE);
        manager = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
        manager.setWifiEnabled(true);
        channel = wifiManager.initialize(this, getMainLooper(), null);
        Receiver = new WiFiDirectBroadcastReceiver(wifiManager, channel, this);
        filter = new IntentFilter();
        filter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        filter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        WifiEnableListener();
    }

    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
            if (!wifiP2pDeviceList.getDeviceList().equals(peers)){
                peers.clear();
                peers.addAll(wifiP2pDeviceList.getDeviceList());
                deviceNameArray = new String[wifiP2pDeviceList.getDeviceList().size()];
                deviceArray = new WifiP2pDevice[wifiP2pDeviceList.getDeviceList().size()];
                int index = 0;
                for (WifiP2pDevice device: wifiP2pDeviceList.getDeviceList()){
                    deviceNameArray[index] = device.deviceName;
                    deviceArray[index] =device;
                    index++;
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, deviceNameArray);
                listView.setAdapter(adapter);
            }
            if (peers.size() == 0){
                Toast.makeText(getApplicationContext(),"No Device Found", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    };

    final WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            final InetAddress groupOwnerAddress = wifiP2pInfo.groupOwnerAddress;
            if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner){
                Toast.makeText(WifiDirect.this, "Host", Toast.LENGTH_SHORT).show();
            }else if (wifiP2pInfo.groupFormed){
                Toast.makeText(WifiDirect.this, "Guest", Toast.LENGTH_SHORT).show();
            }
        }
    };
    private void WifiEnableListener() {
        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wifiManager.WIFI_P2P_STATE_ENABLED != 2){
                    enableTxt.setText("WiFi-Direct NOT available!");
                }else {
                    enableTxt.setText("WiFi-Direct available");
                }
            }
        });
        DiscoverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wifiManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(WifiDirect.this, "Start Discovery", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int i) {
                        Toast.makeText(WifiDirect.this, "Start Discovery Fail", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final WifiP2pDevice device = deviceArray[i];
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                wifiManager.connect(channel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(WifiDirect.this, "Connected to " + device.deviceName, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(int i) {
                        Toast.makeText(WifiDirect.this, "Cannot connected to " + device.deviceName, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(Receiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(Receiver);
    }
}
