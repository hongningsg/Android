package com.example.user.shared;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.MainThread;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Shared extends AppCompatActivity {
    private TextView textView;
    private TextView storageView;
    private EditText editText;
    private Button saveButton;
    private Button checkButton;
    private String text;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT = "text";
    private ListView listView;
    private Button SaveBtnFile;
    private Button ReadBtnFile;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter adapter;
    private  WifiManager wifiManager;
    private LocationManager locationManager;
    private LocationListener loc;
    private String sloc ="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared);
        textView = (TextView)findViewById(R.id.savedtxt);
        storageView = (TextView)findViewById(R.id.storageTxt);
        editText = (EditText)findViewById(R.id.edittxt);
        saveButton = (Button)findViewById(R.id.save);
        checkButton = (Button)findViewById(R.id.checkbtn);
        SaveBtnFile = (Button)findViewById(R.id.SaveBtn);
        ReadBtnFile = (Button)findViewById(R.id.ReadBtn);
        listView = (ListView)findViewById(R.id.listview);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);
        wifiManager =(WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        loc = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                if (location != null) {
                    sloc = Double.toString(location.getLongitude()) +" "+Double.toString(location.getLatitude());
                    if (isExternalStorageWritable() && checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                        File textFile = new File(Environment.getExternalStorageDirectory(), "TestString.txt");
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(textFile);
                            java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                            String date = dateFormat.format(new java.util.Date());
                            fos.write(date.getBytes());
                            fos.write("\n".getBytes());
                            fos.write(sloc.getBytes());
                            fos.write("\n".getBytes());
                            WifiInfo wfi = wifiManager.getConnectionInfo();
                            fos.write(wfi.getSSID().toString().getBytes());
                            fos.write("\n".getBytes());
                            fos.write(Integer.toString(wfi.getRssi()).getBytes());
                            fos.write(" DBM".getBytes());
                            fos.write("\n".getBytes());
                            fos.close();
                            //Toast.makeText(this, "File Saved", Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else {

                        //Toast.makeText(this, "Cannot write to external storage", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView.setText(editText.getText().toString());
                saveData();
            }
        });
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean exist = isExternalStorageWritable();
            }
        });
        SaveBtnFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeFile(view);
            }
        });
        ReadBtnFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readFile(view);
            }
        });

        loadData();
        updateViews();
    }

    public void readFile(View view){
        if (isExternalStorageReadable()){
            StringBuilder sb = new StringBuilder();
            try{

                File textFile = new File(Environment.getExternalStorageDirectory(), "TestString.txt");
                FileInputStream fis = new FileInputStream(textFile);
                if (fis != null){

                    InputStreamReader isr = new InputStreamReader(fis);
                    BufferedReader buff = new BufferedReader(isr);
                    String line = null;
                    arrayList.clear();
                    while ((line = buff.readLine()) != null){
                        arrayList.add(line);
                    }
                    fis.close();;
                }
                adapter.notifyDataSetChanged();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public void writeFile(View view){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        , 10);
            }
            return;
        }
        // this code won't execute IF permissions are not allowed, because in the line above there is return statement.
        locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER,0,0,loc);



    }

    public boolean checkPermission(String permission){
//        int check = ContextCompat.checkSelfPermission(this,permission);
//        return (check == PackageManager.PERMISSION_GRANTED);

        if(Build.VERSION.SDK_INT>=23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "Have Permission", Toast.LENGTH_LONG).show();
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                Toast.makeText(this, "Try again", Toast.LENGTH_LONG).show();
                return false;
            }
        }else{
            return true;
        }
    }

    private boolean isExternalStorageReadable(){
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState())){
            return true;
        }else return false;

    }

    public boolean isExternalStorageWritable() {



        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            StatFs stat = new
                    StatFs(Environment.getExternalStorageDirectory().getPath());
            long bytesAvailable = (long)stat.getBlockSize() *
                    (long)stat.getBlockCount();
            long megAvailable = bytesAvailable / 1048576;
            storageView.setText("Storage:" + Long.toString(megAvailable/1000) + "G");
            //Toast.makeText(this, "External storage available", Toast.LENGTH_LONG).show();
            return true;
        }
        //Toast.makeText(this, "External storage NOT available", Toast.LENGTH_LONG).show();
        return false;
    }

    private void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TEXT, textView.getText().toString());
        editor.apply();
        Toast.makeText(this, "Saving...", Toast.LENGTH_SHORT).show();
    }

    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        text = sharedPreferences.getString(TEXT, "");
    }

    public void updateViews(){
        textView.setText(text);
    }
}
