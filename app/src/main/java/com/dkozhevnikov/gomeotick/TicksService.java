package com.dkozhevnikov.gomeotick;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class TicksService extends Service {
    public TicksService() {
    }

    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        TicksService getService() {
            return TicksService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
