package com.example.runningtracker_manpadungkit.Room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

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

    @Query("SELECT * FROM run_record ORDER BY run_id DESC")
    LiveData<List<RunEntity>> getAllRuns();
}
