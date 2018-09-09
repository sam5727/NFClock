package com.example.sam5727.nfclock;

import java.util.Calendar;

public class ClockOverview {
    private String mTime;
    private int requestCode;
    private Calendar calendar;

    public ClockOverview(String time, int index, Calendar c){
        mTime = time;
        requestCode = index;
        calendar = c;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String mTime) {
        this.mTime = mTime;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }
}
