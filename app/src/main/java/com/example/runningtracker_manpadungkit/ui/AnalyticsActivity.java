package com.example.runningtracker_manpadungkit.ui;

import static com.example.runningtracker_manpadungkit.Constants.EXTRA_COMMENT;
import static com.example.runningtracker_manpadungkit.Constants.EXTRA_DATE;
import static com.example.runningtracker_manpadungkit.Constants.EXTRA_DISTANCE;
import static com.example.runningtracker_manpadungkit.Constants.EXTRA_DURATION;
import static com.example.runningtracker_manpadungkit.Constants.EXTRA_ID;
import static com.example.runningtracker_manpadungkit.Constants.EXTRA_IMAGE;
import static com.example.runningtracker_manpadungkit.Constants.EXTRA_RATING;
import static com.example.runningtracker_manpadungkit.Constants.EXTRA_SPEED;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.runningtracker_manpadungkit.adapter.RunAdapter;
import com.example.runningtracker_manpadungkit.R;
import com.example.runningtracker_manpadungkit.RunViewModel;
import com.example.runningtracker_manpadungkit.databinding.ActivityAnalyticsBinding;

public class AnalyticsActivity extends AppCompatActivity {

    private String mNumOfRuns;  //total number of runs
    private double mTotalDistance;//total distance ran
    ActivityAnalyticsBinding mAnalyticsBinding;//data binding object

    RunViewModel mRunViewModel;
    RunAdapter adapter;

    //handle activity result from updating information in run history
    final ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAnalyticsBinding = DataBindingUtil.setContentView(this, R.layout.activity_analytics);//data binding

        this.setTitle("Run History");//set title of activity

        //set up recycler onto the page
        mAnalyticsBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAnalyticsBinding.recyclerView.setHasFixedSize(true);
        adapter = new RunAdapter();
        mAnalyticsBinding.recyclerView.setAdapter(adapter);


        //initialise runViewModel
        mRunViewModel = new ViewModelProvider(this).get(RunViewModel.class);
        //set all the card views to the runEntities in the database
        mRunViewModel.getAllRuns().observe(this, runEntities -> adapter.setRunEntities(runEntities));

        //get total number of runs
        mRunViewModel.getTotalNumOfRuns().observe(this, s -> mNumOfRuns = s);
        //there has to be more than 0 runs in the database in order to not trigger an error
            //get total distance ran
        mRunViewModel.getTotalDistance().observe(this, s ->{
            if(s != null){
                mTotalDistance = Math.round(Double.parseDouble(String.valueOf(s))*100d)/100d;
            }
        });

        //Slide to delete item from the database
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            // Create an alert dialog to ask user for confirmation of deletion
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AnalyticsActivity.this);
                builder.setMessage(R.string.confirm_delete_run)
                        .setTitle(R.string.delete_run)
                        .setPositiveButton(R.string.confirm, (dialog, id) -> {
                            mRunViewModel.Delete(adapter.getRunAt(viewHolder.getAdapterPosition()));
                            Toast.makeText(AnalyticsActivity.this, "Run Deleted", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton(R.string.cancel, (dialog, id) -> {
                            // Do nothing if user cancels;
                            mRunViewModel.Update(adapter.getRunAt(viewHolder.getAdapterPosition()));
                        });

                // Create the AlertDialog object and return it
                builder.create().show();
            }
        }).attachToRecyclerView(mAnalyticsBinding.recyclerView);

        //set up listener to handle item click event, it will launch workoutSummaryActivity
        //send necessary data as well
        adapter.setUpRunListener(runEntity -> {
            Intent intent = new Intent(AnalyticsActivity.this, WorkoutSummaryActivity.class);
            intent.putExtra(EXTRA_ID, runEntity.getId());
            intent.putExtra(EXTRA_DISTANCE, runEntity.getDistance());
            intent.putExtra(EXTRA_DURATION, runEntity.getDuration());
            intent.putExtra(EXTRA_SPEED, runEntity.getSpeed());
            intent.putExtra(EXTRA_DATE, runEntity.getDate());
            intent.putExtra(EXTRA_RATING, runEntity.getRating());
            intent.putExtra(EXTRA_COMMENT, runEntity.getComment());
            intent.putExtra(EXTRA_IMAGE, runEntity.getImage());
            startForResult.launch(intent);
        });
    }

    //inflate the menu with items
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.analytics_menu, menu);
        return true;
    }


    //handle menu click events
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //delete all runs
            case R.id.deleteAllRun:
                AlertDialog.Builder builder = new AlertDialog.Builder(AnalyticsActivity.this);
                builder.setMessage(R.string.confirm_delete_all_run)
                        .setTitle(R.string.delete_all_run)
                        .setPositiveButton(R.string.confirm, (dialog, id) -> {
                            mRunViewModel.DeleteAll();
                            Toast.makeText(AnalyticsActivity.this, "Run history deleted", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton(R.string.cancel, (dialog, id) -> {
                            //do nothing
                        });

                // Create the AlertDialog object and return it
                builder.create().show();
                return true;
            //sort runs by distance
            case R.id.sortDistance://sort runs wi
                Log.d("analytics", "distance ");
                mRunViewModel.getAllRunsByDistance().observe(this, runEntities -> adapter.setRunEntities(runEntities));
                return true;
            //sort runs by rating
            case R.id.sortRating:
                Log.d("analytics", "rating ");
                mRunViewModel.getAllRunsByRating().observe(this, runEntities -> adapter.setRunEntities(runEntities));
                return true;
            //sort runs by speed
            case R.id.sortSpeed:
                Log.d("analytics", "speed ");
                mRunViewModel.getAllRunsBySpeed().observe(this, runEntities -> adapter.setRunEntities(runEntities));
                return true;
            //sort runs by date
            case R.id.sortDate:
                Log.d("analytics", "date ");
                mRunViewModel.getAllRuns().observe(this, runEntities -> adapter.setRunEntities(runEntities));
                return true;
                //get fun fact of user's runs including total distance ran and number of runs
            case R.id.getFunFact:
                mRunViewModel.getTotalNumOfRuns().observe(this, s -> {
                    if(s != null){
                        mNumOfRuns = s;
                    }
                    else{
                        mNumOfRuns = "0";
                    }
                });
                mRunViewModel.getTotalDistance().observe(this, d -> {
                    if(d != null){
                        mTotalDistance = Math.round(Double.parseDouble(String.valueOf(d))*100d)/100d;
                    }
                    else{
                        mTotalDistance = 0;
                    }
                });

                openDialog(mNumOfRuns, mTotalDistance);//show dialog
                return true;
            default:
                Log.d("analytics", "default ");
                mRunViewModel.getAllRuns().observe(this, runEntities -> adapter.setRunEntities(runEntities));
                return super.onOptionsItemSelected(item);
        }
    }

    //Show a dialog of the fun fact
    private void openDialog(String mNumOfRuns, double mTotalDistance) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AnalyticsActivity.this);
        builder.setTitle("Fun Fact!")
                .setMessage("You have done " + mNumOfRuns + " runs!" + " Totalling " + mTotalDistance + " km!")
                .setPositiveButton("ok", (dialog, which) -> {
                    //do nothing
                });
        builder.show();
    }
}