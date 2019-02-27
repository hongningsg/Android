package com.example.user.gps;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GPS extends AppCompatActivity {
    private Button statusBtn;
    private Button locationBtn;
    private TextView textView;
    private TextView statusView;
    private LocationManager locationManager;
    private LocationListener loc;
    private Button Internet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
        textView = (TextView) findViewById(R.id.txt);
        statusView = (TextView) findViewById(R.id.statustxt);
        statusBtn = (Button) findViewById(R.id.getStatus);
        locationBtn = (Button) findViewById(R.id.getLocation);
        Internet= (Button) findViewById(R.id.getLocatioon_I);
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        loc = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                if (location != null) {
                    showLocation(location);
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
        configureButton();
        getlocation();
        statusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getGPS_status();
            }
        });
//        locationBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                getLocation();
//            }
//        });
    }

    private void getGPS_status() {
        if (locationManager.isProviderEnabled(locationManager.GPS_PROVIDER)) {
            statusView.setText("GPS is activated!");
        } else {
            statusView.setText("GPS is NOT activated!");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                configureButton();
                break;
            default:
                break;
        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void configureButton() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        , 10);
            }
            return;
        }
        // this code won't execute IF permissions are not allowed, because in the line above there is return statement.
        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //noinspection MissingPermission
                locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, loc);

                //Location l = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
                //textView.setText("here");
                //showLocation(l);
            }
        });
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 5, loc);
       // showLocation(locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER));

    }

    private void getlocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        , 10);
            }
            return;
        }
        // this code won't execute IF permissions are not allowed, because in the line above there is return statement.
        Internet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //noinspection MissingPermission
                locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER,0,0,loc);
                //showLocation(l);
            }
        });
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 5, loc);
        // showLocation(locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER));

    }

    private void showLocation(Location location){
        if(location!=null) {
            double longitude = location.getLongitude();
            double altitude = location.getLatitude();
            textView.setText("longtitude: " + Double.toString(longitude) + "\naltitude: " + Double.toString(altitude));
        }
        else{
            textView.setText("norecord");
        }
    }
}
