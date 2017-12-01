package edu.ggc.michael.eightball;

import android.content.Context;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.hardware.SensorEventListener;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.util.Log;

public class EightBall extends AppCompatActivity implements SensorEventListener {

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private int count = 0;
    private boolean flipped = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eight_ball);

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        String sc = "Sensor changed";

        //to see when flipped in development
        if(count %10 == 0) {
            Log.v(sc, "X " + event.values[0]);
            Log.v(sc, "Y " + event.values[1]);
            Log.v(sc, "Z " + event.values[2]);
        }
        count++;

        if(event.values[2] < -2.5){
            flipped = true;
        }

        if(flipped == true && event.values[2] > 0){
            flipped = false;
            ShowPrediction task = new ShowPrediction();
            task.execute();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }

    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    class ShowPrediction extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            String post = "PostExe";
            Log.v(post, "After doInBackground");
        }
    }
}
