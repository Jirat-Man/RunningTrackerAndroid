package com.example.runningtracker_manpadungkit;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.runningtracker_manpadungkit.Repository.RunRepository;
import com.example.runningtracker_manpadungkit.Room.RunEntity;

import java.util.List;

public class RunViewModel extends AndroidViewModel {

    private RunRepository mRepository;

    private LiveData<List<RunEntity>> mAllRuns;

    public RunViewModel(Application application) {
        super(application);
        init(application);
    }

    public void init(Application application) {
        mRepository = new RunRepository(application);
    }

    //return all runs sorted by rating
    public LiveData<List<RunEntity>> getAllRunsByRating() {
        this.mAllRuns = mRepository.GetAllRunsByRating();
        return mAllRuns;
    }

    //return all runs sorted by distance
    public LiveData<List<RunEntity>> getAllRunsByDistance() {
        this.mAllRuns = mRepository.GetAllRunsByDistance();
        return mAllRuns;
    }

    //return all runs sorted by Speed
    public LiveData<List<RunEntity>> getAllRunsBySpeed() {
        this.mAllRuns = mRepository.GetAllRunsBySpeed();
        return mAllRuns;
    }

    //return all runs sorted by date
    public LiveData<List<RunEntity>> getAllRuns() {
        this.mAllRuns = mRepository.GetAllRuns();
        return mAllRuns;
    }

    //get total distance ran
    public LiveData<Double> getTotalDistance() {
        return mRepository.GetTotalDistance();
    }

    //get total number of runs
    public LiveData<String> getTotalNumOfRuns() {
        return mRepository.GetNumOfRuns();
    }

    //insert run entity into database
    public void Insert(RunEntity runEntity) {
        mRepository.Insert(runEntity);
    }

    //Update run entity into database
    public void Update(RunEntity runEntity) {
        mRepository.Update(runEntity);
    }

    //Delete run entity into database
    public void Delete(RunEntity runEntity) {
        mRepository.Delete(runEntity);
    }

    //DeleteAll run entity into database
    public void DeleteAll() {
        mRepository.DeleteAll();
    }
}
