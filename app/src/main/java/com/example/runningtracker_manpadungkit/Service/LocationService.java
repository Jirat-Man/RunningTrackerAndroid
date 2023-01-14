package com.example.runningtracker_manpadungkit.Service;
import static com.example.runningtracker_manpadungkit.Constants.CHANNEL_ID;
import static com.example.runningtracker_manpadungkit.Constants.INTERVAL_MILLIS;
import static com.example.runningtracker_manpadungkit.Constants.NOTIFICATION_ID;
import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;

import android.os.IBinder;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.example.runningtracker_manpadungkit.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class LocationService extends Service {

    private final IBinder binder = new MyLocalBinder();

    double mDistance = 0;
    String mDuration;
    double mSpeed = 0;
    String mDate;
    double mLongitude = 0;
    double mLatitude = 0;
    double mAltitude = 0;
    double mAvgSpeed = 0;
    int seconds = 0;
    int counter = 0;
    Boolean pauseTime = false;
    Timer mTimer;
    TimerTask mTimerTask;
    double mTime = -1.0;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {

        startTimer();

        Notify();

        LocationRequest locationRequest = new
                LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, INTERVAL_MILLIS).build();

        //called every interval
        //reset location for when tracking is paused
        //get location update every interval
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (!pauseTime) {
                    for (Location location : locationResult.getLocations()) {
                        updateData(location);
                    }
                } else {
                    //reset location for when tracking is paused
                    counter = 0;
                }
            }
        };
        //Google's API for location service
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }


    private void setDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy , HH:mm:ss", Locale.ENGLISH);
        mDate = dateFormat.format(calendar.getTime());
    }

    private void startTimer() {
        mTimer = new Timer();
        mTimerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                setDate();
                if(!pauseTime) {
                    mTime++;
                    mDuration = getTimerText();
                }
            }
        };
        mTimer.scheduleAtFixedRate(mTimerTask, 0 ,1000);
    }


    private String getTimerText()
    {
        int rounded = (int) Math.round(mTime);
        int seconds = ((rounded % 86400) % 3600) % 60;
        int minutes = ((rounded % 86400) % 3600) / 60;
        int hours = ((rounded % 86400) / 3600);

        return formatTime(seconds, minutes, hours);
    }

    private String formatTime(int seconds, int minutes, int hours)
    {
        return String.format(Locale.ENGLISH,"%02d",hours) + " : " + String.format(Locale.ENGLISH,"%02d",minutes) + " : " + String.format(Locale.ENGLISH,"%02d",seconds);
    }


    private void updateData(Location location) {

        //Update all the textView with new location
        if (location != null) {

            mDistance = getDistance(location);
            mLongitude = location.getLongitude();
            mLatitude = location.getLatitude();

            //check if phone has altitude checker function
            if(location.hasAltitude()){
                mAltitude = location.getAltitude();
            }
            // if not put "-404"
            else{
                mAltitude = -404;
            }
            //check if phone has speed checker function
            if(location.hasSpeed()){
                mSpeed = (double) Math.round(location.getSpeed() * 1d);
                mAvgSpeed += (double) Math.round(location.getSpeed() * 1d);
                seconds++;
                Log.d("speed", String.valueOf(mSpeed));
                Log.d("avg", String.valueOf(mAvgSpeed));
                Log.d("seconds", String.valueOf(seconds));
            }
            // if not put "404"
            else{
                mAvgSpeed = -404;
            }
        }
    }

    //Return the distance covered by using Longitude and Latitude using Haversine Formula
    private double getDistance(Location location) {
        counter++;
        if(counter > 1){
            int EARTH_RADIUS = 6371; // Approx Earth radius in KM

            double dLat = Math.toRadians((location.getLatitude() - mLatitude));
            double dLong = Math.toRadians((location.getLongitude() - mLongitude));

            double startLat = Math.toRadians(mLatitude);
            double endLat = Math.toRadians(location.getLatitude());

            double a = haversine(dLat) + Math.cos(startLat) * Math.cos(endLat) * haversine(dLong);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

            mDistance = mDistance + (EARTH_RADIUS * c);
        }
        return (double)Math.round(mDistance * 100d) / 100d; // <-- d
    }

    private double haversine(double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }

    protected void resetLocation(){
        mDistance = 0;
        mDuration = "00 : 00 : 00";
        mSpeed = 0;
        mAvgSpeed = 0;
        seconds = 0;
        mDate = "Date";
        mAltitude = 0;
        mTime = -1;
        counter = 0;
        mLatitude = 0;
        mLongitude = 0;
    }

    public void pauseTracking(){
        this.pauseTime = true;
    }
    public void continueTracking(){
        this.pauseTime = false;
    }


    @Override
    public void onDestroy() {
        resetLocation();
        deleteNotification(getApplicationContext(), NOTIFICATION_ID);
        stopSelf();
        super.onDestroy();
    }

    //create notification channel
    private void Notify() {
        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.running_man)
                .setContentTitle("Tracking in progress")
                .setContentText("you got this, keep running!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(NOTIFICATION_ID,builder.build());
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = getSystemService(NotificationManager.class);

        manager.createNotificationChannel(channel);
    }

    //delete notification
    public static void deleteNotification(Context ctx, int notifyId) {
        NotificationManager manager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notifyId);
    }


    //return service to activity, bind service to activity
    public class MyLocalBinder extends Binder {
        public double getDistance(){
            return LocationService.this.mDistance;
        }
        public String getDuration(){
            return LocationService.this.mDuration;
        }
        public double getSpeed(){
            return (double)Math.round((mSpeed) * 100d)/100d;
        }
        public double getAvgSpeed(){
            return (double)Math.round((mAvgSpeed/seconds) * 100d)/100d;
        }
        public String getDate(){return LocationService.this.mDate;}
        public String getAltitude(){return String.valueOf(LocationService.this.mAltitude);}
        public void pauseTracking(){LocationService.this.pauseTracking();}
        public void continueTracking(){LocationService.this.continueTracking();}
    }

}