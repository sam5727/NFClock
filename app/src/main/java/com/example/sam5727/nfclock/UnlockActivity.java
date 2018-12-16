package com.example.sam5727.nfclock;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NdefFormatable;
import android.os.Parcelable;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class UnlockActivity extends AppCompatActivity {

    NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        Vibrator v = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        long[] pattern = {0, 1500, 1000};
        v.vibrate(pattern, 0);
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);;
        int actualVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, actualVolume, AudioManager.ADJUST_SAME);

        MediaPlayer player = new MediaPlayer();
        try {
            player.setAudioStreamType(AudioManager.STREAM_ALARM);
            player.setDataSource(this, notification);
            player.setLooping(true);
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
//
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
//            Toast.makeText(this, "NFC intent", Toast.LENGTH_LONG).show();
            Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (parcelables != null && parcelables.length > 0) {
                readTextFromMessage((NdefMessage) parcelables[0]);
            } else
                Toast.makeText(this, "No NDEF messages found!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        // enable fore ground dispatch
        Intent intent = new Intent(this, UnlockActivity.class);
        intent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        IntentFilter[] intentFilters = new IntentFilter[]{};
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
        super.onResume();
    }

    @Override
    protected void onPause() {
        // disable fore ground dispatch
        nfcAdapter.disableForegroundDispatch(this);
        super.onPause();
    }

    private void readTextFromMessage(NdefMessage ndefMessage) {
        NdefRecord[] ndefRecords = ndefMessage.getRecords();
        if (ndefRecords != null && ndefRecords.length > 0) {
            NdefRecord ndefRecord = ndefRecords[0];
            String tagContent = getTextFromNdefRecord(ndefRecord);
            if (tagContent.equals("sam5727")) {
                Toast.makeText(this, "YES detect", Toast.LENGTH_LONG).show();
            } else
                Toast.makeText(this, "NO detect", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "No NDEF records found", Toast.LENGTH_LONG).show();
        }
    }

    public String getTextFromNdefRecord(NdefRecord ndefRecord) {
        String tagContent = null;
        try {
            byte[] payload = ndefRecord.getPayload();
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageSize = payload[0] & 0063;
            tagContent = new String(payload, languageSize + 1, payload.length - languageSize - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.e("getTextFromNdefRecord", e.getMessage(), e);
        }
        return tagContent;
    }
}
