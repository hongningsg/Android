package com.example.user.magnetometer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class magnetometer extends AppCompatActivity {
    private SensorManager sensorManager;
    private Sensor sensor;
    private SensorEventListener sensorEventListener;
    private TextView textView;
    private TextView Heading;
    private float[] myVals = new float[3];
    private double direction = 0;
    private double trueNorth = 0;
    GeomagneticField geoField;
    private LocationManager locationManager;
    private LocationListener loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magnetometer);
        textView = (TextView)findViewById(R.id.txt1);
        Heading = (TextView)findViewById(R.id.txt2);
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        loc = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                geoField = new GeomagneticField(
                        Double.valueOf(location.getLatitude()).floatValue(),
                        Double.valueOf(location.getLongitude()).floatValue(),
                        Double.valueOf(location.getAltitude()).floatValue(),
                        SystemClock.currentThreadTimeMillis()
                );
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if (sensor == null){
            Toast.makeText(this, "No Magnetic field Sensor avaliable!",Toast.LENGTH_LONG).show();
            finish();
        }
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                myVals = sensorEvent.values;
                float x = myVals[0];
                float y = myVals[1];
                if (x > 0){
                    direction = 270 + (Math.atan(y/x))*180/Math.PI;
                }else if (x < 0){
                    direction = 90 + (Math.atan(y/x))*180/Math.PI;
                }else {
                    direction = y > 0 ? 0 : 180;
                }
                if (ActivityCompat.checkSelfPermission(magnetometer.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(magnetometer.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                                , 10);
                    }
                    return;
                }
                locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, loc);
                if (geoField != null) {
                    trueNorth = direction + geoField.getDeclination();
                }
                else
                    trueNorth = direction + 10;
                if (trueNorth > 360){
                    trueNorth -= 360;
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        Thread readThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    readVals();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        readThread.start();
    }

    private void readVals(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText("X: " + myVals[0] + " Y: " + myVals[1] + " Z: " + myVals[2]);
                Heading.setText("Heading: " + direction + "\nTrue North Heading: " + trueNorth);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);
    }
}
