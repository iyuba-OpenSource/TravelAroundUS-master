package com.iyuba.core.common.util;

public interface OnIJKPlayStateChangedListener {
    void playSuccess();

    void setPlayTime(String currTime, String allTime);

    void playFaild();

    void playCompletion();

    void playResume();

    void playPause();

    void playStop();
}
