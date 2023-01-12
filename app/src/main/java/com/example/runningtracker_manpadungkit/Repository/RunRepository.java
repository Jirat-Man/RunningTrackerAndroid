package com.example.runningtracker_manpadungkit.Repository;

import android.app.Application;

import androidx.lifecycle.LiveData;
import 	android.os.AsyncTask;

import com.example.runningtracker_manpadungkit.Room.RunDao;
import com.example.runningtracker_manpadungkit.Room.RunEntity;
import com.example.runningtracker_manpadungkit.Room.RunRoomDatabase;


import java.util.List;

public class RunRepository {
    private RunDao mRunDao;
    private LiveData<List<RunEntity>> mAllRun;

    public RunRepository(Application application) {
        RunRoomDatabase db = RunRoomDatabase.getInstance(application);
        mRunDao = db.runDao();
        mAllRun = mRunDao.getAllRuns();
    }

    public void Insert(RunEntity runEntity) {
        new insertAsyncTask(mRunDao).execute(runEntity);
    }
    public void Update(RunEntity runEntity) {
        new UpdateRunAsyncTask(mRunDao).execute(runEntity);
    }
    public void Delete(RunEntity runEntity) {
        RunRoomDatabase.databaseWriteExecutor.execute(() -> {
            mRunDao.delete(runEntity);
        });
    }
    public void DeleteAll() {
        RunRoomDatabase.databaseWriteExecutor.execute(() -> {
            mRunDao.deleteAll();
        });
    }
    public LiveData<List<RunEntity>> GetAllRuns() {
        return mAllRun;
    }

    private static class insertAsyncTask extends AsyncTask<RunEntity,Void,Void>{

        private RunDao runDao;

        insertAsyncTask(RunDao taskDao){
            runDao = taskDao;
        }
        @Override
        protected Void doInBackground(RunEntity... runEntities) {
            runDao.insert(runEntities[0]);
            return null;
        }
    }

    private static class UpdateRunAsyncTask extends AsyncTask<RunEntity, Void, Void> {
        private RunDao runDao;

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
