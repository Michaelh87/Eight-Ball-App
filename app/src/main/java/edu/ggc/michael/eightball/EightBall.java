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
    private int length;
    private String musicTxt = "Music";


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
                music.release();
                music = null;
                Intent intent = new Intent(EightBall.this, about.class);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        Log.v("Orientation", ""+  message.getText());
        if(music != null) {
            outState.putInt("position", music.getCurrentPosition());
        }
        outState.putString("messageText", (String) message.getText());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        if(music != null) {
            length = savedInstanceState.getInt("position");
        }
        Log.v("Orientation", ""+ savedInstanceState.getString("messageText"));
        Log.v(musicTxt, ""+length);
        music.seekTo(length);
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

        if(event.values[2] < -5){
            if(v.hasVibrator() && !flipped ) {
                v.vibrate(100);
            } else {
                //for flash of camera is there is time
            }
            flipped = true;
        }

        if(flipped == true && event.values[2] > 0){
            flipped = false;
            showPrediction();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    // This whole method basically checks whether the app was paused and pauses the music so it
    // doesnt keep playing the music even when the app closes.
    protected void onPause() {
        super.onPause();
        if(music != null) {
            music.pause();
        }
        Log.v(musicTxt, "onPause");
        senSensorManager.unregisterListener(this);
    }

    protected void onResume() {
        Log.v(musicTxt, "onResume ");
        if(music != null && !music.isPlaying()) {
            music.seekTo(length);
            music.start();
            Log.v(musicTxt, "Should be playing " + length);
        } else if (music == null){
            musicAsync = new MusicAsync();
            musicAsync.doInBackground();
            Log.v(musicTxt, "music is null recreating");
        }
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        Log.v("tag","music should start here.");

        super.onResume();
    }

    class MusicAsync extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            Log.v(musicTxt, "In Async background");
            music = MediaPlayer.create(EightBall.this, R.raw.music);
            music.setLooping(true);
            music.setVolume(1.0f,1.0f);
            music.start();
            return null;
        }
    }

    class SpeakAsyncTask extends AsyncTask<String, String, Void>{

        @Override
        protected Void doInBackground(String... messageTTS) {

            length = music.getCurrentPosition();
            music.pause();
            tts.speak(messageTTS[0], TextToSpeech.QUEUE_FLUSH, null);
            while(tts.isSpeaking()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            music.start();
            return null;
        }
    }

    private void showPrediction(){
        String post = "PostExe";
        Log.v(post, "After doInBackground");

        int random = rand.nextInt(messages.size());
        message.setText(messages.get(random));
        SpeakAsyncTask speakTask = new SpeakAsyncTask();
        speakTask.execute(messages.get(random));
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

}
