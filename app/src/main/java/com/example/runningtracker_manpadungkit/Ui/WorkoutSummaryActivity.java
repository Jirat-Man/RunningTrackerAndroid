package com.example.runningtracker_manpadungkit.Ui;

import static com.example.runningtracker_manpadungkit.Constants.EXTRA_COMMENT;
import static com.example.runningtracker_manpadungkit.Constants.EXTRA_DATE;
import static com.example.runningtracker_manpadungkit.Constants.EXTRA_DISTANCE;
import static com.example.runningtracker_manpadungkit.Constants.EXTRA_DURATION;
import static com.example.runningtracker_manpadungkit.Constants.EXTRA_ID;
import static com.example.runningtracker_manpadungkit.Constants.EXTRA_RATING;
import static com.example.runningtracker_manpadungkit.Constants.EXTRA_SPEED;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.runningtracker_manpadungkit.R;
import com.example.runningtracker_manpadungkit.Room.RunEntity;
import com.example.runningtracker_manpadungkit.RunViewModel;

public class WorkoutSummaryActivity extends AppCompatActivity {

    Button mDoneButton;
    Button mBrowseButton;
    TextView mDistanceTextView;
    TextView mDurationTextView;
    TextView mDateTextView;
    TextView mSpeedTextView;

    EditText mRunCommentEditText;
    String mRunComment;

    RatingBar mRunRatingBar;
    float mNumberOfStars;

    String mDistance;
    String mDuration;
    String mDate;
    String mSpeed;
    int id;

    RunViewModel mRunViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

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


            mDistance = getIntent().getStringExtra(EXTRA_DISTANCE) ;
            mDuration = getIntent().getStringExtra(EXTRA_DURATION);
            mDate = getIntent().getStringExtra(EXTRA_DATE);
            mSpeed = getIntent().getStringExtra(EXTRA_SPEED);
        }
        else if(getIntent().hasExtra("distance_from_record")){
            //Toast.makeText(this, "here", Toast.LENGTH_SHORT).show();
            getRunResult();
        }
        mBrowseButton.setOnClickListener(view -> {
        });

        //mDoneButton button listener
        mDoneButton.setOnClickListener(view -> {
            WorkoutSummaryActivity.super.onBackPressed();
        });
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

    //Handle the event where the activity is destroyed
    //save and store all the data inside the room database
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        storeRunData();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        storeRunData();
    }

    private void updateRoomDatabase() {
        RunEntity run = new RunEntity(mDuration, mDistance,
                mSpeed,mDate, mNumberOfStars, mRunComment,null);
        run.setId(id);
        mRunViewModel.Update(run);
    }

    private void getRunImage() {

    }

    private void getRunComment() {
        mRunComment = String.valueOf(mRunCommentEditText.getText());
    }

    private void getRunRating() {
        mNumberOfStars = mRunRatingBar.getRating();
    }

    private void storeInRoomDatabase() {
        RunEntity run = new RunEntity(mDuration, mDistance,
                mSpeed,mDate, mNumberOfStars, mRunComment,null);
        Toast.makeText(this, "here", Toast.LENGTH_SHORT).show();
        mRunViewModel.Insert(run);
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
        mBrowseButton = findViewById(R.id.uploadImageButton);
        mDistanceTextView = findViewById(R.id.runDistance);
        mDurationTextView = findViewById(R.id.runDuration);
        mDateTextView = findViewById(R.id.runDate);
        mSpeedTextView = findViewById(R.id.runSpeed);
        mRunRatingBar = findViewById(R.id.ratingBar);
        mRunCommentEditText = findViewById(R.id.editText);
    }


}
