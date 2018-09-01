package com.example.sam5727.nfclock;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private SimpleDateFormat dfTime = new SimpleDateFormat("HH:mm", Locale.CHINESE);
    private SimpleDateFormat dfDate = new SimpleDateFormat("M月d日, EEEE", Locale.CHINESE);
    private ArrayList<PendingIntent> intentArray = new ArrayList<PendingIntent>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Date currentTime = Calendar.getInstance().getTime();
        final TextView titleDate = (TextView) findViewById(R.id.titleDate);
        final TextView titleTime = (TextView) findViewById(R.id.titleTime);
        titleDate.setText(dfDate.format(currentTime));
        titleTime.setText(dfTime.format(currentTime));

        final Handler tikHandler = new Handler(getMainLooper());
        tikHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                titleTime.setText(dfTime.format(new Date()));
                tikHandler.postDelayed(this, 1000);
            }
        }, 10);

        final ArrayList<ClockOverview> clockList = new ArrayList<ClockOverview>();
        clockList.add(new ClockOverview(dfTime.format(new Date())));

        final ListView clockView = (ListView) findViewById(R.id.clockView);
        ClockAdapter adapter = new ClockAdapter(this, clockList);
        clockView.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Use the current time as the default values for the picker
                final Calendar calendar = Calendar.getInstance();
                new TimePickerDialog(MainActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK, new TimePickerDialog.OnTimeSetListener(){
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        clockList.add(new ClockOverview(dfTime.format(calendar.getTime())));
                        ClockAdapter adapter = new ClockAdapter(MainActivity.this, clockList);
                        clockView.setAdapter(adapter);

                        // Set broadcast
                        Intent intent = new Intent(MainActivity.this, Alarm.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
                        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + intentArray.size() * 2000, pendingIntent);

                        intentArray.add(pendingIntent);
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
