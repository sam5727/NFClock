package com.example.sam5727.nfclock;

import android.app.AlarmManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

public class Alarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "ALARM", Toast.LENGTH_LONG).show();

        Vibrator v = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        v.vibrate(5000);
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);;
        int actualVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, actualVolume, AudioManager.ADJUST_SAME);

        MediaPlayer player = new MediaPlayer();
        try {
            player.setAudioStreamType(AudioManager.STREAM_ALARM);
            player.setDataSource(context, notification);
            player.setLooping(true);
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
