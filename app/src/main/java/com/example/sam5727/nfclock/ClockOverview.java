package com.example.sam5727.nfclock;

public class ClockOverview {
    private String mTime;

    public ClockOverview(String time){
        mTime = time;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String mTime) {
        this.mTime = mTime;
    }
}
