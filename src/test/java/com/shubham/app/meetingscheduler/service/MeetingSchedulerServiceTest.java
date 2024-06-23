package com.shubham.app.meetingscheduler.service;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shubham.app.meetingscheduler.dao.RoomDao;
import com.shubham.app.meetingscheduler.entity.Meeting;
import com.shubham.app.meetingscheduler.entity.Person;
import com.shubham.app.meetingscheduler.entity.Room;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class MeetingSchedulerServiceTest {

    final Logger logger = LoggerFactory.getLogger(MeetingSchedulerServiceTest.class);

    Person head = new Person(1, "Shubham Chouksey");
    List<Person> members = new ArrayList<>();

    private Date getDate(int hour) {
        Calendar calender = Calendar.getInstance();
        calender.set(2024, 6, 23, hour, 0, 0);

        return calender.getTime();
    }

    private void printAllMeeting(MeetingSchedulerService meetingSchedulerService) {

        List<Meeting> meetings = meetingSchedulerService.getAllMeetings();
        meetings.forEach((meeting) -> {
            logger.info("meeting : " + meeting);
        });
    }

    @Test
    void testMeetingScheduler() {

        List<Room> rooms = new ArrayList<>();
        rooms.add(new Room(1, 10));
        rooms.add(new Room(2, 10));
        rooms.add(new Room(3, 10));
        rooms.add(new Room(4, 50));

        RoomDao roomDao = new RoomDao(rooms);

        MeetingSchedulerService meetingSchedulerService = new MeetingSchedulerService(roomDao);
        logger.info("we have created an empty everything");

        meetingSchedulerService.createMeeting(1, head, members, getDate(18), getDate(19));
        meetingSchedulerService.createMeeting(1, head, members, getDate(19), getDate(20));
        meetingSchedulerService.createMeeting(1, head, members, getDate(20), getDate(21));

        printAllMeeting(meetingSchedulerService);
    }

    @Test
    void testMeetingScheduler2() {

        List<Room> rooms = new ArrayList<>();
        rooms.add(new Room(1, 10));
        rooms.add(new Room(2, 10));
        rooms.add(new Room(3, 10));
        rooms.add(new Room(4, 50));

        RoomDao roomDao = new RoomDao(rooms);

        MeetingSchedulerService meetingSchedulerService = new MeetingSchedulerService(roomDao);
        logger.info("we have created an empty everything");

        meetingSchedulerService.createMeeting(1, head, members, getDate(18), getDate(19));
        meetingSchedulerService.createMeeting(1, head, members, getDate(19), getDate(20));
        meetingSchedulerService.createMeeting(1, head, members, getDate(20), getDate(21));

        assertThrows(RuntimeException.class, () -> {
            meetingSchedulerService.createMeeting(1, head, members, getDate(18), getDate(20));
        });
        printAllMeeting(meetingSchedulerService);
    }
}
