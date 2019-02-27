package com.example.user.lightsensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class LightSensor extends AppCompatActivity {

    private TextView mTextViewLightLable;
    private SensorManager mSensorManager;
    private SensorEventListener mEventListenerLight;
    private float lastLight;

    private void updateUI(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTextViewLightLable.setText(" " + lastLight);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_sensor);
        mTextViewLightLable = (TextView)findViewById(R.id.intensitytxt);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mEventListenerLight = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                float[] values = sensorEvent.values;
                lastLight = values[0];
                updateUI();
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }
    @Override
    public void onResume(){
        super.onResume();
        mSensorManager.registerListener(mEventListenerLight, mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onStop(){
        mSensorManager.unregisterListener(mEventListenerLight);
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_light_sensor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
