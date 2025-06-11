package com.iyuba.core.common.event;

public class SleepEvent {
    public boolean isCancel;
    public int minute;

    public SleepEvent(boolean isCancel, int minute) {
        this.isCancel = isCancel;
        this.minute = minute;
    }
}
