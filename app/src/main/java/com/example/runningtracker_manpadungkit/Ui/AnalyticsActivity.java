package com.example.runningtracker_manpadungkit.Ui;

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
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import com.example.runningtracker_manpadungkit.Adapter.RunAdapter;
import com.example.runningtracker_manpadungkit.R;
import com.example.runningtracker_manpadungkit.RunViewModel;

public class AnalyticsActivity extends AppCompatActivity {

    private String mNumOfRuns;
    private double mTotalDistance;

    public static RunViewModel mRunViewModel;
    RunAdapter adapter;

    //handle result from updating information in run history
    ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.runningtracker_manpadungkit.databinding.ActivityAnalyticsBinding mAnalyticsBinding = DataBindingUtil.setContentView(this, R.layout.activity_analytics);

        this.setTitle("Run History");



        mAnalyticsBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAnalyticsBinding.recyclerView.setHasFixedSize(true);
        adapter = new RunAdapter();
        mAnalyticsBinding.recyclerView.setAdapter(adapter);


        mRunViewModel = new ViewModelProvider(this).get(RunViewModel.class);

        mRunViewModel.getAllRuns().observe(this, runEntities -> adapter.setRunEntities(runEntities));
        mRunViewModel.getTotalDistance().observe(this, s -> mTotalDistance = Double.parseDouble(String.valueOf(s)));
        mRunViewModel.getTotalNumOfRuns().observe(this, s -> mNumOfRuns = s);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.analytics_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
            case R.id.sortDistance:
                Log.d("analytics", "distance ");
                mRunViewModel.getAllRunsByDistance().observe(this, runEntities -> adapter.setRunEntities(runEntities));
                return true;
            case R.id.sortRating:
                Log.d("analytics", "rating ");
                mRunViewModel.getAllRunsByRating().observe(this, runEntities -> adapter.setRunEntities(runEntities));
                return true;
            case R.id.sortSpeed:
                Log.d("analytics", "speed ");
                mRunViewModel.getAllRunsBySpeed().observe(this, runEntities -> adapter.setRunEntities(runEntities));
                return true;
            case R.id.sortDate:
                Log.d("analytics", "date ");
                mRunViewModel.getAllRuns().observe(this, runEntities -> adapter.setRunEntities(runEntities));
                return true;
            case R.id.getFunFact:
                mRunViewModel.getTotalDistance().observe(this, s -> mTotalDistance = Double.parseDouble(String.valueOf(s)));
                mRunViewModel.getTotalNumOfRuns().observe(this, s -> mNumOfRuns = s);
                openDialog(mNumOfRuns, mTotalDistance);
                return true;
            default:
                Log.d("analytics", "default ");
                mRunViewModel.getAllRuns().observe(this, runEntities -> adapter.setRunEntities(runEntities));
                return super.onOptionsItemSelected(item);
        }
    }

    private void openDialog(String mNumOfRuns, double mTotalDistance) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AnalyticsActivity.this);
        builder.setTitle("Fun Fact!")
                .setMessage("You have done " + mNumOfRuns + " runs!"+" Totalling " + mTotalDistance + " km!")
                .setPositiveButton("ok", (dialog, which) -> {
                    //do nothing
                });
        builder.show();
    }
}