package com.example.runningtracker_manpadungkit.Room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

//Holds different methods to extract various data from RoomDatabase
@Dao
public interface RunDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(RunEntity journey);

    @Update
    void update(RunEntity journey);

    @Delete
    void delete(RunEntity journey);

    @Query("DELETE FROM run_record")
    void deleteAll();

    @Query("SELECT SUM(run_distance) FROM run_record")
    LiveData<Double> getSumOfDistance();

    @Query("SELECT COUNT(run_id) FROM run_record")
    LiveData<String> getNumOfRuns();

    @Query("SELECT * FROM run_record ORDER BY run_id DESC")
    LiveData<List<RunEntity>> getAllRuns();

    @Query("SELECT * FROM run_record ORDER BY rating DESC")
    LiveData<List<RunEntity>> getAllRunsByRating();

    @Query("SELECT * FROM run_record ORDER BY speed ASC")
    LiveData<List<RunEntity>> getAllRunsBySpeed();

    @Query("SELECT * FROM run_record ORDER BY run_distance DESC")
    LiveData<List<RunEntity>> getAllRunsByDistance();


}
