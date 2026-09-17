package com.shubham.app.meetingscheduler2.ideal.entity;

public class Meeting {

    private final int id;
    private final TimeSlot timeSlot;
    private final Room room;
    private final int bookedByUserId;

    public Meeting(int id, TimeSlot timeSlot, Room room, int bookedByUserId) {
        this.id = id;
        this.timeSlot = timeSlot;
        this.room = room;
        this.bookedByUserId = bookedByUserId;
    }

    public int getId() {
        return id;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public Room getRoom() {
        return room;
    }

    public int getBookedByUserId() {
        return bookedByUserId;
    }

    @Override
    public String toString() {
        return "Meeting{id=" + id + ", timeSlot=" + timeSlot + ", room=" + room + ", bookedByUserId=" + bookedByUserId
                + '}';
    }
}
