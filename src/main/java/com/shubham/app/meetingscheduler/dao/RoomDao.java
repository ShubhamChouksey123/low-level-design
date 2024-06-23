package com.shubham.app.meetingscheduler.dao;

import com.shubham.app.meetingscheduler.entity.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RoomDao {

    private List<Room> rooms = new ArrayList<>();

    public RoomDao(List<Room> rooms) {
        this.rooms = rooms;
    }

    public void addRoom(Room room) {
        rooms.add(room);
    }

    public Room getRoomById(Integer roomId) {
        for (Room room : rooms) {
            if (Objects.equals(room.getRoomId(), roomId)) {
                return room;
            }
        }

        return null;
    }
}
