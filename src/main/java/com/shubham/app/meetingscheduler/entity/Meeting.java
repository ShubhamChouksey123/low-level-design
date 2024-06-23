package com.shubham.app.meetingscheduler.entity;

import java.util.Date;
import java.util.List;

public class Meeting {

    private Room room;
    private Person head;
    private List<Person> members;

    private Date startDate;
    private Date endDate;

    public Meeting(Room room, Person head, List<Person> members, Date startDate, Date endDate) {
        this.room = room;
        this.head = head;
        this.members = members;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Person getHead() {
        return head;
    }

    public void setHead(Person head) {
        this.head = head;
    }

    public List<Person> getMembers() {
        return members;
    }

    public void setMembers(List<Person> members) {
        this.members = members;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "Meeting{" + "room=" + room + ", head=" + head + ", members=" + members + ", startDate=" + startDate
                + ", endDate=" + endDate + '}';
    }
}
