package com.dkozhevnikov.gomeotick;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = TicksService.class.getSimpleName();

    private TicksService timerService;
    private boolean serviceBound;

    private Button leftBtn;
    private Button rightBtn;
    private TextView currentTime;
    private TextView laps;
    private TextView startTime;
    private TextView endTime;

    // Handler to update the UI every second when the timer is running
    private final Handler mUpdateTimeHandler = new UIUpdateHandler(this);

    // Message type for the handler
    private final static int MSG_UPDATE_TIME = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        leftBtn = findViewById(R.id.leftBtn);
        rightBtn = findViewById(R.id.rightBtn);
        currentTime = findViewById(R.id.currentTime);
        laps = findViewById(R.id.laps);
        startTime = findViewById(R.id.startTime);
        endTime = findViewById(R.id.endTime);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "Starting and binding service");
        }
        Intent i = new Intent(this, TicksService.class);
        startService(i);
        bindService(i, mConnection, 0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (serviceBound) {
            // If a timer is active, foreground the service, otherwise kill the service
            if (timerService.getAppState().getTickingStatus() != TickingStatus.Inactive) {
                timerService.foreground();
            }
            else {
                stopService(new Intent(this, TicksService.class));
            }
            // Unbind the service
            unbindService(mConnection);
            serviceBound = false;
        }
    }

    public void onLeftButtonClick(View v) {
        if(serviceBound){
            AppState state = timerService.getAppState();
            TickingStatus status = state.getTickingStatus();

            if(status == TickingStatus.Inactive){
                Log("Start");
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
                timerService.start();
            }

            if(status == TickingStatus.Ticking){
                Log("Skip lap");
                timerService.skip();
            }

            if(status == TickingStatus.Pause){
                Log("Next lap");
                timerService.nextLap();
            }
            updateUI();
        }
    }

    public void onRightButtonClick(View v) {
        if(serviceBound){
            Log("Start");
            timerService.cancel();
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            updateUI();
        }
    }

    private void Log(String msg){
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, msg);
        }
    }



    private void updateUI(){
        AppState state = timerService.getAppState();
        TickingStatus status = state.getTickingStatus();
        rightBtn.setText("Cancel");
        if(status == TickingStatus.Inactive){
            leftBtn.setText("Start");
            currentTime.setText("00:00");
            currentTime.setEnabled(false);
            laps.setText("0/0");
            laps.setEnabled(false);
            startTime.setText("00:00:00");
            startTime.setEnabled(false);
            endTime.setText("00:00:00");
            endTime.setEnabled(false);
        }

        if(status == TickingStatus.Ticking){
            leftBtn.setText("Skip");
            currentTime.setText(getCurrentTime(state));
            currentTime.setEnabled(true);
            laps.setText(state.getCurrentLap()+"/"+state.getLapCount());
            laps.setEnabled(true);
            startTime.setText(getBoundTime(state.getStartTime()));
            startTime.setEnabled(true);
            endTime.setText(getBoundTime(state.getEndTime()));
            endTime.setEnabled(true);
        }

        if(status == TickingStatus.Pause){
            leftBtn.setText("Continue");
            currentTime.setText("00:00");
            currentTime.setEnabled(true);
            laps.setText(state.getCurrentLap()+"/"+state.getLapCount());
            currentTime.setEnabled(true);
            startTime.setText(getBoundTime(state.getStartTime()));
            startTime.setEnabled(true);
            endTime.setText(getBoundTime(state.getEndTime()));
            endTime.setEnabled(true);
        }
    }

    private String getCurrentTime(AppState state){
        long min = state.getLapTime() / 1000 / 60;
        long sec = (state.getLapTime() - min*1000*60) / 1000;

        return String.format("%02d", min) + ":" + String.format("%02d", sec) ;
    }

    private String getBoundTime(long currentTime){
        return getStringTime(currentTime, "HH:mm:ss");
    }

    private String getStringTime(long time, String pattern){
        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getDefault());
        calendar.setTimeInMillis(time);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(calendar.getTime());
    }


    /**
     * Callback for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "Service bound");
            }
            TicksService.RunServiceBinder binder = (TicksService.RunServiceBinder) service;
            timerService = binder.getService();
            serviceBound = true;
            // Ensure the service is not in the foreground when bound
            timerService.background();
            // Update the UI if the service is already running the timer
            updateUI();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "Service disconnect");
            }
            serviceBound = false;
        }
    };

    /**
     * When the timer is running, use this handler to update
     * the UI every second to show timer progress
     */
    static class UIUpdateHandler extends Handler {

        private final static int UPDATE_RATE_MS = 1000;
        private final WeakReference<MainActivity> activity;

        UIUpdateHandler(MainActivity activity) {
            this.activity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message message) {
            if (MSG_UPDATE_TIME == message.what) {
                if (Log.isLoggable(TAG, Log.VERBOSE)) {
                    Log.v(TAG, "updating time");
                }
                activity.get().updateUI();
                sendEmptyMessageDelayed(MSG_UPDATE_TIME, UPDATE_RATE_MS);
            }
        }

    }
}
