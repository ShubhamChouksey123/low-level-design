package com.shubham.app.meetingscheduler2.practice.entity;

import java.time.Instant;

public class Meeting {

    private int id;
    private Instant startTime;
    private Instant endTime;
    private Instant createdAt;
    private int createdByUserId;
    private Room room;

    public Meeting(int id, Instant startTime, Instant endTime, Instant createdAt, int createdByUserId, Room room) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createdAt = createdAt;
        this.createdByUserId = createdByUserId;
        this.room = room;
    }

    public int getId() {
        return id;
    }

    public Room getRoom() {
        return room;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return "Meeting{" + "id=" + id + ", startTime=" + startTime + ", endTime=" + endTime + ", createdAt="
                + createdAt + ", createdByUserId=" + createdByUserId + ", room=" + room + '}';
    }
}
