package com.example.runningtracker_manpadungkit.ui;

import static com.example.runningtracker_manpadungkit.Constants.EXTRA_DATE;
import static com.example.runningtracker_manpadungkit.Constants.EXTRA_DURATION;
import static com.example.runningtracker_manpadungkit.Constants.EXTRA_DURATION_FROM_RECORD;
import static com.example.runningtracker_manpadungkit.Constants.EXTRA_SECONDS;
import static com.example.runningtracker_manpadungkit.Constants.EXTRA_SPEED;
import static com.example.runningtracker_manpadungkit.Constants.RUN_RESULT_CODE;
import static com.example.runningtracker_manpadungkit.ui.MainActivity.tracking;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import com.example.runningtracker_manpadungkit.service.LocationService;
import com.example.runningtracker_manpadungkit.databinding.ActivityRecordRunBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import android.widget.Toast;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import com.example.runningtracker_manpadungkit.R;

public class RecordRunActivity extends AppCompatActivity{

    //Service intent for location service
    Intent serviceIntent;
    //Boolean to check if there is any service bound to the activity
    boolean isBound = false;
    //Boolean to check if tracking is paused
    static boolean onPause = false;

    //Various variables related to the run
    double mDistance = 0;
    String mDuration;
    double mSpeed = 0;
    String mDate;
    String mStartTime;
    String mAltitude;
    String mAvgSpeed;
    int mSeconds = -1;

    //handler to change UI views using Service data
    private Handler handler;

    // ServiceBinder
    private LocationService.MyLocalBinder mLocalBinder;

    //Data Binding Object
    private ActivityRecordRunBinding mRecordRunBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initialise activity
        mRecordRunBinding = DataBindingUtil.setContentView(this, R.layout.activity_record_run);

        //handle the image so that users know whether tracking is paused or live
        handlePausePlayImage();

        //check location permission, if granted bind to service
        checkLocationPermission();

        //StopButton listener, launch confirmation dialog
        mRecordRunBinding.stopButton.setOnClickListener(v -> stopButtonDialogConfirmation());

        //PauseButton, pause tracking and change button image
        mRecordRunBinding.pauseButton.setOnClickListener(v -> {
            onPause = !onPause;
            if(onPause){
                Toast.makeText(RecordRunActivity.this, "Tracking paused", Toast.LENGTH_SHORT).show();
                mRecordRunBinding.pauseButton.setImageResource(R.drawable.play_button);
                pauseTracking();
            }
            else{
                Toast.makeText(RecordRunActivity.this, "Tracking resume", Toast.LENGTH_SHORT).show();
                mRecordRunBinding.pauseButton.setImageResource(R.drawable.pause_button);
                continueTracking();
            }
        });
    }


    private void checkLocationPermission() {
        //Google's API for location service
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //permission is granted
            fusedLocationClient.getLastLocation().addOnSuccessListener(RecordRunActivity.this, location -> {
                //permission granted and ready to use
                //bind to service
                serviceBind();
                tracking = true;
            });
        }
        else{
            //permission denied
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                Toast.makeText(this, "You don't have Location Permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //bind to service
    private void serviceBind() {
        serviceIntent = new Intent(RecordRunActivity.this, LocationService.class);
        //Start service so that service persist even after activity is destroy
        startService(serviceIntent);
        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    //Service connection
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            mLocalBinder = (LocationService.MyLocalBinder) binder;
            handler = new Handler();
            isBound = true;

            new Thread(() -> {
                while (mLocalBinder != null) {
                    if(mSeconds == 0){
                        mStartTime = mLocalBinder.getDate();
                    }
                    mDistance = mLocalBinder.getDistance();
                    mDuration = mLocalBinder.getDuration();
                    mSpeed = mLocalBinder.getSpeed();
                    mDate = mLocalBinder.getDate();
                    mAltitude = mLocalBinder.getAltitude();
                    mAvgSpeed = String.valueOf(mLocalBinder.getAvgSpeed());
                    mSeconds++;
                    //Update the UI of activity with the location data from the service
                    handler.post(() -> {
                        mRecordRunBinding.distance.setText(String.valueOf(mDistance));
                        mRecordRunBinding.speed.setText(String.valueOf(mSpeed));
                        mRecordRunBinding.duration.setText(String.valueOf(mDuration));
                        mRecordRunBinding.altitude.setText(mAltitude);
                        mRecordRunBinding.date.setText(mDate);
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    //pause activities going on in the service
    private void pauseTracking() {
        mLocalBinder.pauseTracking();
    }

    //resume activities in the service
    private void continueTracking() {
        mLocalBinder.continueTracking();
    }

    //brings up a dialog asking for confirmation from the user that they want to stop the run
    //Once confirm, sends users back to MainActivity with the data
    //From Main Activity, immediately launch workoutSummary with the attached data.
    //Unbind from Service as well
    private void stopButtonDialogConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirm_dialog_message)
                .setTitle(R.string.confirm_dialog_title)
                .setPositiveButton(R.string.confirm, (dialog, id) -> {
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
                    if(serviceConnection != null){
                        unbindService(serviceConnection);
                        serviceConnection = null;
                    }
                    stopService(serviceIntent);
                    RecordRunActivity.super.onBackPressed();
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    // Do nothing if user cancels;
                });

        // Create the AlertDialog object and return it
        builder.create().show();
    }

    //handle image on button
    private void handlePausePlayImage() {
        if(onPause){
            mRecordRunBinding.pauseButton.setImageResource(R.drawable.play_button);
        }
        else{
            mRecordRunBinding.pauseButton.setImageResource(R.drawable.pause_button);
        }
    }
}