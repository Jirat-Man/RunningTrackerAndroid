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
        //this.mAllRuns = mRepository.GetAllRuns();
    }

    public void init(Application application) {
        mRepository = new RunRepository(application);
    }

    public LiveData<List<RunEntity>> getAllRunsByRating() {
        this.mAllRuns = mRepository.GetAllRunsByRating();
        return mAllRuns;
    }

    public LiveData<List<RunEntity>> getAllRunsByDistance() {
        this.mAllRuns = mRepository.GetAllRunsByDistance();
        return mAllRuns;
    }

    public LiveData<List<RunEntity>> getAllRunsBySpeed() {
        this.mAllRuns = mRepository.GetAllRunsBySpeed();
        return mAllRuns;
    }

    public LiveData<List<RunEntity>> getAllRuns() {
        this.mAllRuns = mRepository.GetAllRuns();
        return mAllRuns;
    }

    public LiveData<Double> getTotalDistance() {
        return mRepository.GetTotalDistance();
    }

    public LiveData<String> getTotalNumOfRuns() {
        return mRepository.GetNumOfRuns();
    }

    public void Insert(RunEntity runEntity) {
        mRepository.Insert(runEntity);
    }

    public void Update(RunEntity runEntity) {
        mRepository.Update(runEntity);
    }

    public void Delete(RunEntity runEntity) {
        mRepository.Delete(runEntity);
    }

    public void DeleteAll() {
        mRepository.DeleteAll();
    }
}
