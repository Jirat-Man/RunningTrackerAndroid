package com.example.runningtracker_manpadungkit.Service;

import static com.example.runningtracker_manpadungkit.Ui.RecordRunActivity.INTERVAL_MILLIS;

import static org.chromium.base.ThreadUtils.runOnUiThread;
import static java.lang.String.valueOf;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.runningtracker_manpadungkit.Ui.RecordRunActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;

public class LocationService extends Service {

    //private MyLocalBinder localBinder = new MyLocalBinder();

    private IBinder binder = new MyLocalBinder();

    //Google's API for location service
    private FusedLocationProviderClient fusedLocationClient;

    //config file for all settings of FusedLocationProviderClient
    private LocationRequest locationRequest;

    //get location update every interval
    private LocationCallback locationCallback;

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

    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String date;
    Timer mTimer;
    TimerTask mTimerTask;
    double mTime = 0.0;

    private Handler handler;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {

        startTimer();

        setDate();

        LocationRequest locationRequest = new
                LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, INTERVAL_MILLIS).build();

        //called every interval
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                for (Location location : locationResult.getLocations()) {
                    updateData(location);
                }
            }
        };
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
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
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("MM-dd-yyyy , HH:mm:ss");
        date = dateFormat.format(calendar.getTime());
        mDate = date;
    }

    private void startTimer() {
        mTimer = new Timer();
        mTimerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                mTime++;
                mDuration = getTimerText();
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
        return String.format("%02d",hours) + " : " + String.format("%02d",minutes) + " : " + String.format("%02d",seconds);
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
            // if not put "Not Available"
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
            // if not put "Not Available"
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


    //return service to activity, bind service to activity
    public class MyLocalBinder extends Binder {
        public LocationService getBoundService(){
            return LocationService.this;
        }
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
    }

}