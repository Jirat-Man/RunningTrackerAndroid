package com.example.runningtracker_manpadungkit.Ui;

import static java.lang.String.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.location.Location;

import com.example.runningtracker_manpadungkit.Service.LocationService;
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

import java.text.SimpleDateFormat;
import java.util.*;

public class RecordRunActivity extends AppCompatActivity{

    public static int RUN_RESULT_CODE = 99;

    LocationService locationService = new LocationService();
    boolean isBound = false;

    public static final int INTERVAL_MILLIS = 1000;
    private static final int PERMISSION_FINE_LOCATION = 1;

    private TextView mDistanceTextView;
    private TextView mDurationTextView;
    private TextView mAltitudeTextView;
    private TextView mSpeedTextView;
    private TextView mDateTextView;

    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String date;

    Timer mTimer;
    TimerTask mTimerTask;
    double mTime = 0.0;

    private ImageButton mPauseButton;
    private ImageButton mStopButton;

    double mDistance = 0;
    String mDuration;
    double mSpeed = 0;
    String mDate;
    double mLongitude = 0;
    double mLatitude = 0;
    String mAltitude;
    String mAvgSpeed;

    int counter = 0;
    int seconds = 0;

    //Google's API for location service
    private FusedLocationProviderClient fusedLocationClient;

    //config file for all settings of FusedLocationProviderClient
    private LocationRequest locationRequest;

    //get location update every interval
    private LocationCallback locationCallback;

    //ViewModel
    private RunViewModel mRunViewModel;

    private Handler handler;

    private LocationService.MyLocalBinder service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_run);

        widgetInit();

        checkLocationPermission();

        //initialise ViewModel
        mRunViewModel = new ViewModelProvider(this).get(RunViewModel.class);

        mStopButton.setOnClickListener(view -> {
            stopButtonDialogConfirmation();
        });

        mPauseButton.setOnClickListener(view -> {
            pauseTracking();
        });

    }

    private void checkLocationPermission() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //permission is granted
            fusedLocationClient.getLastLocation().addOnSuccessListener(RecordRunActivity.this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    //permission granted and ready to use
                    serviceBind();
                }
            });
        }
        else{
            //permission denied
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                Toast.makeText(this, "You don't have Location Permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void serviceBind() {
        Intent intent = new Intent(RecordRunActivity.this, LocationService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            service = (LocationService.MyLocalBinder) binder;
            //locationService = service.getBoundService();
            handler = new Handler();
            isBound = true;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (service != null) {
                        mDistance = service.getDistance();
                        mDuration = service.getDuration();
                        mSpeed = service.getSpeed();
                        mDate = service.getDate();
                        mAltitude = service.getAltitude();
                        mAvgSpeed = String.valueOf(service.getAvgSpeed());

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                mDistanceTextView.setText(String.valueOf(mDistance));
                                mSpeedTextView.setText(String.valueOf(mSpeed));
                                mDurationTextView.setText(String.valueOf(mDuration));
                                mAltitudeTextView.setText(mAltitude);
                                mDateTextView.setText(mDate);
                            }
                        });
                        try {
                            Thread.sleep(500);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            service = null;
            isBound = false;
        }
    };

    private void pauseTracking() {

    }

    //brings up a dialog asking for confirmation from the user that they want to stop the run
    private void stopButtonDialogConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirm_dialog_message)
                .setTitle(R.string.confirm_dialog_title)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent();
                        intent.putExtra("distance", String.valueOf(mDistance));
                        intent.putExtra("duration", mDuration);
                        intent.putExtra("speed", mAvgSpeed);
                        intent.putExtra("date", mDate);
                        setResult(RUN_RESULT_CODE, intent);
                        RecordRunActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // CANCEL
                        mRunViewModel.DeleteAll();

                    }
                });

        // Create the AlertDialog object and return it
        builder.create().show();
    }

    private void widgetInit() {
        mDistanceTextView = findViewById(R.id.distance);
        mDurationTextView = findViewById(R.id.duration);
        mAltitudeTextView = findViewById(R.id.altitude);
        mSpeedTextView = findViewById(R.id.speed);
        mPauseButton = findViewById(R.id.pauseButton);
        mStopButton = findViewById(R.id.stopButton);
        mDateTextView = findViewById(R.id.date);
    }
}