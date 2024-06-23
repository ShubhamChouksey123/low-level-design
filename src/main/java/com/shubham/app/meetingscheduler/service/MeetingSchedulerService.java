package com.shubham.app.meetingscheduler.service;

import com.shubham.app.meetingscheduler.dao.RoomDao;
import com.shubham.app.meetingscheduler.entity.Meeting;
import com.shubham.app.meetingscheduler.entity.Person;
import com.shubham.app.meetingscheduler.entity.Room;
import com.shubham.app.meetingscheduler.utils.HelperUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MeetingSchedulerService {

    private HelperUtils helperUtils;
    private RoomDao roomDao;
    private List<Meeting> meetings;

    public MeetingSchedulerService(HelperUtils helperUtils, RoomDao roomDao) {
        this.helperUtils = helperUtils;
        this.roomDao = roomDao;
        this.meetings = new ArrayList<>();
    }

    public MeetingSchedulerService(RoomDao roomDao) {
        this.helperUtils = new HelperUtils();
        this.roomDao = roomDao;
        this.meetings = new ArrayList<>();
    }

    public void createMeeting(Integer roomId, Person head, List<Person> members, Date startDate, Date endDate) {

        meetings.forEach((meeting) -> {
            if (meeting.getRoom().getRoomId().equals(roomId) && helperUtils.isClashing(meeting, startDate, endDate)) {
                throw new RuntimeException("this room is already booked for this slot");
            }
        });

        Room room = roomDao.getRoomById(roomId);

        if (room == null) {
            throw new RuntimeException("Invalid room Id");
        }

        Meeting meeting = new Meeting(room, head, members, startDate, endDate);

        meetings.add(meeting);
    }

    public List<Meeting> getAllMeetings() {
        return meetings;
    }
}
