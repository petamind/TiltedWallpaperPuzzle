package com.tungnd.android.wallpaperpuzzle.utils;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import com.tungnd.android.wallpaperpuzzle.R;

/**
 * @deprecated
 */
public class MusicService extends Service {

    private MediaPlayer mp;
    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    public void onCreate()
    {
        mp = MediaPlayer.create(this, R.raw.sound);
        mp.setLooping(false);
    }
    public void onDestroy()
    {
        mp.stop();
    }
    public void onStart(Intent intent,int startid){

        Log.d(this.getClass().toString(), "On start");
        mp.start();
    }
}
