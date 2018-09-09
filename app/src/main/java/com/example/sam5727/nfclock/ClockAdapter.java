package com.example.sam5727.nfclock;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

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
        clockSwitch.setChecked(true);
        clockSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int requestCode = currentClockOverview.getRequestCode();
                if (isChecked) {

                } else {
                    Intent intent = new Intent(activity, Alarm.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, requestCode, intent, 0);
                    AlarmManager alarmManager = (AlarmManager) activity.getSystemService(MainActivity.ALARM_SERVICE);
                    alarmManager.cancel(pendingIntent);
                }
            }
        });

        LinearLayout clockLayout = (LinearLayout) listItemView.findViewById(R.id.clockLayout);
        clockLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return listItemView;
    }

}
