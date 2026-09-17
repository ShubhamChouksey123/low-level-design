package com.shubham.app.meetingscheduler2.ideal.entity;

import com.shubham.app.meetingscheduler2.ideal.exception.InvalidTimeSlotException;

import java.time.Instant;

public class TimeSlot {

    private final Instant start;
    private final Instant end;

    public TimeSlot(Instant start, Instant end) {
        if (!start.isBefore(end)) {
            throw new InvalidTimeSlotException("start (" + start + ") must be before end (" + end + ")");
        }
        this.start = start;
        this.end = end;
    }

    public Instant getStart() {
        return start;
    }

    public Instant getEnd() {
        return end;
    }

    // Half-open interval overlap: two slots overlap unless one ends at or before
    // the other
    // starts. The practice attempt's check only tested whether an endpoint of the
    // new slot
    // fell inside the existing slot, which misses the case where one slot fully
    // wraps the
    // other (see practice/session-03-meeting-room-scheduler.md).
    public boolean overlaps(TimeSlot other) {
        return start.isBefore(other.end) && other.start.isBefore(end);
    }

    @Override
    public String toString() {
        return "TimeSlot{" + start + " - " + end + '}';
    }
}
