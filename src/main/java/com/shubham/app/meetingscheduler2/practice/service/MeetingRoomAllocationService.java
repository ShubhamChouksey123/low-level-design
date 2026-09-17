package com.shubham.app.meetingscheduler2.practice.service;

import com.shubham.app.meetingscheduler2.practice.entity.Meeting;
import com.shubham.app.meetingscheduler2.practice.entity.MeetingRoomSchedular;
import com.shubham.app.meetingscheduler2.practice.entity.Room;
import com.shubham.app.meetingscheduler2.practice.entity.User;
import com.shubham.app.meetingscheduler2.practice.exception.InvalidMeetingException;
import com.shubham.app.meetingscheduler2.practice.exception.InvalidMeetingRoom;
import com.shubham.app.meetingscheduler2.practice.exception.RoomAlreadyBookedException;

import java.time.*;
import java.util.Optional;

public class MeetingRoomAllocationService {

    private MeetingRoomSchedular meetingRoomSchedular;

    public MeetingRoomAllocationService(MeetingRoomSchedular meetingRoomSchedular) {
        this.meetingRoomSchedular = meetingRoomSchedular;
    }

    public synchronized void createMeeting(int roomId, Instant startTime, Instant endTime, User user) {

        Optional<Room> desiredMeetingRoom = Optional.empty();

        for (Room meetingRoom : meetingRoomSchedular.getRooms()) {
            if (roomId == meetingRoom.getId()) {
                desiredMeetingRoom = Optional.of(meetingRoom);
                break;
            }
        }

        if (desiredMeetingRoom.isEmpty()) {
            throw new InvalidMeetingRoom("invalid roomId");
        }

        for (Meeting scheduledMeeting : meetingRoomSchedular.getScheduledMeetings()) {
            if ((scheduledMeeting.getRoom().getId() == roomId)
                    && ((scheduledMeeting.getStartTime().getEpochSecond() <= startTime.getEpochSecond()
                            && scheduledMeeting.getEndTime().getEpochSecond() >= startTime.getEpochSecond())
                            || (scheduledMeeting.getStartTime().getEpochSecond() <= endTime.getEpochSecond()
                                    && scheduledMeeting.getEndTime().getEpochSecond() >= endTime.getEpochSecond()))) {
                System.out.println("Error : this meeting room is already booked for this time");
                throw new RoomAlreadyBookedException("this meeting room is already booked for this time");
            }
        }

        int meetingId = meetingRoomSchedular.getAtomicInteger().get();
        Meeting meeting = new Meeting(meetingId, startTime, endTime, Instant.now(), user.getId(),
                desiredMeetingRoom.get());

        meetingRoomSchedular.getScheduledMeetings().add(meeting);
    }

    public synchronized void deleteMeeting(int meetingId) {

        Meeting meetingToDelete = null;
        int index = 0;
        for (index = 0; index < meetingRoomSchedular.getScheduledMeetings().size(); index++) {
            Meeting scheduledMeeting = meetingRoomSchedular.getScheduledMeetings().get(index);
            if (scheduledMeeting.getId() == meetingId) {
                meetingToDelete = scheduledMeeting;
                break;
            }
        }

        if (meetingToDelete == null) {
            throw new InvalidMeetingException("invalid meetingId");
        }
        meetingRoomSchedular.getScheduledMeetings().remove(index);
    }
}
