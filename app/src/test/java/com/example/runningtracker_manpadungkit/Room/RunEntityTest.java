package com.example.runningtracker_manpadungkit.Room;

import static org.junit.Assert.*;
import org.junit.Test;

public class RunEntityTest {

    @Test
    public void getSpeed() {
        RunEntity runEntity = new RunEntity("00:30:00", 5,6, "01-05-2022", 4, "Love running", null);
        assertEquals(6, runEntity.getSpeed(), 0);
    }

    @Test
    public void getDuration() {
        RunEntity runEntity = new RunEntity("00:30:00", 5,6, "01-05-2022", 4, "Love running", null);
        assertEquals("duration", runEntity.getDuration(), "00:30:00");
    }

    @Test
    public void getDistance() {
        RunEntity runEntity = new RunEntity("00:30:00", 5,6, "01-05-2022", 4, "Love running", null);
        assertEquals(5, runEntity.getDistance(), 0);
    }

    @Test
    public void getDate() {
        RunEntity runEntity = new RunEntity("00:30:00", 5,6, "01-05-2022", 4, "Love running", null);
        assertEquals("date", runEntity.getDate(), "01-05-2022");
    }

    @Test
    public void getRating() {
        RunEntity runEntity = new RunEntity("00:30:00", 5,6, "01-05-2022", 4, "Love running", null);
        assertEquals(4, runEntity.getRating(), 0);
    }

    @Test
    public void getComment() {
        RunEntity runEntity = new RunEntity("00:30:00", 5,6, "01-05-2022", 4, "Love running", null);
        assertEquals("comment", runEntity.getComment(), "Love running");
    }
}