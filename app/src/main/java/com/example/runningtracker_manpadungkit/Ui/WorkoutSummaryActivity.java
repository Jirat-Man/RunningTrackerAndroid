package com.example.runningtracker_manpadungkit.Ui;

import static com.example.runningtracker_manpadungkit.Constants.EXTRA_COMMENT;
import static com.example.runningtracker_manpadungkit.Constants.EXTRA_DATE;
import static com.example.runningtracker_manpadungkit.Constants.EXTRA_DISTANCE;
import static com.example.runningtracker_manpadungkit.Constants.EXTRA_DURATION;
import static com.example.runningtracker_manpadungkit.Constants.EXTRA_ID;
import static com.example.runningtracker_manpadungkit.Constants.EXTRA_IMAGE;
import static com.example.runningtracker_manpadungkit.Constants.EXTRA_RATING;
import static com.example.runningtracker_manpadungkit.Constants.EXTRA_SPEED;
import static com.example.runningtracker_manpadungkit.Constants.IMAGE_PERMISSION_CODE;
import static com.example.runningtracker_manpadungkit.Constants.IMAGE_PICKER_CODE;
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
import androidx.lifecycle.ViewModelProvider;

import com.example.runningtracker_manpadungkit.R;
import com.example.runningtracker_manpadungkit.Room.RunEntity;
import com.example.runningtracker_manpadungkit.RunViewModel;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class WorkoutSummaryActivity extends AppCompatActivity {

    Button mDoneButton;
    Button mUploadButton;
    TextView mDistanceTextView;
    TextView mDurationTextView;
    TextView mDateTextView;
    TextView mSpeedTextView;
    ImageView mImageView;

    EditText mRunCommentEditText;
    String mRunComment;

    RatingBar mRunRatingBar;
    float mNumberOfStars;

    String mDistance;
    String mDuration;
    String mDate;
    String mSpeed;
    String path;
    Uri uri;
    Bitmap bitmap;
    byte[] imageByte;
    int id;

    RunViewModel mRunViewModel;


        ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result != null && result.getResultCode() == IMAGE_PICKER_CODE && result.getData() != null){
                    //mImageView.setImageURI(result.getData().getData());
                    uri = result.getData().getData();
                    try {
                        bitmap = BitmapFactory.decodeStream(getApplicationContext().getContentResolver().openInputStream(uri));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    mImageView.setImageBitmap(bitmap);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG,80,stream);
                    imageByte = stream.toByteArray();
                }
            }
        });

        @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        //tell the application that tracking is done and not on paused
        tracking = false;
        onPause = false;

        //initialise all Views
        widgetInit();

        //initialise ViewModel
        mRunViewModel = new ViewModelProvider(this).get(RunViewModel.class);

        if(getIntent().hasExtra(EXTRA_ID)){
            id = getIntent().getIntExtra(EXTRA_ID, -1);
            mDistanceTextView.setText(getIntent().getStringExtra(EXTRA_DISTANCE));
            mDurationTextView.setText(getIntent().getStringExtra(EXTRA_DURATION));
            mDateTextView.setText(getIntent().getStringExtra(EXTRA_DATE));
            mSpeedTextView.setText(getIntent().getStringExtra(EXTRA_SPEED));
            mRunRatingBar.setRating(getIntent().getFloatExtra(EXTRA_RATING, 0));
            mRunCommentEditText.setText(getIntent().getStringExtra(EXTRA_COMMENT));
            Toast.makeText(this, getIntent().getStringExtra(EXTRA_IMAGE), Toast.LENGTH_SHORT).show();

            byte[] byteArray = getIntent().getByteArrayExtra(EXTRA_IMAGE);
            Bitmap compressedBitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
            mImageView.setImageBitmap(compressedBitmap);
    
            imageByte = getIntent().getByteArrayExtra(EXTRA_IMAGE);
            mDistance = getIntent().getStringExtra(EXTRA_DISTANCE) ;
            mDuration = getIntent().getStringExtra(EXTRA_DURATION);
            mDate = getIntent().getStringExtra(EXTRA_DATE);
            mSpeed = getIntent().getStringExtra(EXTRA_SPEED);
        }
        else if(getIntent().hasExtra("distance_from_record")){
            getRunResult();
        }
        mUploadButton.setOnClickListener(view -> {
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED){
                //No permission
                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permissions, IMAGE_PERMISSION_CODE);
            }
            else{
                getImage();
            }
        });

        //mDoneButton button listener
        mDoneButton.setOnClickListener(view -> {
            storeRunData();
            WorkoutSummaryActivity.super.onBackPressed();
        });
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
        getRunImage();
        if(getIntent().hasExtra("distance_from_record")){
            storeInRoomDatabase();
        }
        else if(getIntent().hasExtra(EXTRA_ID)){
            updateRoomDatabase();
        }
    }

    //Handle the event where users click black button instead of "done"
    //save and store all the data inside the room database
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        storeRunData();
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
    private void getRunImage() {
       //path = getImageFilePath(getApplicationContext(), mImageView.getU);
    }

    private void getRunComment() {
        mRunComment = String.valueOf(mRunCommentEditText.getText());
    }

    private void getRunRating() {
        mNumberOfStars = mRunRatingBar.getRating();
    }

    private void getRunResult() {
        Intent intent = getIntent();
        mDistanceTextView.setText(intent.getStringExtra("distance_from_record"));
        mDurationTextView.setText(intent.getStringExtra("duration"));
        mDateTextView.setText(intent.getStringExtra("date"));
        mSpeedTextView.setText(intent.getStringExtra("speed"));

        mDistance = intent.getStringExtra("distance_from_record") ;
        mDuration = intent.getStringExtra("duration");
        mDate = intent.getStringExtra("date");
        mSpeed = intent.getStringExtra("speed");
    }

    private void widgetInit() {
        mDoneButton = findViewById(R.id.doneButton);
        mUploadButton = findViewById(R.id.uploadImageButton);
        mDistanceTextView = findViewById(R.id.runDistance);
        mDurationTextView = findViewById(R.id.runDuration);
        mDateTextView = findViewById(R.id.runDate);
        mSpeedTextView = findViewById(R.id.runSpeed);
        mRunRatingBar = findViewById(R.id.ratingBar);
        mRunCommentEditText = findViewById(R.id.editText);
        mImageView = findViewById(R.id.imageView);
    }


}
