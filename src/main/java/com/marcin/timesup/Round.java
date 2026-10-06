package com.marcin.timesup;

public class Round {

    private final int number;
    private final int durationSeconds;
    private final boolean skipAllowed;
    private final String rule;

    public Round(int number, int durationSeconds, boolean skipAllowed, String rule) {
        this.number = number;
        this.durationSeconds = durationSeconds;
        this.skipAllowed = skipAllowed;
        this.rule = rule;
    }

    public int getNumber() {
        return number;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public boolean isSkipAllowed() {
        return skipAllowed;
    }

    public String getRule() {
        return rule;
    }
}
