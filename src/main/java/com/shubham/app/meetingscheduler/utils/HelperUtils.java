package com.shubham.app.meetingscheduler.utils;

import com.shubham.app.meetingscheduler.entity.Meeting;

import java.util.Date;

public class HelperUtils {

    public boolean isClashing(Meeting meeting, Date startDate, Date endDate) {

        if (meeting == null) {
            return false;
        }

        if (startDate.after(meeting.getStartDate()) && startDate.before(meeting.getEndDate())) {
            return true;
        }

        if (endDate.after(meeting.getStartDate()) && endDate.before(meeting.getEndDate())) {
            return true;
        }
        return false;
    }
}
