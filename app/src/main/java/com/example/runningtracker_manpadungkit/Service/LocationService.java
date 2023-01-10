package com.example.runningtracker_manpadungkit.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class LocationService extends Service {

    public MyLocalBinder localBinder = new MyLocalBinder();

    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    public class MyLocalBinder extends Binder {
        public LocationService getBoundService(){
            return LocationService.this;
        }
    }
}