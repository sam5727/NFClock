package com.example.sam5727.nfclock;

public class ClockOverview {
    private String mTime;
    private int requestCode;

    public ClockOverview(String time, int index){
        mTime = time;
        requestCode = index;
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
}
