package com.shubham.app.meetingscheduler.entity;

public class Room {

    private Integer roomId;
    private Integer maxCapacity;

    public Room(Integer roomId, Integer maxCapacity) {
        this.roomId = roomId;
        this.maxCapacity = maxCapacity;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    @Override
    public String toString() {
        return "Room{" + "roomId=" + roomId + ", maxCapacity=" + maxCapacity + '}';
    }
}
