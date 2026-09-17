package com.shubham.app.meetingscheduler2.practice;

import com.shubham.app.meetingscheduler2.practice.entity.Meeting;
import com.shubham.app.meetingscheduler2.practice.entity.MeetingRoomSchedular;
import com.shubham.app.meetingscheduler2.practice.entity.User;
import com.shubham.app.meetingscheduler2.practice.service.MeetingRoomAllocationService;
import com.shubham.app.meetingscheduler2.practice.service.MeetingRoomCreationService;

import java.time.Instant;

public class Main {

    public static void main(String[] args) {

        MeetingRoomCreationService meetingRoomCreationService = new MeetingRoomCreationService();
        MeetingRoomSchedular meetingRoomSchedular = meetingRoomCreationService.createMeetingRoom(5);

        MeetingRoomAllocationService meetingRoomAllocationService = new MeetingRoomAllocationService(
                meetingRoomSchedular);

        User user = new User(1, "Shubham");
        meetingRoomAllocationService.createMeeting(1, Instant.parse("2026-09-17T10:00:00Z"),
                Instant.parse("2026-09-17T10:59:00Z"), user);

        meetingRoomAllocationService.createMeeting(1, Instant.parse("2026-09-17T12:00:00Z"),
                Instant.parse("2026-09-17T13:59:00Z"), user);

        printAllMeetings(meetingRoomSchedular);
        try {
            meetingRoomAllocationService.createMeeting(1, Instant.parse("2026-09-17T12:30:00Z"),
                    Instant.parse("2026-09-17T13:00:00Z"), user);
        } catch (Exception e) {
            System.out.println("Error : " + e.getMessage());
        }
    }

    private static void printAllMeetings(MeetingRoomSchedular meetingRoomSchedular) {
        for (Meeting scheduledMeeting : meetingRoomSchedular.getScheduledMeetings()) {
            System.out.println(scheduledMeeting);
        }
    }
}
