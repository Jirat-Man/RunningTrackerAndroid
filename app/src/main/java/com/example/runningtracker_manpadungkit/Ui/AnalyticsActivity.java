package com.example.runningtracker_manpadungkit.Ui;

import static com.example.runningtracker_manpadungkit.Constants.RUN_RESULT_CODE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.runningtracker_manpadungkit.Adapter.RunAdapter;
import com.example.runningtracker_manpadungkit.R;
import com.example.runningtracker_manpadungkit.Room.RunEntity;
import com.example.runningtracker_manpadungkit.RunViewModel;

import java.util.List;

public class AnalyticsActivity extends AppCompatActivity {

    public static RunViewModel mRunViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        RunAdapter adapter = new RunAdapter();
        recyclerView.setAdapter(adapter);


        mRunViewModel = new ViewModelProvider(this).get(RunViewModel.class);

        mRunViewModel.getAllRuns().observe(this, new Observer<List<RunEntity>>() {
            @Override
            public void onChanged(List<RunEntity> runEntities) {
                adapter.setRunEntities(runEntities);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT ) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AnalyticsActivity.this);
                builder.setMessage(R.string.confirm_delete_run)
                        .setTitle(R.string.delete_run)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mRunViewModel.Delete(adapter.getRunAt(viewHolder.getAdapterPosition()));
                                Toast.makeText(AnalyticsActivity.this, "Run Deleted", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Do nothing if user cancels;
                                mRunViewModel.Update(adapter.getRunAt(viewHolder.getAdapterPosition()));
                            }
                        });

                // Create the AlertDialog object and return it
                builder.create().show();
            }
        }).attachToRecyclerView(recyclerView);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.analytics_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.deleteAllRun:
                mRunViewModel.DeleteAll();
                Toast.makeText(this, "Run history deleted", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}