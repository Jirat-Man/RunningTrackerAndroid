package com.example.runningtracker_manpadungkit.Ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.runningtracker_manpadungkit.Adapter.RunAdapter;
import com.example.runningtracker_manpadungkit.R;
import com.example.runningtracker_manpadungkit.Room.RunDao;
import com.example.runningtracker_manpadungkit.Room.RunEntity;
import com.example.runningtracker_manpadungkit.ViewModel.RunViewModel;

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

    }
}