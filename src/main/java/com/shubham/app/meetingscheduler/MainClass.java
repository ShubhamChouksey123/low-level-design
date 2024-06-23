package com.shubham.app.meetingscheduler;

import com.shubham.app.meetingscheduler.dao.RoomDao;
import com.shubham.app.meetingscheduler.entity.Person;
import com.shubham.app.meetingscheduler.entity.Room;
import com.shubham.app.meetingscheduler.service.MeetingSchedulerService;

import java.util.ArrayList;
import java.util.List;

public class MainClass {

    Person head = new Person(1, "Shubham Chouksey");
    List<Person> members = new ArrayList<>();

    public static void main(String[] args) {

        List<Room> rooms = new ArrayList<>();
        rooms.add(new Room(1, 10));
        rooms.add(new Room(2, 10));
        rooms.add(new Room(3, 10));
        rooms.add(new Room(4, 50));

        RoomDao roomDao = new RoomDao(rooms);

        MeetingSchedulerService meetingSchedulerService = new MeetingSchedulerService(roomDao);

        // meetingSchedulerService.createMeeting( 1, head, List<Person> members, Date
        // startDate,
        // Date endDate);
    }
}
