package com.example.user.battery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class battery extends AppCompatActivity {
    private TextView batteryTxt;
    private int currLevel;
    private Intent batteryStatus;
    private float batteryPct;
    private float currbat;
    private Thread GPSThread;
    private Thread WiFiThread;
    private Thread NormalThread;
    private Boolean runGps;
    private Boolean runWifi;
    private Boolean runNormal;
    private int status;
    private int count = 0;
    private boolean isCharging;
    public boolean usbCharge;
    public boolean acCharge;
    private TextView batteryInfo;
    private Button gpsbtn;
    private Button wifibtn;
    private Button normalbtn;
    protected PowerConnectionReceiver receiver;

    private void getBatteryInfo(){
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        batteryPct = level / (float)scale;
    }

    private void updateTxt(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String charging = isCharging?"":"NOT";
                String outputStr = "Current level of battery is: " + batteryPct*100 + "%\nMobile is " + charging + " charging\n";
                if (isCharging){
                    if (acCharge){
                        outputStr += "Source: AC";
                    }
                    else {
                        outputStr += "Source: USB";
                    }

                }
                batteryTxt.setText(outputStr);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery);
        batteryTxt = (TextView)findViewById(R.id.status);
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryInfo = (TextView)findViewById(R.id.batteryop);
        wifibtn = (Button)findViewById(R.id.UseWifi);
        gpsbtn = (Button)findViewById(R.id.UseGPS);
        normalbtn = (Button)findViewById(R.id.normal);
        batteryStatus = this.registerReceiver(null, ifilter);
        //getBatteryInfo();
        updateTxt();
        Thread checkingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //getBatteryInfo();
                    updateTxt();
                }
            }
        });
        checkingThread.start();
        GPSThread = new Thread(new Runnable() {
            @Override
            public void run() {
                currbat = batteryPct;
                count = 0;
                while (runGps){
                    int remainTime = 10 - count;
                    String str = "Use GPS\nTime Remaining: " + remainTime + " minutes.";
                    SetTxt(str);
                    //batteryInfo.setText("Use GPS\nTime Remaining: " + remainTime + " minutes.");
                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (runGps)
                    count++;
                }
                if (runGps){
                    runGps = false;
                    String OutStr = "Using GPS for 10 minutes:\nInitial level of battery: " + Float.toString(currbat*100) + "%\nFinal level: " +
                            Float.toString(batteryPct*100) + " %\nConsumed battery " + Float.toString((currbat-batteryPct)*100) + "%";
                    SetTxt(OutStr);
                    count = 0;
                }
            }
        });
        WiFiThread = new Thread(new Runnable() {
            @Override
            public void run() {
                currbat = batteryPct;
                count = 0;
                while (runWifi && count < 10){
                    int remainTime = 10 - count;
                    String str = "Use WiFi\nTime Remaining: " + remainTime + " minutes.";
                    SetTxt(str);
                    //batteryInfo.setText("Use WiFi\nTime Remaining: " + remainTime + " minutes.");
                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (runWifi)
                    count++;
                }
                if (runWifi){
                    runWifi = false;
                    String OutStr = "Using Wi-Fi for 10 minutes:\nInitial level of battery: " + Float.toString(currbat*100) + "%\nFinal level: " +
                            Float.toString(batteryPct*100) + " %\nConsumed battery " + Float.toString((currbat-batteryPct)*100) + "%";
                    SetTxt(OutStr);
                    count = 0;
                }
            }
        });
        NormalThread = new Thread(new Runnable() {
            @Override
            public void run() {
                currbat = batteryPct;
                count = 0;
                while (runNormal && count < 10){
                    int remainTime = 10 - count;
                    //batteryInfo.setText("Normal usage\nTime Remaining: " + remainTime + " minutes.");
                    String str = "Normal usage\nTime Remaining: " + remainTime + " minutes.";
                    SetTxt(str);
                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (runNormal)
                    count++;
                }
                if (runNormal){
                    runNormal = false;
                    String OutStr = "Normal usage of mobile phone for 10 minutes:\nInitial level of battery: " + Float.toString(currbat*100) + "%\nFinal level: " +
                            Float.toString(batteryPct*100) + " %\nConsumed battery " + Float.toString((currbat-batteryPct)*100) + "%";
                    SetTxt(OutStr);
                    count = 0;
                }
            }
        });
        normalbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runGps = false;
                runWifi = false;
                runNormal = true;
                count = 0;
                NormalThread.start();
            }
        });

        wifibtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runGps = false;
                runNormal = false;
                runWifi = true;
                count = 0;
                WiFiThread.start();
            }
        });

        gpsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runNormal = false;
                runWifi = false;
                runGps = true;
                count = 0;
                GPSThread.start();
            }
        });

    }

    private void SetTxt(final String str){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                batteryInfo.setText(str);
            }
        });
    }

    public class PowerConnectionReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            batteryPct = level / (float)scale;
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, ifilter);
            int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
            acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

            if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)){
                isCharging = true;
            }else {
                intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED);
                isCharging = false;
            }
//            Toast.makeText(battery.this, "in here" + acCharge, Toast.LENGTH_LONG).show();
//            usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
//            acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        receiver = new PowerConnectionReceiver();
        IntentFilter ifilter = new IntentFilter();
        ifilter.addAction(Intent.ACTION_POWER_CONNECTED);
        ifilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        registerReceiver(receiver, ifilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }
}
