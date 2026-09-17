package com.shubham.app.meetingscheduler2.ideal.entity;

public class Room {

    private final int id;

    public Room(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Room{id=" + id + '}';
    }
}
