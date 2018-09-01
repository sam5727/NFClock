package com.example.sam5727.nfclock;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ClockAdapter extends ArrayAdapter<ClockOverview> {

    public ClockAdapter(Activity context, ArrayList<ClockOverview> cases) {
        super(context, 0, cases);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.clock_item, parent, false);
        }

        ClockOverview currentClockOverview = getItem(position);

        TextView clockTime = (TextView) listItemView.findViewById(R.id.clockTime);
        clockTime.setText(currentClockOverview.getTime());

        TextView caseValue = (TextView) listItemView.findViewById(R.id.clockDay);
        caseValue.setText("今天");

        return listItemView;
    }

}
