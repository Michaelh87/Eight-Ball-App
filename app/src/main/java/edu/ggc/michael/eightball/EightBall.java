package edu.ggc.michael.eightball;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.hardware.SensorEventListener;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class EightBall extends AppCompatActivity implements SensorEventListener {

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private int count = 0;
    private boolean flipped = false;
    private ArrayList<String> messages = new ArrayList<>();
    private TextView message;
    private Random rand;
    private Vibrator v;
    private TextToSpeech tts;
    private Intent svc;
    public static MediaPlayer music;
    private MusicAsync musicAsync;
    int length = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eight_ball);
        message = (TextView) findViewById(R.id.message);
        rand = new Random();
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        setTTS();

        musicAsync = new MusicAsync();
        musicAsync.doInBackground();

        setMessages();

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.aboutBTN);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("fab", "in the on click for the fab");
                Intent intent = new Intent(EightBall.this, about.class);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        Log.v("Orientation", ""+  message.getText());
        outState.putInt("position", music.getCurrentPosition());
        outState.putString("messageText", (String) message.getText());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        int pos = savedInstanceState.getInt("position");
        Log.v("Orientation", ""+ savedInstanceState.getString("messageText"));
        music.seekTo(pos);
        message.setText(savedInstanceState.getString("messageText"));
        super.onRestoreInstanceState(savedInstanceState);

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
            if(v.hasVibrator() && !flipped ) {
                v.vibrate(100);
            } else {
                //for flash of camera is there is time
            }
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
    // This whole method basically checks whether the app was paused and pauses the music so it
    // doesnt keep playing the music even when the app closes.
    protected void onPause() {
        if (this.isFinishing()){
            music.stop();
        }
        Context context = getApplicationContext();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        if (!taskInfo.isEmpty()) {
            ComponentName topActivity = taskInfo.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                music.stop();
                length = music.getCurrentPosition();

            }

        }
        super.onPause();
        senSensorManager.unregisterListener(this);
    }

    protected void onResume() {

        super.onResume();
        if(music != null && !music.isPlaying()) {
            music.seekTo(length);
            music.start();
        }
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        Log.v("tag","music should start here.");


    }
    class MusicAsync extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            music = MediaPlayer.create(EightBall.this, R.raw.music);
            music.setLooping(true);
            music.setVolume(1.0f,1.0f);
            music.start();
            return null;
        }
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

            int random = rand.nextInt(messages.size());
            message.setText(messages.get(random));
            speak(messages.get(random));
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
        messages.add("Better to not tell you");
        messages.add("Cannot predict now");
        messages.add("Concentrate and ask again");
        messages.add("Don't count on it");
        messages.add("My reply is no");
        messages.add("My sources say no");
        messages.add("Outlook not so good");
        messages.add("Very doubtful");
    }

    private void setTTS(){
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                tts.setLanguage(Locale.US);
            }
        });
    }

    private void speak(String sp){
        tts.speak(sp, TextToSpeech.QUEUE_FLUSH, null);
    }


}
