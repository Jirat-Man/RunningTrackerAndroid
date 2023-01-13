package com.example.runningtracker_manpadungkit.Ui;

import static com.example.runningtracker_manpadungkit.Constants.EXTRA_DATE;
import static com.example.runningtracker_manpadungkit.Constants.EXTRA_DURATION;
import static com.example.runningtracker_manpadungkit.Constants.EXTRA_DURATION_FROM_RECORD;
import static com.example.runningtracker_manpadungkit.Constants.EXTRA_SECONDS;
import static com.example.runningtracker_manpadungkit.Constants.EXTRA_SPEED;
import static com.example.runningtracker_manpadungkit.Constants.PERMISSION_GPS_CODE;
import static com.example.runningtracker_manpadungkit.Constants.RUN_RESULT_CODE;
import static com.example.runningtracker_manpadungkit.Ui.RecordRunActivity.onPause;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.runningtracker_manpadungkit.R;
import com.example.runningtracker_manpadungkit.RunViewModel;

import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private RunViewModel mRunViewModel;
    ImageButton mRecordRunButton;
    ImageButton mAnalyticsButton;
    WebView mGifTracking;
    TextView mPressToStartTextView;
    TextView mInProgressTextview;
    TextView mOnPauseTextView;

    String mDistance;
    String mDuration;
    String mSpeed;
    String mDate;
    int mSeconds;
    public static Boolean tracking = false;

    ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {

             if(result != null && result.getResultCode() == RUN_RESULT_CODE){
                 // launch the summary activity
                 Intent resultIntent = result.getData();

                 if(resultIntent != null){
                     mDistance = resultIntent.getStringExtra(EXTRA_DURATION_FROM_RECORD);
                     mDuration = resultIntent.getStringExtra(EXTRA_DURATION);
                     mSpeed = resultIntent.getStringExtra(EXTRA_SPEED);
                     mDate = resultIntent.getStringExtra(EXTRA_DATE);
                     mSeconds = resultIntent.getIntExtra(EXTRA_SECONDS, 0);

                     Intent intent = new Intent(MainActivity.this, WorkoutSummaryActivity.class);
                     intent.putExtra(EXTRA_DURATION_FROM_RECORD, mDistance);
                     intent.putExtra(EXTRA_DURATION, mDuration);
                     intent.putExtra(EXTRA_SPEED, mSpeed);
                     intent.putExtra(EXTRA_DATE, mDate);
                     intent.putExtra(EXTRA_SECONDS, mSeconds);
                     startActivity(intent);
                 }
             }
            //set gif and text visibility
            handleViewVisibility();
        }
    });

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecordRunButton = findViewById(R.id.RecordRun);
        mAnalyticsButton = findViewById(R.id.analyse);
        mGifTracking = findViewById(R.id.gifTracking);
        mPressToStartTextView = findViewById(R.id.pressToStart);
        mInProgressTextview = findViewById(R.id.trackingInProgress);
        mOnPauseTextView = findViewById(R.id.trackingOnPause);

        mGifTracking.getSettings().setJavaScriptEnabled(true);
        mGifTracking.setWebViewClient(new WebViewClient());
        String file = "file:android_asset/gif_tracking.gif";
        //url of gif
        mGifTracking.loadUrl(file);

        handleViewVisibility();

        //mRecordRunButton button listener
        mRecordRunButton.setOnClickListener(view -> {
            Intent journey = new Intent(MainActivity.this, RecordRunActivity.class);
            startForResult.launch(journey);
            tracking = true;
        });

        mAnalyticsButton.setOnClickListener(view -> {
            Intent journey = new Intent(MainActivity.this, AnalyticsActivity.class);
            startActivity(journey);
        });

        askForLocationPermission();

    }

    private void handleViewVisibility() {
        if(onPause && tracking){
            //Tracking Paused
            mGifTracking.setVisibility(View.VISIBLE);
            mPressToStartTextView.setVisibility(View.INVISIBLE);
            mInProgressTextview.setVisibility(View.INVISIBLE);
            mOnPauseTextView.setVisibility(View.VISIBLE);
            Log.d("EEEE", "1");
        }
        if(tracking && !onPause){
            //Tracking is in progress
            mGifTracking.setVisibility(View.VISIBLE);
            mPressToStartTextView.setVisibility(View.INVISIBLE);
            mInProgressTextview.setVisibility(View.VISIBLE);
            mOnPauseTextView.setVisibility(View.INVISIBLE);
            Log.d("EEEE", "2");
        }
        if(!tracking && !onPause){
            //No tracking started
            mGifTracking.setVisibility(View.INVISIBLE);
            mPressToStartTextView.setVisibility(View.VISIBLE);
            mInProgressTextview.setVisibility(View.INVISIBLE);
            mOnPauseTextView.setVisibility(View.INVISIBLE);
            Log.d("EEEE", "3");
        }
    }

    public static class PermissionNotGrantedDialog extends DialogFragment {
        public static PermissionNotGrantedDialog newInstance() {
            PermissionNotGrantedDialog dialog = new PermissionNotGrantedDialog();
            return dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("GPS is needed for tracking your runs")
                    .setPositiveButton("Enable GPS", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // user agreed to enable GPS
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_GPS_CODE);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // user decide to not enable GPS
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
    private void askForLocationPermission() {
        //Request Location from user
        //if request is denied, disable the record run button
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //re-ask for location when denied
                DialogFragment permissionNotGrantedDialog = PermissionNotGrantedDialog.newInstance();
                permissionNotGrantedDialog.show(getSupportFragmentManager(), "Permissions");
            } else {
                // request the permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_GPS_CODE);
            }

        }
    }

    //check if Location is permitted, or else disable record run button
    @Override
    public void onRequestPermissionsResult(int reqCode, String[] permissions, int[] results) {
        super.onRequestPermissionsResult(reqCode, permissions, results);
        switch (reqCode) {
            case PERMISSION_GPS_CODE:
                if (results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted
                    mRecordRunButton.setEnabled(true);
                }
                else {
                    // permission denied, disable GPS tracking buttons
                    Toast.makeText(this, "Enable GPS in settings before you can proceed", Toast.LENGTH_SHORT).show();
                }
                return;

        }
    }
}