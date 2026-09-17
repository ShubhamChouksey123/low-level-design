package com.shubham.app.meetingscheduler2.practice.entity;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MeetingRoomSchedular {

    private List<Room> rooms;
    private List<Meeting> scheduledMeetings;
    private AtomicInteger atomicInteger;

    public MeetingRoomSchedular(List<Room> rooms) {
        this.rooms = rooms;
        this.scheduledMeetings = new LinkedList<>();
        this.atomicInteger = new AtomicInteger();
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public List<Meeting> getScheduledMeetings() {
        return scheduledMeetings;
    }

    public void setScheduledMeetings(List<Meeting> scheduledMeetings) {
        this.scheduledMeetings = scheduledMeetings;
    }

    public AtomicInteger getAtomicInteger() {
        return atomicInteger;
    }

    public void setAtomicInteger(AtomicInteger atomicInteger) {
        this.atomicInteger = atomicInteger;
    }
}
