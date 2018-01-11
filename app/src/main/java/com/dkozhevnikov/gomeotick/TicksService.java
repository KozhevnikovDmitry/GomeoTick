package com.dkozhevnikov.gomeotick;

import android.app.IntentService;
import android.content.Intent;

public class TicksService extends IntentService {

    public TicksService() {
        super("TicksService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Normally we would do some work here, like download a file.
        // For our sample, we just sleep for 5 seconds.
        long endTime = System.currentTimeMillis() + 5*1000;
        while (System.currentTimeMillis() < endTime) {
            synchronized (this) {
                try {
                    wait(endTime - System.currentTimeMillis());
                } catch (Exception e) {
                }
            }
        }
    }
}
