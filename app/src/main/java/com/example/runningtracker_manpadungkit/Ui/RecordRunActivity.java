package com.example.runningtracker_manpadungkit.Ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.location.Location;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import android.widget.Toast;
import android.Manifest;
import android.content.pm.PackageManager;
import 	android.os.Build;

import com.example.runningtracker_manpadungkit.R;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.*;

public class RecordRunActivity extends AppCompatActivity {

    public static final int INTERVAL_MILLIS = 1000;
    private static final int PERMISSION_FINE_LOCATION = 1;

    private TextView mDistanceTextView;
    private TextView mDurationTextView;
    private TextView mCaloriesTextView;
    private TextView mAltitudeTextView;
    private TextView mSpeedTextView;

    Timer timer;
    TimerTask timerTask;
    double time = 0.0;

    private ImageButton mPauseButton;
    private ImageButton mStopButton;

    int second = 0;
    int minute = 0;
    int hour = 0;

    //Google's API for location service
    private FusedLocationProviderClient fusedLocationClient;

    //config file for all settings of FusedLocationProviderClient
    private LocationRequest locationRequest;

    //get location update every interval
    private LocationCallback locationCallback;

    @SuppressLint({"WrongViewCast", "MissingPermission"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_run);

        widgetInit();

        startTimer();

        LocationRequest locationRequest = new
                LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, INTERVAL_MILLIS).build();

        //called every interval of fuseLocationProviderClient
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                for(Location location: locationResult.getLocations()){
                    Log.d("help", "onLocationResult: ");
                    updateUI(location);
                }
            }
        };
        updateLocation();
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void startTimer() {
        timer = new Timer();
        timerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        time++;
                        mDistanceTextView.setText(String.valueOf(getTimerText()));
                    }
                });
            }

        };
        timer.scheduleAtFixedRate(timerTask, 0 ,1000);
    }

    private String getTimerText()
    {
        int rounded = (int) Math.round(time);

        int seconds = ((rounded % 86400) % 3600) % 60;
        int minutes = ((rounded % 86400) % 3600) / 60;
        int hours = ((rounded % 86400) / 3600);

        return formatTime(seconds, minutes, hours);
    }

    private String formatTime(int seconds, int minutes, int hours)
    {
        return String.format("%02d",hours) + " : " + String.format("%02d",minutes) + " : " + String.format("%02d",seconds);
    }

    //Check if Location permission is granted
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode){
            case PERMISSION_FINE_LOCATION:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    updateLocation();
                }
                else{
                    Toast.makeText(this, "Location permission needed", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void widgetInit() {
        mDistanceTextView = findViewById(R.id.distance);
        mDurationTextView = findViewById(R.id.duration);
        mCaloriesTextView = findViewById(R.id.calories);
        mAltitudeTextView = findViewById(R.id.altitute);
        mSpeedTextView = findViewById(R.id.speed);
        
        mPauseButton = findViewById(R.id.pauseButton);
        mStopButton = findViewById(R.id.stopButton);

    }

    private void updateLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //permission is granted
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    //permission granted and ready to use
                    updateUI(location);
                }
            });
        }
        else{
            //permission denied
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
            }
        }
    }

    private void updateUI(Location location) {
        //Update all the textView with new location
        if (location != null) {
            mDurationTextView.setText(String.valueOf(location.getLongitude()));
            mDistanceTextView.setText(String.valueOf(location.getLatitude()));
        }

        //check if phone has altitude checker function
        if(location.hasAltitude()){
            mAltitudeTextView.setText(String.valueOf(location.getAltitude()));
        }
        // if not put "Not Available"
        else{
            mAltitudeTextView.setText(R.string.not_available);
        }
        //check if phone has speed checker function
        if(location.hasSpeed()){
            mSpeedTextView.setText((String.valueOf(location.getSpeed())));
        }
        // if not put "Not Available"
        else{
            mSpeedTextView.setText(R.string.not_available);
        }

    }

    private String setTimer() {
        second += 1;
        if(second == 60){
            second = 0;
            minute += 1;
        }
        if(minute == 60){
            minute = 0;
            hour += 1;
        }
        String result = String.valueOf(hour)+ " h "+String.valueOf(minute)+" m "+String.valueOf(second) + " s";

        return result;
    }
}