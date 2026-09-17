package com.shubham.app.meetingscheduler2.ideal.entity;

import java.util.List;
import java.util.Optional;

public interface RoomAllocationStrategy {

    Optional<Room> chooseRoom(List<Room> rooms, List<Meeting> existingMeetings, TimeSlot requested);
}
