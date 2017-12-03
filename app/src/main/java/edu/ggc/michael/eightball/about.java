package edu.ggc.michael.eightball;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

public class about extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);


    }
    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        outState.putInt("position", EightBall.music.getCurrentPosition());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        int pos = savedInstanceState.getInt("position");
        Log.v("Orientation", ""+ savedInstanceState.getString("messageText"));
        EightBall.music.seekTo(pos);
        super.onRestoreInstanceState(savedInstanceState);

    }
    protected void onPause() {

        if (this.isFinishing()){
            EightBall.music.stop();
        }
        Context context = getApplicationContext();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        if (!taskInfo.isEmpty()) {
            ComponentName topActivity = taskInfo.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                EightBall.music.stop();
            }else{
                EightBall.music.start();
            }

        }
        super.onPause();
    }
}
