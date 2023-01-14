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
import static com.example.runningtracker_manpadungkit.Ui.MainActivity.tracking;
import static com.example.runningtracker_manpadungkit.Ui.RecordRunActivity.onPause;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.content.ContextWrapper;

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
import java.io.IOException;

public class WorkoutSummaryActivity extends AppCompatActivity {

    String mRunComment;
    float mNumberOfStars;
    String mDistance;
    String mDuration;
    String mDate;
    String mSpeed;
    int mSeconds;
    Uri uri;
    Bitmap bitmap;
    byte[] imageByte;
    int id;

    RunViewModel mRunViewModel;

    private ActivitySummaryBinding mSummaryBinding;


        ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result != null && result.getResultCode() == IMAGE_PICKER_CODE && result.getData() != null){
                    uri = result.getData().getData();
                    try {
                        bitmap = BitmapFactory.decodeStream(getApplicationContext().getContentResolver().openInputStream(uri));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    mSummaryBinding.imageView.setImageBitmap(bitmap);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG,80,stream);
                    imageByte = stream.toByteArray();
                }
            }
        });

        @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSummaryBinding = DataBindingUtil.setContentView(this, R.layout.activity_summary);

        //initialise ViewModel
        mRunViewModel = new ViewModelProvider(this).get(RunViewModel.class);

        if(getIntent().hasExtra(EXTRA_ID)){
            id = getIntent().getIntExtra(EXTRA_ID, -1);
            mSummaryBinding.runDistance.setText(getIntent().getStringExtra(EXTRA_DISTANCE));
            mSummaryBinding.runDuration.setText(getIntent().getStringExtra(EXTRA_DURATION));
            mSummaryBinding.runDate.setText(getIntent().getStringExtra(EXTRA_DATE));
            mSummaryBinding.runSpeed.setText(getIntent().getStringExtra(EXTRA_SPEED));
            mSummaryBinding.ratingBar.setRating(getIntent().getFloatExtra(EXTRA_RATING, 0));
            mSummaryBinding.editText.setText(getIntent().getStringExtra(EXTRA_COMMENT));

            if(getIntent().getByteArrayExtra(EXTRA_IMAGE) != null){
                byte[] byteArray = getIntent().getByteArrayExtra(EXTRA_IMAGE);
                Bitmap compressedBitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
                mSummaryBinding.imageView.setImageBitmap(compressedBitmap);
            }

            imageByte = getIntent().getByteArrayExtra(EXTRA_IMAGE);
            mDistance = getIntent().getStringExtra(EXTRA_DISTANCE) ;
            mDuration = getIntent().getStringExtra(EXTRA_DURATION);
            mDate = getIntent().getStringExtra(EXTRA_DATE);
            mSpeed = getIntent().getStringExtra(EXTRA_SPEED);
        }
        else if(getIntent().hasExtra(EXTRA_DURATION_FROM_RECORD)){
            getRunResult();
        }

        if(savedInstanceState != null){
            imageByte = savedInstanceState.getByteArray(SAVE_IMAGE_BYTE);
        }
        if(imageByte != null){

            Bitmap compressedBitmap = BitmapFactory.decodeByteArray(imageByte,0,imageByte.length);
            mSummaryBinding.imageView.setImageBitmap(compressedBitmap);
        }

        mSummaryBinding.uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED){
                    //No permission
                    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    requestPermissions(permissions, IMAGE_PERMISSION_CODE);
                }
                else{
                    getImage();
                }
            }
        });

        mSummaryBinding.doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeRunData();
                WorkoutSummaryActivity.super.onBackPressed();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putByteArray(SAVE_IMAGE_BYTE, imageByte);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void getImage() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        setResult(IMAGE_PICKER_CODE, intent);
        startForResult.launch(intent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case IMAGE_PERMISSION_CODE:
                if(grantResults.length > 0 && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED){
                    getImage();
                }
                else{
                    Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void storeRunData() {
        getRunRating();
        getRunComment();
        if(getIntent().hasExtra(EXTRA_DURATION_FROM_RECORD)){
            storeInRoomDatabase();
        }
        else if(getIntent().hasExtra(EXTRA_ID)){
            updateRoomDatabase();
        }
    }

    //get avg speed from distance/time
    private void getAvgSpeed(){
        double tempDist = Double.parseDouble(mDistance);

        //distance is in km so have to *1000
        double distance = tempDist*1000;
        double AvgSpeed = distance/mSeconds;
        mSpeed = String.valueOf(Math.round(((1000/AvgSpeed)/60)* 100d)/100d);
        Toast.makeText(this, mSpeed, Toast.LENGTH_SHORT).show();
        if(distance == 0){
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
        RunEntity run = new RunEntity(mDuration, mDistance,
                mSpeed,mDate, mNumberOfStars, mRunComment, imageByte);
        run.setId(id);
        mRunViewModel.Update(run);
    }
    private void storeInRoomDatabase() {
        RunEntity run = new RunEntity(mDuration, mDistance,
                mSpeed,mDate, mNumberOfStars, mRunComment, imageByte);
        mRunViewModel.Insert(run);
    }

    private void getRunComment() {
        mRunComment = String.valueOf(mSummaryBinding.editText.getText());
    }

    private void getRunRating() {
        mNumberOfStars = mSummaryBinding.ratingBar.getRating();
    }

    private void getRunResult() {
        Intent intent = getIntent();
        mDistance = intent.getStringExtra(EXTRA_DURATION_FROM_RECORD) ;
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
