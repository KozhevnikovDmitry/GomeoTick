package com.dkozhevnikov.gomeotick;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class TicksService extends Service {

    private static final String TAG = TicksService.class.getSimpleName();

    private static final int LapDurationMillis = 10 * 1000; //5 * 60 * 1000;

    private static final int LapCount = 6;

    private long startTime, currentLapStartTime;

    private TickingStatus tickingStatus;

    private int currentLap;

    private int currentLapCount;

    public void start(){
        startTime = currentLapStartTime = System.currentTimeMillis();
        tickingStatus = TickingStatus.Ticking;
        currentLap = 1;
        currentLapCount = LapCount;
    }

    public void cancel(){
        startTime = currentLapStartTime = 0;
        currentLap = 0;
        tickingStatus = TickingStatus.Inactive;
        currentLapCount = 0;
    }

    public void skip(){
        currentLapCount--;
        if(currentLapCount == 0) {
            cancel();
        }
        else {
            nextLap();
        }
    }

    public void nextLap(){
        tickingStatus = TickingStatus.Ticking;
        currentLapStartTime = System.currentTimeMillis();
        currentLap++;
    }

    public void pause(){
        tickingStatus = TickingStatus.Pause;
        currentLapStartTime =0;
        currentLap++;
    }

    public AppState getAppState() {
        long endTime = 0;
        long lapTime = 0;

        if(tickingStatus == TickingStatus.Ticking){
            lapTime = System.currentTimeMillis() - currentLapStartTime;
            if(lapTime >= LapDurationMillis){
                pause();
                return getAppState();
            }

            endTime = currentLapStartTime + (LapCount - currentLap + 1) * LapDurationMillis;
        }

        if(tickingStatus == TickingStatus.Pause){
            long restTime =  (LapCount - currentLap + 1) * LapDurationMillis;
            endTime = System.currentTimeMillis() + restTime;
        }

        return new AppState(tickingStatus, startTime, endTime, lapTime, currentLap, currentLapCount);
    }










    // Foreground notification id
    private static final int NOTIFICATION_ID = 1;

    // Service binder
    private final IBinder serviceBinder = new RunServiceBinder();

    public class RunServiceBinder extends Binder {
        TicksService getService() {
            return TicksService.this;
        }
    }

    @Override
    public void onCreate() {
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "Creating service");
        }
        startTime = 0;
        tickingStatus = TickingStatus.Inactive;
        currentLap = 0;
        currentLapCount = 0;
        currentLapStartTime = 0;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "Starting service");
        }
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "Binding service");
        }
        return serviceBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "Destroying service");
        }
    }


    /**
     * Place the service into the foreground
     */
    public void foreground() {
        startForeground(NOTIFICATION_ID, createNotification());
    }

    /**
     * Return the service to the background
     */
    public void background() {
        stopForeground(true);
    }

    /**
     * Creates a notification for placing the service into the foreground
     *
     * @return a notification for interacting with the service when in the foreground
     */
    private Notification createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle("Timer Active")
                .setContentText("Tap to return to the timer")
                .setSmallIcon(R.mipmap.ic_launcher);

        Intent resultIntent = new Intent(this, TicksService.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(this, 0, resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);

        return builder.build();
    }
}
