package com.dkozhevnikov.gomeotick;

public class AppState {

    private final TickingStatus tickingStatus;
    private final long startDate;
    private final long endTime;
    private final long lapTime;
    private final int currentLap;
    private final int lapCount;

    AppState(TickingStatus tickingStatus, long startDate, long endTime, long lapTime, int currentLap, int lapCount){
        this.tickingStatus = tickingStatus;
        this.startDate = startDate;
        this.endTime = endTime;
        this.lapTime = lapTime;
        this.currentLap = currentLap;
        this.lapCount = lapCount;
    }

    public TickingStatus getTickingStatus() {
        return tickingStatus;
    }

    public long getStartTime() {
        return startDate;
    }

    public long getEndTime() {
        return endTime;
    }

    public int getCurrentLap() {
        return currentLap;
    }

    public int getLapCount() {
        return lapCount;
    }

    public long getLapTime() {
        return lapTime;
    }
}
