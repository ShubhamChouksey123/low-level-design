package com.shubham.app.meetingscheduler2.ideal;

import com.shubham.app.meetingscheduler2.ideal.entity.Meeting;
import com.shubham.app.meetingscheduler2.ideal.entity.MeetingRoomBoard;
import com.shubham.app.meetingscheduler2.ideal.entity.User;
import com.shubham.app.meetingscheduler2.ideal.exception.InvalidMeetingException;
import com.shubham.app.meetingscheduler2.ideal.exception.NoRoomAvailableException;
import com.shubham.app.meetingscheduler2.ideal.service.FirstAvailableRoomAllocationStrategy;
import com.shubham.app.meetingscheduler2.ideal.service.MeetingRoomCreationService;
import com.shubham.app.meetingscheduler2.ideal.service.MeetingSchedulingService;

import java.time.Instant;

public class Main {

    public static void main(String[] args) {
        MeetingRoomBoard board = new MeetingRoomCreationService().createMeetingRooms(1,
                new FirstAvailableRoomAllocationStrategy());
        MeetingSchedulingService schedulingService = new MeetingSchedulingService(board);
        User user = new User(1, "Shubham");

        Meeting meetingA = schedulingService.bookMeeting(Instant.parse("2026-09-17T12:00:00Z"),
                Instant.parse("2026-09-17T13:59:00Z"), user);
        System.out.println("Booked: " + meetingA);

        // This slot wraps entirely around meetingA (11:30-14:15 contains 12:00-13:59) —
        // the
        // practice attempt's overlap check missed exactly this containment case.
        try {
            schedulingService.bookMeeting(Instant.parse("2026-09-17T11:30:00Z"), Instant.parse("2026-09-17T14:15:00Z"),
                    user);
        } catch (NoRoomAvailableException e) {
            System.out.println("Rejected wrapping slot: " + e.getMessage());
        }

        schedulingService.cancelMeeting(meetingA.getId());
        System.out.println("Cancelled meeting " + meetingA.getId());

        Meeting meetingB = schedulingService.bookMeeting(Instant.parse("2026-09-17T11:30:00Z"),
                Instant.parse("2026-09-17T14:15:00Z"), user);
        System.out.println("Booked after cancellation: " + meetingB);

        // meetingA.getId() was correctly assigned only once (the practice attempt's
        // AtomicInteger.get() bug meant every meeting shared id 0) — cancelling it a
        // second
        // time now correctly fails instead of silently matching whatever has id 0.
        try {
            schedulingService.cancelMeeting(meetingA.getId());
        } catch (InvalidMeetingException e) {
            System.out.println("Rejected re-cancel: " + e.getMessage());
        }
    }
}
