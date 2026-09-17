package com.shubham.app.meetingscheduler2.practice.entity;

public class Room {

    private int id;

    public Room(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Room{" + "id=" + id + '}';
    }
}
