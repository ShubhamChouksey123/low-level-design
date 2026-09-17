package com.shubham.app.meetingscheduler2.ideal.service;

import com.shubham.app.meetingscheduler2.ideal.entity.Meeting;
import com.shubham.app.meetingscheduler2.ideal.entity.Room;
import com.shubham.app.meetingscheduler2.ideal.entity.RoomAllocationStrategy;
import com.shubham.app.meetingscheduler2.ideal.entity.TimeSlot;

import java.util.List;
import java.util.Optional;

public class FirstAvailableRoomAllocationStrategy implements RoomAllocationStrategy {

    @Override
    public Optional<Room> chooseRoom(List<Room> rooms, List<Meeting> existingMeetings, TimeSlot requested) {
        for (Room room : rooms) {
            boolean roomHasAConflict = false;

            for (Meeting meeting : existingMeetings) {
                if (meeting.getRoom().getId() == room.getId() && meeting.getTimeSlot().overlaps(requested)) {
                    roomHasAConflict = true;
                    break;
                }
            }

            if (!roomHasAConflict) {
                return Optional.of(room);
            }
        }

        return Optional.empty();
    }
}
