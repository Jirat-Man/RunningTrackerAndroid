package com.example.runningtracker_manpadungkit.Ui;

import static com.example.runningtracker_manpadungkit.Constants.EXTRA_DATE;
import static com.example.runningtracker_manpadungkit.Constants.EXTRA_DURATION;
import static com.example.runningtracker_manpadungkit.Constants.EXTRA_DURATION_FROM_RECORD;
import static com.example.runningtracker_manpadungkit.Constants.EXTRA_SECONDS;
import static com.example.runningtracker_manpadungkit.Constants.EXTRA_SPEED;
import static com.example.runningtracker_manpadungkit.Constants.NOTIFICATION_ID;
import static com.example.runningtracker_manpadungkit.Constants.RUN_RESULT_CODE;
import static com.example.runningtracker_manpadungkit.Ui.MainActivity.tracking;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModelProvider;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.widget.ImageButton;
import android.widget.TextView;
import android.location.Location;

import com.example.runningtracker_manpadungkit.Service.LocationService;
import com.example.runningtracker_manpadungkit.RunViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


import android.widget.Toast;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;

import com.example.runningtracker_manpadungkit.R;
import com.google.android.gms.tasks.OnSuccessListener;

public class RecordRunActivity extends AppCompatActivity{

    //Service intent for location service
    Intent serviceIntent;
    //Boolean to check if there is any service binded to the activity
    boolean isBound = false;
    static boolean onPause = false;

    //All the textview in the activity
    private TextView mDistanceTextView;
    private TextView mDurationTextView;
    private TextView mAltitudeTextView;
    private TextView mSpeedTextView;
    private TextView mDateTextView;

    //All the buttons in the activity
    private ImageButton mPauseButton;
    private ImageButton mStopButton;

    //Various variables related to the run
    double mDistance = 0;
    String mDuration;
    double mSpeed = 0;
    String mDate;
    String mStartTime;
    String mAltitude;
    String mAvgSpeed;
    int mSeconds = -1;

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
            onPause = !onPause;
            if(onPause){
                Toast.makeText(this, "tracking paused", Toast.LENGTH_SHORT).show();
                mPauseButton.setImageResource(R.drawable.play_button);
                pauseTracking();
            }
            else{
                Toast.makeText(this, "tracking resume", Toast.LENGTH_SHORT).show();
                mPauseButton.setImageResource(R.drawable.pause_button);
                continueTracking();
            }
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
                    if(!isBound){
                        serviceBind();
                    }
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
        serviceIntent = new Intent(RecordRunActivity.this, LocationService.class);
        startService(serviceIntent);
        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            service = (LocationService.MyLocalBinder) binder;
            handler = new Handler();
            isBound = true;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (service != null) {
                        if(mSeconds == 0){
                            mStartTime = service.getDate();
                        }
                        mDistance = service.getDistance();
                        mDuration = service.getDuration();
                        mSpeed = service.getSpeed();
                        mDate = service.getDate();
                        mAltitude = service.getAltitude();
                        mAvgSpeed = String.valueOf(service.getAvgSpeed());
                        mSeconds++;
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
                            Thread.sleep(1000);
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
        service.pauseTracking();
    }
    private void continueTracking() {
        service.continueTracking();
    }
    //brings up a dialog asking for confirmation from the user that they want to stop the run
    private void stopButtonDialogConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirm_dialog_message)
                .setTitle(R.string.confirm_dialog_title)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent();
                        intent.putExtra(EXTRA_DURATION_FROM_RECORD, String.valueOf(mDistance));
                        intent.putExtra(EXTRA_DURATION, mDuration);
                        intent.putExtra(EXTRA_SPEED, mAvgSpeed);
                        intent.putExtra(EXTRA_DATE, mStartTime);
                        intent.putExtra(EXTRA_SECONDS, mSeconds);
                        setResult(RUN_RESULT_CODE, intent);
                        isBound = false;
                        tracking = false;
                        onPause = false;
                        stopService(serviceIntent);
                        RecordRunActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Do nothing if user cancels;
                    }
                });

        // Create the AlertDialog object and return it
        builder.create().show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        isBound = false;
        if(serviceConnection != null){
            unbindService(serviceConnection);
            serviceConnection = null;
        }
    }

    private void widgetInit() {
        mDistanceTextView = findViewById(R.id.distance);
        mDurationTextView = findViewById(R.id.duration);
        mAltitudeTextView = findViewById(R.id.altitude);
        mSpeedTextView = findViewById(R.id.speed);
        mPauseButton = findViewById(R.id.pauseButton);
        mStopButton = findViewById(R.id.stopButton);
        mDateTextView = findViewById(R.id.date);

        if(onPause){
            mPauseButton.setImageResource(R.drawable.play_button);
        }
        else{
            mPauseButton.setImageResource(R.drawable.pause_button);
        }
    }
}