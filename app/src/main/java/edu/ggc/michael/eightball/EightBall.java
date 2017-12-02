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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class EightBall extends AppCompatActivity implements SensorEventListener {

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private int count = 0;
    private boolean flipped = false;
    private ArrayList<String> messages = new ArrayList<>();
    private TextView message;
    private Random rand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eight_ball);
        message = (TextView) findViewById(R.id.message);
        rand = new Random();
        setMessages();

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

            message.setText(messages.get(rand.nextInt(messages.size())));
        }
    }

    private void setMessages(){
        messages.add("It is certain");
        messages.add("It is decidedly so");
        messages.add("Without a doubt");
        messages.add("Yes definitely");
        messages.add("You may rely on it");
        messages.add("As I see it, yes");
        messages.add("Most likely");
        messages.add("Outlook good");
        messages.add("Yes");
        messages.add("Signs point to yes");
        messages.add("Reply hazy try again");
        messages.add("Ask again later");
        messages.add("Better not tell you now");
        messages.add("Cannot predict now");
        messages.add("Concentrate and ask again");
        messages.add("Don't count on it");
        messages.add("My reply is no");
        messages.add("My sources say no");
        messages.add("Outlook not so good");
        messages.add("Very doubtful");
    }
}
