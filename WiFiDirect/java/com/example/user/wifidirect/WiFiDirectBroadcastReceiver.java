package com.example.user.wifidirect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

/**
 * Created by User on 17/10/2018.
 */

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {
    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel channel;
    private WifiDirect mActivity;

    public WiFiDirectBroadcastReceiver(WifiP2pManager mManager, WifiP2pManager.Channel mChannel, WifiDirect wifiDirect){
        this.wifiP2pManager = mManager;
        this.channel = mChannel;
        this.mActivity = wifiDirect;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)){
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED){
                Toast.makeText(context, "WiFi is ON", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(context, "WiFi is OFF", Toast.LENGTH_SHORT).show();
            }
        }else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)){
            if (wifiP2pManager != null){
                wifiP2pManager.requestPeers(channel, mActivity.peerListListener);
            }
        }else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)){
            if (wifiP2pManager == null){
                return;
            }
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (networkInfo.isConnected()){
                wifiP2pManager.requestConnectionInfo(channel, mActivity.connectionInfoListener);
            }else {
                Toast.makeText(context, "Device Disconnected", Toast.LENGTH_SHORT).show();
            }
        }else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)){

        }
    }
}
