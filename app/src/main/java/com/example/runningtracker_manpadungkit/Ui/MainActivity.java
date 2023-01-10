package com.example.runningtracker_manpadungkit.Ui;

import static com.example.runningtracker_manpadungkit.Ui.RecordRunActivity.RUN_RESULT_CODE;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.runningtracker_manpadungkit.R;
import com.example.runningtracker_manpadungkit.Room.RunEntity;
import com.example.runningtracker_manpadungkit.ViewModel.RunViewModel;
import android.widget.ImageButton;
import android.widget.Toast;

import org.chromium.base.Log;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_GPS_CODE = 1;
    private RunViewModel mRunViewModel;
    ImageButton mRecordRunButton;
    ImageButton mAnalyticsButton;

    String mDistance;
    String mDuration;
    String mSpeed;
    String mDate;

    ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
             if(result != null && result.getResultCode() == RUN_RESULT_CODE){
                 // launch the summary activity
                 Intent resultIntent = result.getData();

                 if(resultIntent != null){
                     mDistance = resultIntent.getStringExtra("distance");
                     mDuration = resultIntent.getStringExtra("duration");
                     mSpeed = resultIntent.getStringExtra("speed");
                     mDate = resultIntent.getStringExtra("date");
                 }
                 Intent intent = new Intent(MainActivity.this, WorkoutSummaryActivity.class);
                 intent.putExtra("distance", mDistance);
                 intent.putExtra("duration", mDuration);
                 intent.putExtra("speed", mSpeed);
                 intent.putExtra("date", mDate);
                 startActivity(intent);
             }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecordRunButton = findViewById(R.id.RecordRun);
        mAnalyticsButton = findViewById(R.id.analyse);

        //mRecordRunButton button listener
        mRecordRunButton.setOnClickListener(view -> {
            Intent journey = new Intent(MainActivity.this, RecordRunActivity.class);
            startForResult.launch(journey);
        });

        mAnalyticsButton.setOnClickListener(view -> {
            Intent journey = new Intent(MainActivity.this, AnalyticsActivity.class);
            startActivity(journey);
        });

        askForLocationPermission();

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