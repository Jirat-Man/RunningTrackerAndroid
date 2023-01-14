package com.example.runningtracker_manpadungkit.Room;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "run_record")       //Name of table
public class RunEntity {

    //Different Data Stored in the table
    @PrimaryKey(autoGenerate = true)//auto generate ID
    @ColumnInfo(name = "run_id")
    private int id;

    @ColumnInfo(name = "run_duration")
    private final String duration;

    @ColumnInfo(name = "run_distance")
    private final double distance;

    @ColumnInfo(name = "speed")
    private final double speed;

    @ColumnInfo(name = "run_date")
    private final String date;

    private final float rating;
    private final String comment;

    @ColumnInfo(name = "run_image")
    private final byte[] image;



    //Setter for  Run Id
    public void setId(int id) {
        this.id = id;
    }



    //entity Constructor
    public RunEntity(String duration, double distance, double speed,String date, float rating, String comment, byte[] image) {
        this.duration = duration;
        this.distance = distance;
        this.speed = speed;
        this.date = date;
        this.rating = rating;
        this.comment = comment;
        this.image = image;
    }


    //Different Getters
    public double getSpeed() {
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
