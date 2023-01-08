package com.example.runningtracker_manpadungkit.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.runningtracker_manpadungkit.R;
import com.example.runningtracker_manpadungkit.Room.RunEntity;

import java.util.ArrayList;
import java.util.List;

public class RunAdapter extends RecyclerView.Adapter<RunAdapter.RunHolder> {

    private List<RunEntity> runEntities = new ArrayList<>();
    
    @NonNull
    @Override
    public RunHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.run_card_view, parent, false);
        return new RunHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RunHolder holder, int position) {
        RunEntity currentRun = runEntities.get(position);
        holder.mRunDistanceView.setText(String.valueOf(currentRun.getDistance()));
        holder.mRunDurationView.setText(String.valueOf(currentRun.getDuration()));
        holder.mRunRatingView.setText(String.valueOf(currentRun.getRating()));
        holder.mRunCommentView.setText(currentRun.getComment());
        holder.mRunSpeedView.setText(String.valueOf(currentRun.getSpeed()));
        holder.mRunDateView.setText(String.valueOf(currentRun.getDate()));
    }

    @Override
    public int getItemCount() {
        return runEntities.size();
    }

    public void setRunEntities(List<RunEntity> runEntities){
        this.runEntities = runEntities;
        notifyDataSetChanged();
    }

    class RunHolder extends RecyclerView.ViewHolder{

        private TextView mRunDistanceView;
        private TextView mRunDurationView;
        private TextView mRunDateView;
        private TextView mRunSpeedView;
        private TextView mRunRatingView;
        private TextView mRunCommentView;

        public RunHolder(@NonNull View itemView) {
            super(itemView);
            widgetInit();
        }

        private void widgetInit() {
            mRunDistanceView = itemView.findViewById(R.id.runDistance);
            mRunDurationView = itemView.findViewById(R.id.runDuration);
            mRunDateView = itemView.findViewById(R.id.runDate);
            mRunSpeedView = itemView.findViewById(R.id.runSpeed);
            mRunRatingView = itemView.findViewById(R.id.runRating);
            mRunCommentView = itemView.findViewById(R.id.runComment);
        }
    }
}
