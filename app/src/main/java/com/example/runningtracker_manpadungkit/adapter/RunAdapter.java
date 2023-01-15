package com.example.runningtracker_manpadungkit.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.runningtracker_manpadungkit.R;
import com.example.runningtracker_manpadungkit.room.RunEntity;

import java.util.ArrayList;
import java.util.List;

//This class fills up the recyclerView with cardviews that has data from, the room database
public class RunAdapter extends RecyclerView.Adapter<RunAdapter.RunHolder> {

    private List<RunEntity> runEntities = new ArrayList<>();
    private onRunClickListener listener;

    //inflate view holder with the cardviews
    @NonNull
    @Override
    public RunHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.run_card_view, parent, false);
        return new RunHolder(itemView);
    }

    //set the textview with the data from room database
    @Override
    public void onBindViewHolder(@NonNull RunHolder holder, int position) {
        RunEntity currentRun = runEntities.get(position);
        holder.mRunDistanceView.setText(String.valueOf(currentRun.getDistance()));
        holder.mRunDurationView.setText(currentRun.getDuration());
        holder.mRunRatingView.setText(String.valueOf(currentRun.getRating()));
        holder.mRunCommentView.setText(String.valueOf(currentRun.getComment()));
        holder.mRunSpeedView.setText(String.valueOf(currentRun.getSpeed()));
        holder.mRunDateView.setText(currentRun.getDate());
    }

    //return number of runEntities
    @Override
    public int getItemCount() {
        return runEntities.size();
    }

    //set the runEntity and notify database
    @SuppressLint("NotifyDataSetChanged")
    public void setRunEntities(List<RunEntity> runEntities) {
        this.runEntities = runEntities;
        notifyDataSetChanged();
    }

    //return runEntity at a given position
    public RunEntity getRunAt(int position) {
        return runEntities.get(position);
    }


    //Initialise the recycler view of cardviews
    class RunHolder extends RecyclerView.ViewHolder {

        private TextView mRunDistanceView;
        private TextView mRunDurationView;
        private TextView mRunDateView;
        private TextView mRunSpeedView;
        private TextView mRunRatingView;
        private TextView mRunCommentView;

        public RunHolder(@NonNull View itemView) {
            super(itemView);
            widgetInit();

            //click listener for when users want to click on the card view and see run details
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onRunClick(runEntities.get(position));
                }
            });
        }

        //initialise all views
        private void widgetInit() {
            mRunDistanceView = itemView.findViewById(R.id.runDistance);
            mRunDurationView = itemView.findViewById(R.id.runDuration);
            mRunDateView = itemView.findViewById(R.id.runDate);
            mRunSpeedView = itemView.findViewById(R.id.runSpeed);
            mRunRatingView = itemView.findViewById(R.id.runRating);
            mRunCommentView = itemView.findViewById(R.id.runComment);
        }
    }

    public interface onRunClickListener {
        void onRunClick(RunEntity runEntity);
    }

    public void setUpRunListener(onRunClickListener listener) {
        this.listener = listener;
    }
}
