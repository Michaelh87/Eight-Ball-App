package edu.ggc.michael.eightball;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;

/**
 * Created by Lusticar on 12/3/2017.
 */

public class BackgroundMusic extends Service {
    private static final String TAG = null;
    MediaPlayer player;
    Handler HN = new Handler();
    public IBinder onBind(Intent arg0) {

        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        player = MediaPlayer.create(this, R.raw.music);
        player.setLooping(true); // Set looping
        player.setVolume(100,100);

    }
    public class MusicBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }

    protected void onHandleIntent(Intent intent) {
        //... Register BroadcastReceiver
        registerReceiver(new MusicBroadcastReceiver(), new IntentFilter(
                "com.xx.PAUSE_MUSIC_ACTION"));


    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        player.start();
        return 1;
    }

    public void onStart(Intent intent, int startId) {
        // TO DO
    }
    public IBinder onUnBind(Intent arg0) {
        // TO DO Auto-generated method
        return null;
    }

    public void onStop() {
        player.stop();
    }
    public void onPause() {
        player.pause();

    }
    @Override
    public void onDestroy() {
        player.stop();
        player.release();
    }

    @Override
    public void onLowMemory() {

    }
}
