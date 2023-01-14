package com.example.runningtracker_manpadungkit.Ui;

import static com.example.runningtracker_manpadungkit.Constants.EXTRA_COMMENT;
import static com.example.runningtracker_manpadungkit.Constants.EXTRA_DATE;
import static com.example.runningtracker_manpadungkit.Constants.EXTRA_DISTANCE;
import static com.example.runningtracker_manpadungkit.Constants.EXTRA_DURATION;
import static com.example.runningtracker_manpadungkit.Constants.EXTRA_DURATION_FROM_RECORD;
import static com.example.runningtracker_manpadungkit.Constants.EXTRA_ID;
import static com.example.runningtracker_manpadungkit.Constants.EXTRA_IMAGE;
import static com.example.runningtracker_manpadungkit.Constants.EXTRA_RATING;
import static com.example.runningtracker_manpadungkit.Constants.EXTRA_SECONDS;
import static com.example.runningtracker_manpadungkit.Constants.EXTRA_SPEED;
import static com.example.runningtracker_manpadungkit.Constants.IMAGE_PERMISSION_CODE;
import static com.example.runningtracker_manpadungkit.Constants.IMAGE_PICKER_CODE;
import static com.example.runningtracker_manpadungkit.Constants.SAVE_IMAGE_BYTE;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import com.example.runningtracker_manpadungkit.R;
import com.example.runningtracker_manpadungkit.Room.RunEntity;
import com.example.runningtracker_manpadungkit.RunViewModel;
import com.example.runningtracker_manpadungkit.databinding.ActivitySummaryBinding;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

public class WorkoutSummaryActivity extends AppCompatActivity {

    // all the run variable
    String mRunComment;
    float mNumberOfStars;
    String mDistance;
    String mDuration;
    String mDate;
    String mSpeed;
    int mSeconds;
    Uri uri;
    Bitmap bitmap;
    byte[] imageByte;//store image uploaded from gallery
    int id;

    RunViewModel mRunViewModel;

    private ActivitySummaryBinding mSummaryBinding;


    //handle activity result from gallery
    ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result != null && result.getResultCode() == IMAGE_PICKER_CODE && result.getData() != null) {
                //get Image URI
                uri = result.getData().getData();
                //Turn URI into a bitMap
                try {
                    bitmap = BitmapFactory.decodeStream(getApplicationContext().getContentResolver().openInputStream(uri));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                //set Image View as the bitmap
                mSummaryBinding.imageView.setImageBitmap(bitmap);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                //convert the bitmap into byte[] so it can be stored in the database
                imageByte = stream.toByteArray();
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initialise activity
        mSummaryBinding = DataBindingUtil.setContentView(this, R.layout.activity_summary);

        //initialise ViewModel
        mRunViewModel = new ViewModelProvider(this).get(RunViewModel.class);


        //if intent is from AnalyticsActivity
        // set all the Component of the activity with the data from the run that the user clicked
        if (getIntent().hasExtra(EXTRA_ID)) {
            id = getIntent().getIntExtra(EXTRA_ID, -1);
            //set all the textview
            mSummaryBinding.runDistance.setText(String.valueOf(getIntent().getDoubleExtra(EXTRA_DISTANCE, 0)));
            mSummaryBinding.runDuration.setText(getIntent().getStringExtra(EXTRA_DURATION));
            mSummaryBinding.runDate.setText(getIntent().getStringExtra(EXTRA_DATE));
            mSummaryBinding.runSpeed.setText(String.valueOf(getIntent().getDoubleExtra(EXTRA_SPEED, 0)));
            mSummaryBinding.ratingBar.setRating(getIntent().getFloatExtra(EXTRA_RATING, 0));
            mSummaryBinding.editText.setText(getIntent().getStringExtra(EXTRA_COMMENT));

            //set Image if there is one
            if (getIntent().getByteArrayExtra(EXTRA_IMAGE) != null) {
                byte[] byteArray = getIntent().getByteArrayExtra(EXTRA_IMAGE);
                Bitmap compressedBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                mSummaryBinding.imageView.setImageBitmap(compressedBitmap);
            }

            //set the local variable with the data for when we update the runEntity
            imageByte = getIntent().getByteArrayExtra(EXTRA_IMAGE);
            mDistance = String.valueOf(getIntent().getDoubleExtra(EXTRA_DISTANCE, 0));
            mDuration = getIntent().getStringExtra(EXTRA_DURATION);
            mDate = getIntent().getStringExtra(EXTRA_DATE);
            mSpeed = String.valueOf(getIntent().getDoubleExtra(EXTRA_SPEED, 0));
        }
        //if intent was from MainActivity
        else if (getIntent().hasExtra(EXTRA_DURATION_FROM_RECORD)) {
            getRunResult();
        }

        // save the image for when the screen rotates
        if (savedInstanceState != null) {
            imageByte = savedInstanceState.getByteArray(SAVE_IMAGE_BYTE);
        }
        if (imageByte != null) {
            Bitmap compressedBitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
            mSummaryBinding.imageView.setImageBitmap(compressedBitmap);
        }

        //Upload button listener
        mSummaryBinding.uploadImageButton.setOnClickListener(v -> {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED) {
                //No permission
                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permissions, IMAGE_PERMISSION_CODE);
            } else {
                //go to gallery to pick image
                getImage();
            }
        });

        //Done Button listener
        //Store/Update the data into the database
        mSummaryBinding.doneButton.setOnClickListener(v -> {
            storeRunData();
            Toast.makeText(WorkoutSummaryActivity.this, "Run stored in History", Toast.LENGTH_SHORT).show();
            WorkoutSummaryActivity.super.onBackPressed();
        });
    }

    //save image byte onSaveInstanceState
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putByteArray(SAVE_IMAGE_BYTE, imageByte);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    //launch intent to Image folder to get Image
    private void getImage() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        setResult(IMAGE_PICKER_CODE, intent);
        startForResult.launch(intent);
    }


    //ask for permission for access to gallery
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == IMAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                getImage();
            } else {
                Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Store run Data
    private void storeRunData() {
        getRunRating();
        getRunComment();
        //if new run, insert data
        if (getIntent().hasExtra(EXTRA_DURATION_FROM_RECORD)) {
            storeInRoomDatabase();
        }
        //if update run, update data
        else if (getIntent().hasExtra(EXTRA_ID)) {
            updateRoomDatabase();
        }
    }

    //get avg speed from distance/time
    private void getAvgSpeed() {
        double tempDist = Double.parseDouble(mDistance);

        //split duration String by ":" to get hours, minutes, seconds
        String[] split = mDuration.split(" : ");
        double hours = Double.parseDouble(split[0]) * 60;
        double minutes = Double.parseDouble(split[1]);
        double seconds = Double.parseDouble(split[2]) / 60;

        //add all the time in minutes together
        double totalMinutes = hours + minutes + seconds;

        //divide the total minutes of run by the total distance to get Min/Km
        double AvgSpeed = totalMinutes/tempDist;

        mSpeed = String.valueOf(Math.round(((AvgSpeed) * 100d)) / 100d);

        //if Distance is 0 or less than 0, make the Average speed 0
        if (tempDist <= 0) {
            mSpeed = String.valueOf(0);
        }
    }

    //Handle the event where users click black button instead of "done"
    //save and store all the data inside the room database
    @Override
    public void onBackPressed() {
        storeRunData();
        super.onBackPressed();
    }

    private void updateRoomDatabase() {
        RunEntity run = new RunEntity(mDuration, Double.parseDouble(mDistance),
                Double.parseDouble(mSpeed), mDate, mNumberOfStars, mRunComment, imageByte);
        //set id of run so that it knows which runEntity to update
        run.setId(id);
        mRunViewModel.Update(run);
    }

    private void storeInRoomDatabase() {
        RunEntity run = new RunEntity(mDuration, Double.parseDouble(mDistance),
                Double.parseDouble(mSpeed), mDate, mNumberOfStars, mRunComment, imageByte);
        mRunViewModel.Insert(run);
    }

    private void getRunComment() {
        //get edittext content
        mRunComment = String.valueOf(mSummaryBinding.editText.getText());
    }

    private void getRunRating() {
        //get Rating bar content
        mNumberOfStars = mSummaryBinding.ratingBar.getRating();
    }

    private void getRunResult() {
        //extract all the run data from the record activity
        Intent intent = getIntent();
        mDistance = intent.getStringExtra(EXTRA_DURATION_FROM_RECORD);
        mDuration = intent.getStringExtra(EXTRA_DURATION);
        mDate = intent.getStringExtra(EXTRA_DATE);
        mSpeed = intent.getStringExtra(EXTRA_SPEED);
        mSeconds = intent.getIntExtra(EXTRA_SECONDS, 0);

        mSummaryBinding.runDistance.setText(intent.getStringExtra(EXTRA_DURATION_FROM_RECORD));
        mSummaryBinding.runDuration.setText(intent.getStringExtra(EXTRA_DURATION));
        mSummaryBinding.runDate.setText(intent.getStringExtra(EXTRA_DATE));
        getAvgSpeed();
        mSummaryBinding.runSpeed.setText(mSpeed);
    }
}
