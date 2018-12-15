package com.example.sam5727.nfclock;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ClockAdapter extends ArrayAdapter<ClockOverview> {

    private Activity activity;

    public ClockAdapter(Activity context, ArrayList<ClockOverview> cases) {
        super(context, 0, cases);
        activity = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.clock_item, parent, false);
        }

        final ClockOverview currentClockOverview = getItem(position);

        TextView clockTime = (TextView) listItemView.findViewById(R.id.clockTime);
        clockTime.setText(currentClockOverview.getTime());

        TextView caseValue = (TextView) listItemView.findViewById(R.id.clockDay);
        caseValue.setText("今天");

        Switch clockSwitch = (Switch) listItemView.findViewById(R.id.clockSwitch);
        clockSwitch.setOnCheckedChangeListener(null);
        clockSwitch.setChecked(currentClockOverview.isChecked());
        clockSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                currentClockOverview.setChecked(isChecked);
                SharedPreferences shref = activity.getPreferences(MainActivity.MODE_PRIVATE);
                SharedPreferences.Editor editor = shref.edit();
                Gson gson = new Gson();
                String json = gson.toJson(((MainActivity) activity).clockList);
                editor.putString("data", json);
                editor.commit();

                int requestCode = currentClockOverview.getRequestCode();
                Intent intent = new Intent(activity, Alarm.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, requestCode, intent, 0);
                AlarmManager alarmManager = (AlarmManager) activity.getSystemService(MainActivity.ALARM_SERVICE);
                if (isChecked) {
                    Calendar tmp = Calendar.getInstance();
                    tmp.set(Calendar.SECOND, 0);
                    tmp.set(Calendar.MILLISECOND, 0);
                    Long differ = currentClockOverview.getCalendar().getTimeInMillis() - tmp.getTimeInMillis();
                    if (differ <= 0) {
                        differ += 86400000;
                        tmp = currentClockOverview.getCalendar();
                        tmp.add(Calendar.HOUR_OF_DAY, 1);
                        currentClockOverview.setCalendar(tmp);
                    }

                    String createMessage = String.format(Locale.CHINESE, "%d hour, %d min",
                            TimeUnit.MILLISECONDS.toHours(differ),
                            TimeUnit.MILLISECONDS.toMinutes(differ) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(differ))
                    );
                    alarmManager.set(AlarmManager.RTC_WAKEUP, currentClockOverview.getCalendar().getTimeInMillis(), pendingIntent);
                    Snackbar.make(((MainActivity) activity).fab, createMessage, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else
                    alarmManager.cancel(pendingIntent);
            }
        });

        LinearLayout clockLayout = (LinearLayout) listItemView.findViewById(R.id.clockLayout);
        clockLayout.setOnLongClickListener(null);


        return listItemView;
    }
}
