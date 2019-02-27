package com.example.user.gyroscope;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class gyroscope extends AppCompatActivity {
    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private TextView txt1;
    private TextView rotationTxt;
    private TextView test;
    private Button button;
    private SensorEventListener gyroscopeEventListener;
    private float[] gyroscopeVal = new float[3];
    private double direction;
    private static final float NS2S = 1.0f/1000000000.0f;
    private float timestamp;
    private final float[] deltaRotationVector = new float[4];
    private float rotation = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gyroscope);
        txt1 = (TextView)findViewById(R.id.gyroscope1);
        rotationTxt = (TextView) findViewById(R.id.rotation);
        test = (TextView)findViewById(R.id.test);
        setOutput();
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rotation = 0;
                setOutput();
            }
        });
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (gyroscopeSensor == null){
            Toast.makeText(this, "No Gyroscope Sensor avaliable!",Toast.LENGTH_LONG).show();
            finish();
        }
        gyroscopeEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                gyroscopeVal = sensorEvent.values;
                final float dT = (sensorEvent.timestamp - timestamp) * NS2S;
                float axisZ= gyroscopeVal[2] *dT;
                timestamp = sensorEvent.timestamp;
                if (Math.abs(axisZ* 180/Math.PI)>0.3){
                    rotation+= axisZ* 180/Math.PI;
                    rotation =rotation%360;
                    setOutput();
                }

//                if (timestamp != 0){
//                    final float dT = (sensorEvent.timestamp - timestamp) * NS2S;
//                    float axisX = sensorEvent.values[0];
//                    float axisY = sensorEvent.values[1];
//                    float axisZ = sensorEvent.values[2];
//
//                    float omegaMagnitude = (float) Math.sqrt(axisX*axisX + axisY+axisY + axisZ*axisZ);
//
//                    if (omegaMagnitude > 0.0001){
//                        axisX /= omegaMagnitude;
//                        axisY /= omegaMagnitude;
//                        axisZ /= omegaMagnitude;
//                    }
//
//                    float thetaOverTwo = omegaMagnitude*dT/2.0f;
//                    float sinThetaOverTwo = (float)Math.sin(thetaOverTwo);
//                    float cosThetaOverTwo = (float)Math.cos(thetaOverTwo);
//                    deltaRotationVector[0] = sinThetaOverTwo * axisX;
//                    deltaRotationVector[1] = sinThetaOverTwo * axisY;
//                    deltaRotationVector[2] = sinThetaOverTwo * axisZ;
//                    deltaRotationVector[3] = cosThetaOverTwo;
//                }
//                timestamp = sensorEvent.timestamp;
//                float[] deltaRotationMatrix = new float[9];
//                float[] adjustedRotationMatrix = new float[9];
//                float[] orientation = new float[3];
//                SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
//                SensorManager.remapCoordinateSystem(deltaRotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Z, adjustedRotationMatrix);
//                SensorManager.getOrientation(adjustedRotationMatrix, orientation);
//                if (!Float.isNaN(orientation[0])){
//                    rotation += (float)(orientation[0]* 180/Math.PI);
//                    rotation= rotation%360;
//                }
                //test.setText(Float.toString(orientation[0]));
                //rotation += (float) (Math.acos(deltaRotationMatrix[1]) * 180/Math.PI);

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        Thread readThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    readGyroscope();
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

    private void setOutput(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rotationTxt.setText("Rotation: " + rotation);
            }
        });
    }

    private void readGyroscope(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txt1.setText("X: " + gyroscopeVal[0] + " Y: " + gyroscopeVal[1] + " Z: " + gyroscopeVal[2]);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(gyroscopeEventListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(gyroscopeEventListener);
    }
}
