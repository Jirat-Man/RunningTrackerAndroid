package com.example.runningtracker_manpadungkit.Ui;

import static java.lang.String.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.location.Location;

import com.example.runningtracker_manpadungkit.Room.RunEntity;
import com.example.runningtracker_manpadungkit.ViewModel.RunViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;


import android.widget.Toast;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;

import com.example.runningtracker_manpadungkit.R;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.*;

public class RecordRunActivity extends AppCompatActivity {

    public static final int INTERVAL_MILLIS = 1000;
    private static final int PERMISSION_FINE_LOCATION = 1;

    private TextView mDistanceTextView;
    private TextView mDurationTextView;
    private TextView mAltitudeTextView;
    private TextView mSpeedTextView;

    Timer mTimer;
    TimerTask mTimerTask;
    double mTime = 0.0;

    private ImageButton mPauseButton;
    private ImageButton mStopButton;

    double mDistance = 0;
    double mLongitude = 0;
    double mLatitude = 0;
    int counter = 0;

    //Google's API for location service
    private FusedLocationProviderClient fusedLocationClient;

    //config file for all settings of FusedLocationProviderClient
    private LocationRequest locationRequest;

    //get location update every interval
    private LocationCallback locationCallback;

    //ViewModel
    RunViewModel mRunViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_run);

        widgetInit();

        startTimer();

        //initialise ViewModel
        mRunViewModel = new ViewModelProvider(this).get(RunViewModel.class);


        //ViewModel observe LiveData
        mRunViewModel.getAllRuns().observe(this, mAllRuns -> {
            org.chromium.base.Log.d("run", ": "+mAllRuns.size());
            for(RunEntity run: mAllRuns){
                org.chromium.base.Log.d("run", String.valueOf(run.getSpeed()));
            }
        });

        LocationRequest locationRequest = new
                LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, INTERVAL_MILLIS).build();

        //called every interval
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                for (Location location : locationResult.getLocations()) {
                    Log.d("help123", "onLocationResult: ");
                    updateUI(location);
                }
            }
        };
        updateLocation();
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

        mStopButton.setOnClickListener(view -> {
            stopButtonDialogConfirmation();
            Log.d("Hello", "hello it worked");
        });
    }

    //brings up a dialog asking for confirmation from the user that they want to stop the run
    private void stopButtonDialogConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirm_dialog_message)
                .setTitle(R.string.confirm_dialog_title)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // CONFIRM
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // CANCEL
                    }
                });

        // Create the AlertDialog object and return it
        builder.create().show();
    }


    private void startTimer() {
        mTimer = new Timer();
        mTimerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                runOnUiThread(() -> {
                    mTime++;
                    mDurationTextView.setText(getTimerText());
                });
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
        mAltitudeTextView = findViewById(R.id.altitude);
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
            mDistanceTextView.setText(valueOf(getDistance(location)));
            mLongitude = location.getLongitude();
            mLatitude = location.getLatitude();

            Log.d("HELLLOOOOO", String.valueOf(location.getLongitude()));

            //check if phone has altitude checker function
            if(location.hasAltitude()){
                mAltitudeTextView.setText(valueOf(location.getAltitude()));
            }
            // if not put "Not Available"
            else{
                mAltitudeTextView.setText(R.string.not_available);
            }
            //check if phone has speed checker function
            if(location.hasSpeed()){
                mSpeedTextView.setText((String.valueOf((double) Math.round(location.getSpeed() * 1d))));
            }
            // if not put "Not Available"
            else{
                mSpeedTextView.setText(R.string.not_available);
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
}