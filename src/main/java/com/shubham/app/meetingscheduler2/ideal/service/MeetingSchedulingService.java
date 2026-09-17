package com.shubham.app.meetingscheduler2.ideal.service;

import com.shubham.app.meetingscheduler2.ideal.entity.Meeting;
import com.shubham.app.meetingscheduler2.ideal.entity.MeetingRoomBoard;
import com.shubham.app.meetingscheduler2.ideal.entity.TimeSlot;
import com.shubham.app.meetingscheduler2.ideal.entity.User;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

public class MeetingSchedulingService {

    private final MeetingRoomBoard board;
    private final AtomicInteger nextMeetingId = new AtomicInteger(0);

    public MeetingSchedulingService(MeetingRoomBoard board) {
        this.board = board;
    }

    public Meeting bookMeeting(Instant start, Instant end, User user) {
        TimeSlot requested = new TimeSlot(start, end);
        return board.book(nextMeetingId.getAndIncrement(), requested, user.getId());
    }

    public void cancelMeeting(int meetingId) {
        board.cancel(meetingId);
    }
}
