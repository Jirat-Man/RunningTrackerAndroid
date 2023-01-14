package com.example.runningtracker_manpadungkit.Repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import android.os.AsyncTask;

import com.example.runningtracker_manpadungkit.Room.RunDao;
import com.example.runningtracker_manpadungkit.Room.RunEntity;
import com.example.runningtracker_manpadungkit.Room.RunRoomDatabase;


import java.util.List;

public class RunRepository {
    private final RunDao mRunDao;
    private LiveData<List<RunEntity>> mAllRun;

    //repository constructor
    public RunRepository(Application application) {
        RunRoomDatabase db = RunRoomDatabase.getInstance(application);
        mRunDao = db.runDao();
    }

    //notify runDao to Insert runEntity into rommDatabase
    public void Insert(RunEntity runEntity) {
        new insertAsyncTask(mRunDao).execute(runEntity);
    }

    //notify runDao to Update runEntity into rommDatabase
    public void Update(RunEntity runEntity) {
        new UpdateRunAsyncTask(mRunDao).execute(runEntity);
    }

    //notify runDao to Delete runEntity into rommDatabase
    public void Delete(RunEntity runEntity) {
        RunRoomDatabase.databaseWriteExecutor.execute(() -> mRunDao.delete(runEntity));
    }

    //notify runDao to Delete all runEntities
    public void DeleteAll() {
        RunRoomDatabase.databaseWriteExecutor.execute(mRunDao::deleteAll);
    }

    public LiveData<List<RunEntity>> GetAllRuns() {
        mAllRun = mRunDao.getAllRuns();
        return mAllRun;
    }

    //return all runs sorted by rating
    public LiveData<List<RunEntity>> GetAllRunsByRating() {
        mAllRun = mRunDao.getAllRunsByRating();
        return mAllRun;
    }

    //return all runs sorted by distance
    public LiveData<List<RunEntity>> GetAllRunsByDistance() {
        mAllRun = mRunDao.getAllRunsByDistance();
        return mAllRun;
    }

    //return all runs sorted by speed
    public LiveData<List<RunEntity>> GetAllRunsBySpeed() {
        mAllRun = mRunDao.getAllRunsBySpeed();
        return mAllRun;
    }

    //return total distance ran
    public LiveData<Double> GetTotalDistance() {
        return mRunDao.getSumOfDistance();
    }

    //return total number of runs
    public LiveData<String> GetNumOfRuns() {
        return mRunDao.getNumOfRuns();
    }


    //Insert runEntity in Background
    private static class insertAsyncTask extends AsyncTask<RunEntity, Void, Void> {
        private final RunDao runDao;

        insertAsyncTask(RunDao taskDao) {
            runDao = taskDao;
        }

        @Override
        protected Void doInBackground(RunEntity... runEntities) {
            runDao.insert(runEntities[0]);
            return null;
        }
    }

    //Update runEntity in Background
    private static class UpdateRunAsyncTask extends AsyncTask<RunEntity, Void, Void> {
        private final RunDao runDao;

        UpdateRunAsyncTask(RunDao runDao) {
            this.runDao = runDao;
        }

        @Override
        protected Void doInBackground(RunEntity... runEntities) {
            runDao.update(runEntities[0]);
            return null;
        }
    }
}
