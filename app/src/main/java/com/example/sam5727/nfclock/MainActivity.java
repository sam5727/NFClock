package com.example.sam5727.nfclock;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences shref;
    public FloatingActionButton fab;
    private SimpleDateFormat dfTime = new SimpleDateFormat("HH:mm", Locale.CHINESE);
    private SimpleDateFormat dfDate = new SimpleDateFormat("M月d日, EEEE", Locale.CHINESE);
    private ArrayList<PendingIntent> intentArray = new ArrayList<PendingIntent>();
    public ArrayList<ClockOverview> clockList = new ArrayList<ClockOverview>();
    private String createMessage;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        calendar = Calendar.getInstance();
        shref = getPreferences(MODE_PRIVATE);
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

        Gson gson = new Gson();
        String response = shref.getString("data", "");
        if (shref.contains("data"))
            clockList = gson.fromJson(response, new TypeToken<List<ClockOverview>>() {
            }.getType());
        else
            clockList = new ArrayList<ClockOverview>();

        final ListView clockView = (ListView) findViewById(R.id.clockView);
        ClockAdapter adapter = new ClockAdapter(this, clockList);
        clockView.setAdapter(adapter);
        clockView.setOnItemClickListener(null);
        registerForContextMenu(clockView);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                calendar = Calendar.getInstance();
                // Use the current time as the default values for the picker
                new TimePickerDialog(MainActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker pickerView, int hourOfDay, int minute) {
                        int requestCode = hourOfDay * 100 + minute;
                        calendar = Calendar.getInstance();
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                        Long currentTime = calendar.getTimeInMillis();
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);

                        Long differ = calendar.getTimeInMillis() - currentTime;
                        if (differ <= 0) {
                            differ += 86400000;
                            calendar.add(Calendar.DATE, 1);
                        }

                        clockList.add(new ClockOverview(dfTime.format(calendar.getTime()), requestCode, calendar, true));
                        ClockAdapter adapter = new ClockAdapter(MainActivity.this, clockList);
                        clockView.setAdapter(adapter);

                        SharedPreferences.Editor editor = shref.edit();
                        Gson gson = new Gson();
                        String json = gson.toJson(clockList);
                        editor.putString("data", json);
                        editor.commit();

                        createMessage = String.format(Locale.CHINESE, "%d hour, %d min",
                                TimeUnit.MILLISECONDS.toHours(differ),
                                TimeUnit.MILLISECONDS.toMinutes(differ) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(differ))
                        );

                        Snackbar.make(view, createMessage, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                        Log.e("ttt", calendar.getTimeInMillis() + "");
                        // Set broadcast
                        Intent intent = new Intent(MainActivity.this, Alarm.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, requestCode, intent, 0);
                        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                        intentArray.add(pendingIntent);
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.clockView) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_list, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        ClockAdapter adapter = new ClockAdapter(this, clockList);
        ClockOverview clockOverview = adapter.getItem(info.position);
        switch(item.getItemId()) {
            case R.id.edit:
                // edit stuff here
                Toast.makeText(MainActivity.this, clockOverview.getTime(), Toast.LENGTH_LONG).show();
                return true;
            case R.id.delete:
                if (clockOverview.isChecked()){
                    Intent intent = new Intent(MainActivity.this, Alarm.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, clockOverview.getRequestCode(), intent, 0);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(MainActivity.ALARM_SERVICE);
                    alarmManager.cancel(pendingIntent);
                }
                clockList.remove(info.position);
                adapter = new ClockAdapter(this, clockList);
                ListView clockView = (ListView) findViewById(R.id.clockView);
                clockView.setAdapter(adapter);

                SharedPreferences.Editor editor = shref.edit();
                Gson gson = new Gson();
                String json = gson.toJson(clockList);
                editor.putString("data", json);
                editor.commit();

                return true;
            default:
                return super.onContextItemSelected(item);
        }
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
        if (id == R.id.action_delete_all) {
            for (ClockOverview co : clockList) {
                if (co.isChecked()){
                    Intent intent = new Intent(MainActivity.this, Alarm.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, co.getRequestCode(), intent, 0);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(MainActivity.ALARM_SERVICE);
                    alarmManager.cancel(pendingIntent);
                }
            }

            ListView clockView = (ListView) findViewById(R.id.clockView);
            clockList = new ArrayList<ClockOverview>();
            ClockAdapter adapter = new ClockAdapter(this, clockList);
            clockView.setAdapter(adapter);

            SharedPreferences.Editor editor = shref.edit();
            Gson gson = new Gson();
            String json = gson.toJson(new ArrayList<ClockOverview>());
            editor.putString("data", json);
            editor.commit();
            return true;
        } else if (id == R.id.action_settings){

        }

        return super.onOptionsItemSelected(item);
    }

}
