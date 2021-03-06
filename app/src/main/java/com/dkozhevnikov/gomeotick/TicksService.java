package com.dkozhevnikov.gomeotick;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class TicksService extends Service {

    private static final String TAG = TicksService.class.getSimpleName();

    private static final int LapDurationMillis = 5 * 60 * 1000;

    private static final int LapCount = 6;

    private long startTime, currentLapStartTime;

    private TickingStatus tickingStatus;

    private int currentLap;

    private int currentLapCount;

    private Timer timer;

    private Ringtone lapRigntone;

    private Vibrator vibrator;

    public void start(){
        startTime = currentLapStartTime = System.currentTimeMillis();
        tickingStatus = TickingStatus.Ticking;
        currentLap = 1;
        currentLapCount = LapCount;
        ScheduleNextLap();
    }

    public void cancel(){
        startTime = currentLapStartTime = 0;
        currentLap = 0;
        tickingStatus = TickingStatus.Inactive;
        currentLapCount = 0;
        CancelTimer();
    }

    public void skip(){
        currentLapCount--;
        if(currentLapCount < currentLap || currentLapCount == 0) {
            cancel();
        }
        else {
            nextLap();
        }
    }

    public void nextLap(){
        lapRigntone.stop();
        vibrator.cancel();
        tickingStatus = TickingStatus.Ticking;
        currentLapStartTime = System.currentTimeMillis();
        currentLap++;

        if(currentLap > currentLapCount){
            cancel();
            return;
        }

        ScheduleNextLap();
    }

    public void pause(){
        lapRigntone.play();
        long[] pattern = { 0, 200, 0 }; //0 to start now, 200 to vibrate 200 ms, 0 to sleep for 0 ms.
        vibrator.vibrate(pattern, 0);
        tickingStatus = TickingStatus.Pause;
        currentLapStartTime = 0;
        CancelTimer();
    }

    private void CancelTimer(){
        if(timer != null) {
            timer.cancel();
        }
    }

    private void ScheduleNextLap(){
        CancelTimer();
        timer = new Timer();
        timer.schedule(new PauseTask(this), LapDurationMillis);
    }

    public AppState getAppState() {
        long endTime = 0;
        long lapTime = 0;

        if(tickingStatus == TickingStatus.Ticking){
            lapTime = System.currentTimeMillis() - currentLapStartTime;
            endTime = currentLapStartTime + (LapCount - currentLap + 1) * LapDurationMillis;
        }

        if(tickingStatus == TickingStatus.Pause){
            long restTime =  (LapCount - currentLap) * LapDurationMillis;
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
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        lapRigntone = RingtoneManager.getRingtone(getApplicationContext(), notification);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
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

class PauseTask extends TimerTask {

    private final TicksService ticksService;

    PauseTask(TicksService ticksService){
        this.ticksService = ticksService;
    }

    @Override
    public void run() {
        ticksService.pause();
    }
}
