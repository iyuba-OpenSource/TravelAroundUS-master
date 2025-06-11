package com.iyuba.core.common.util;

public class PlayerNextEvent {

    public String  sentence;
    public String  sentenceCn;
    public int position;
    public int artPosition;

    public PlayerNextEvent(String sentence, String sentenceCn, int position, int artPosition) {
        this.sentence = "Episode  "+sentence;
        this.sentenceCn = sentenceCn;
        this.position=position;
        this.artPosition=artPosition;
    }

    @Override
    public String toString() {
        return "PlayerNextEvent{" +
                "sentence='" + sentence + '\'' +
                ", sentenceCn='" + sentenceCn + '\'' +
                ", position=" + position +
                ", artPosition=" + artPosition +
                '}';
    }
}
