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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.runningtracker_manpadungkit.R;
import com.example.runningtracker_manpadungkit.databinding.ActivityMainBinding;

import android.view.View;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    String mDistance;
    String mDuration;
    String mSpeed;
    String mDate;
    int mSeconds;
    public static Boolean tracking = false;

    private ActivityMainBinding mMainBinding;

    ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {

            if (result != null && result.getResultCode() == RUN_RESULT_CODE) {
                // launch the summary activity
                Intent resultIntent = result.getData();

                if (resultIntent != null) {
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
        mMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mMainBinding.RecordRunButton.setOnClickListener(v -> {
            Intent journey = new Intent(MainActivity.this, RecordRunActivity.class);
            tracking = true;
            startForResult.launch(journey);
        });
        mMainBinding.analyseButton.setOnClickListener(v -> {
            Intent journey = new Intent(MainActivity.this, AnalyticsActivity.class);
            startActivity(journey);
        });

        mMainBinding.gifTracking.getSettings().setJavaScriptEnabled(true);
        mMainBinding.gifTracking.setWebViewClient(new WebViewClient());
        String file = "file:android_asset/gif_tracking.gif";
        mMainBinding.gifTracking.loadUrl(file);

        handleViewVisibility();
        askForLocationPermission();

    }

    private void handleViewVisibility() {
        if (onPause && tracking) {
            //Tracking Paused
            mMainBinding.gifTracking.setVisibility(View.VISIBLE);
            mMainBinding.pressToStart.setVisibility(View.INVISIBLE);
            mMainBinding.trackingInProgress.setVisibility(View.INVISIBLE);
            mMainBinding.trackingOnPause.setVisibility(View.VISIBLE);
        }
        if (tracking && !onPause) {
            //Tracking is in progress
            mMainBinding.gifTracking.setVisibility(View.VISIBLE);
            mMainBinding.pressToStart.setVisibility(View.INVISIBLE);
            mMainBinding.trackingInProgress.setVisibility(View.VISIBLE);
            mMainBinding.trackingOnPause.setVisibility(View.INVISIBLE);
        }
        if (!tracking && !onPause) {
            //No tracking started
            mMainBinding.gifTracking.setVisibility(View.INVISIBLE);
            mMainBinding.pressToStart.setVisibility(View.VISIBLE);
            mMainBinding.trackingInProgress.setVisibility(View.INVISIBLE);
            mMainBinding.trackingOnPause.setVisibility(View.INVISIBLE);
        }
    }

    public static class PermissionNotGrantedDialog extends DialogFragment {
        public static PermissionNotGrantedDialog newInstance() {
            return new PermissionNotGrantedDialog();
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("GPS is needed for tracking your runs")
                    .setPositiveButton("Enable GPS", (dialog, id) -> {
                        // user agreed to enable GPS
                        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_GPS_CODE);
                    })
                    .setNegativeButton("No", (dialog, id) -> {
                        // user decide to not enable GPS
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

    private void askForLocationPermission() {
        //Request Location from user
        //if request is denied, disable the record run button
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
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
    public void onRequestPermissionsResult(int reqCode, @NonNull String[] permissions, @NonNull int[] results) {
        super.onRequestPermissionsResult(reqCode, permissions, results);
        if (reqCode == PERMISSION_GPS_CODE) {
            if (results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED) {
                // permission granted
                mMainBinding.RecordRunButton.setEnabled(true);
            } else {
                // permission denied, disable GPS tracking buttons
                Toast.makeText(this, "Enable GPS in settings before you can proceed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}