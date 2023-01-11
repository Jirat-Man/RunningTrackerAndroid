package com.example.runningtracker_manpadungkit.Room;

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
    private String duration;

    @ColumnInfo(name = "run_distance")
    @NonNull
    private double distance;

    @ColumnInfo(name = "speed")
    @NonNull
    private String speed;

    @ColumnInfo(name = "run_date")
    @NonNull
    private String date;

    private float rating;
    private String comment;

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private byte[] image;

    //Setter for  Run Id
    public void setId(int id) {
        this.id = id;
    }

    //entity Constructor
    public RunEntity(@NonNull String duration, double distance, String speed, @NonNull String date, float rating, String comment, byte[] image) {
        this.duration = duration;
        this.distance = distance;
        this.speed = speed;
        this.date = date;
        this.rating = rating;
        this.comment = comment;
        this.image = image;
    }


    //Different Getters
    public String getSpeed() {
        return speed;
    }
    public int getId() {
        return id;
    }

    @NonNull
    public String getDuration() {
        return duration;
    }

    public double getDistance() {
        return distance;
    }

    @NonNull
    public String getDate() {
        return date;
    }

    public float getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public byte[] getImage() {
        return image;
    }
}
