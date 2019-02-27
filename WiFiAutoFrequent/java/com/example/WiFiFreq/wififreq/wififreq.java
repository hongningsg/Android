package com.example.WiFiFreq.wififreq;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class wififreq extends AppCompatActivity
{
    private TextView textViewInformation;

    private Button button;

    private WifiManager wifiManager;

    private String information;

    private double five_GHz;

    private String support_5G;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab10);

        textViewInformation = (TextView) findViewById(R.id.textViewInformation);

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                find_protocol();
                textViewInformation.setText(information);
    }
});

        wifiManager = (WifiManager) this.getApplicationContext().getSystemService(WIFI_SERVICE);
        if (wifiManager.is5GHzBandSupported()) {
            support_5G = "Device supports 5 GHz connection";
        } else {
            support_5G = "Device does not supports 5 GHz connection";
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    private void find_protocol()
    {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        //  identify  protocols
        String protocol = "";
        if (wifiInfo.getFrequency() / 1000 == 2)
        {   five_GHz = 2.4;
            if (wifiInfo.getLinkSpeed() / 20 == 0)
            {
                protocol += "802.11b";
            } else if (wifiInfo.getLinkSpeed() / 60 == 0)
            {
                protocol += "802.11g";
            } else
            {
                protocol += "802.11n";
            }
        } else if (wifiInfo.getFrequency() / 1000 == 5)
        {   five_GHz = 5;
            if (wifiInfo.getLinkSpeed() / 100 == 0)
            {
                protocol += "802.11a";
            } else if (wifiInfo.getLinkSpeed() / 100 <= 4)
            {
                protocol += "802.11n";
            } else
            {
                protocol += "802.11ac";
            }
        } else
        {
            protocol += "unknow";
        }

        wifiManager.startScan();

        if (wifiInfo.getBSSID() != null)
        {
            information = "Availability of 5 GHz: \n" + support_5G +

                    "\n\n\nSSID: " + wifiInfo.getSSID() +
                    "\n" + five_GHz + "GHz WiFi connection" +
                    "\nIP: " + intToIp(wifiInfo.getIpAddress()) +
                    "\nBSSID: " + wifiInfo.getBSSID().toUpperCase() +
                    "\nProtocol: " + protocol +
                    "\nData Rate: " + wifiInfo.getLinkSpeed() + "Mbps" +
                    "\nFrequency: " +wifiInfo.getFrequency() + "MHz";
        }
    }

    private String intToIp(int i)
    {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF);
    }

}