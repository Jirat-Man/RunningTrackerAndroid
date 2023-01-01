package com.example.runningtracker_manpadungkit.Room;

import android.media.Image;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "run_record")       //Name of table
public class RunEntity {

    //Different Data Stored in the table
    @PrimaryKey(autoGenerate = true)//auto generate ID
    @NonNull
    @ColumnInfo(name = "run_id")
    private int id;

    @NonNull
    @ColumnInfo(name = "run_duration")
    private float duration;

    @ColumnInfo(name = "run_distance")
    @NonNull
    private float distance;

    @ColumnInfo(name = "calories")
    @NonNull
    private int Calories;

    @ColumnInfo(name = "run_date")
    @NonNull
    private float time;

    private String name;
    private int rating;
    private String comment;

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private byte[] image;

    //Setter for  Run Id
    public void setId(int id) {
        this.id = id;
    }

    //entity Constructor
    public RunEntity(float duration, float distance, int Calories, float time, String name, int rating, String comment, byte[] image) {
        this.duration = duration;
        this.distance = distance;
        this.time = time;
        this.name = name;
        this.rating = rating;
        this.comment = comment;
        this.image = image;
    }


    //Different Getters
    public int getCalories() {
        return Calories;
    }
    public int getId() {
        return id;
    }

    public float getDuration() {
        return duration;
    }

    public float getDistance() {
        return distance;
    }

    public float getTime() {
        return time;
    }

    public String getName() {
        return name;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public byte[] getImage() {
        return image;
    }
}
