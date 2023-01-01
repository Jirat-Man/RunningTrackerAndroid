package com.example.runningtracker_manpadungkit.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;
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
        this.mAllRuns = mRepository.GetAllRuns();
    }

    public void init(Application application){
        mRepository = new RunRepository(application);
    }
    public LiveData<List<RunEntity>> getAllRuns() {
        return mAllRuns;
    }
    public void Insert(RunEntity runEntity){
        mRepository.Insert(runEntity);
    }
    public void Update(RunEntity runEntity){
        mRepository.Update(runEntity);
    }
    public void Delete(RunEntity runEntity){
        mRepository.Delete(runEntity);
    }
    public void DeleteAll(){
        mRepository.DeleteAll();
    }

}
