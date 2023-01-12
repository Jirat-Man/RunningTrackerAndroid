package com.example.runningtracker_manpadungkit.Ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

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

    RunViewModel mRunViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        widgetInit();

        getRunResult();

        //initialise ViewModel
        mRunViewModel = new ViewModelProvider(this).get(RunViewModel.class);

        //mRecordRunButton button listener
        mDoneButton.setOnClickListener(view -> {
            getRunRating();
            getRunComment();
            getRunImage();
            storeInRoomDatabase();
            finish();
        });
    }

    //Handle the event where the back button is pressed
    //save and store all the data inside the room database
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        getRunRating();
        getRunComment();
        getRunImage();
        storeInRoomDatabase();
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
        RunEntity run = new RunEntity(mDuration, Double.parseDouble(mDistance),
                mSpeed,mDate, mNumberOfStars, mRunComment,null);
        mRunViewModel.Insert(run);
    }

    private void getRunResult() {
        Intent intent = getIntent();
        mDistanceTextView.setText(intent.getStringExtra("distance"));
        mDurationTextView.setText(intent.getStringExtra("duration"));
        mDateTextView.setText(intent.getStringExtra("date"));
        mSpeedTextView.setText(intent.getStringExtra("speed"));

        mDistance = intent.getStringExtra("distance") ;
        mDuration = intent.getStringExtra("duration");
        mDate = intent.getStringExtra("date");
        mSpeed = intent.getStringExtra("speed");
    }

    private void widgetInit() {
        mDoneButton = findViewById(R.id.doneButton);
        mBrowseButton = findViewById(R.id.browseButton);
        mDistanceTextView = findViewById(R.id.runDistance);
        mDurationTextView = findViewById(R.id.runDuration);
        mDateTextView = findViewById(R.id.runDate);
        mSpeedTextView = findViewById(R.id.runSpeed);
        mRunRatingBar = findViewById(R.id.ratingBar);
        mRunCommentEditText = findViewById(R.id.editText);
    }
}
