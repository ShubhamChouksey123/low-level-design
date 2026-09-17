package com.shubham.app.meetingscheduler2.practice.service;

import com.shubham.app.meetingscheduler2.practice.entity.MeetingRoomSchedular;
import com.shubham.app.meetingscheduler2.practice.entity.Room;
import com.shubham.app.meetingscheduler2.practice.exception.InvalidNumberOfRoomsException;

import java.util.*;

public class MeetingRoomCreationService {

    public MeetingRoomSchedular createMeetingRoom(int totalRooms) {

        if (totalRooms <= 0) {
            throw new InvalidNumberOfRoomsException("invalid meeting room number");
        }

        List<Room> rooms = new ArrayList<>();
        for (int i = 0; i < totalRooms; i++) {
            rooms.add(new Room(i));
        }

        return new MeetingRoomSchedular(rooms);
    }
}
