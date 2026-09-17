package com.shubham.app.meetingscheduler2.ideal.service;

import com.shubham.app.meetingscheduler2.ideal.entity.MeetingRoomBoard;
import com.shubham.app.meetingscheduler2.ideal.entity.Room;
import com.shubham.app.meetingscheduler2.ideal.entity.RoomAllocationStrategy;
import com.shubham.app.meetingscheduler2.ideal.exception.InvalidNumberOfRoomsException;

import java.util.ArrayList;
import java.util.List;

public class MeetingRoomCreationService {

    public MeetingRoomBoard createMeetingRooms(int totalRooms, RoomAllocationStrategy allocationStrategy) {
        if (totalRooms <= 0) {
            throw new InvalidNumberOfRoomsException("a meeting room board must have at least one room");
        }

        List<Room> rooms = new ArrayList<>();
        for (int i = 0; i < totalRooms; i++) {
            rooms.add(new Room(i));
        }

        return new MeetingRoomBoard(rooms, allocationStrategy);
    }
}
