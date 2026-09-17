package com.shubham.app.meetingscheduler2.practice.exception;

public class InvalidMeetingRoom extends RuntimeException {
    public InvalidMeetingRoom(String message) {
        super(message);
    }
}
