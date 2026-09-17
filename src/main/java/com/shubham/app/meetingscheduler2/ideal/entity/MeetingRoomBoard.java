package com.shubham.app.meetingscheduler2.ideal.entity;

import com.shubham.app.meetingscheduler2.ideal.exception.InvalidMeetingException;
import com.shubham.app.meetingscheduler2.ideal.exception.NoRoomAvailableException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MeetingRoomBoard {

    private final List<Room> rooms;
    private final List<Meeting> meetings = new ArrayList<>();
    private final RoomAllocationStrategy allocationStrategy;

    public MeetingRoomBoard(List<Room> rooms, RoomAllocationStrategy allocationStrategy) {
        this.rooms = List.copyOf(rooms);
        this.allocationStrategy = allocationStrategy;
    }

    // Finding a free room and recording its booking must happen as one atomic step
    // — the
    // same lesson as ParkingLot.claimVacantSpot in the parking-lot ideal design.
    // Otherwise two
    // threads could both pick the same room for overlapping slots before either is
    // recorded.
    public synchronized Meeting book(int meetingId, TimeSlot requested, int bookedByUserId) {
        Room room = allocationStrategy.chooseRoom(rooms, meetings, requested)
                .orElseThrow(() -> new NoRoomAvailableException("no room is free for " + requested));
        Meeting meeting = new Meeting(meetingId, requested, room, bookedByUserId);
        meetings.add(meeting);
        return meeting;
    }

    public synchronized void cancel(int meetingId) {
        // A plain for-each can't remove from the list while iterating it (that throws
        // ConcurrentModificationException), so this walks the list with an Iterator and
        // calls
        // its own remove() once the matching meeting is found.
        boolean removed = false;

        Iterator<Meeting> iterator = meetings.iterator();
        while (iterator.hasNext()) {
            Meeting meeting = iterator.next();
            if (meeting.getId() == meetingId) {
                iterator.remove();
                removed = true;
                break;
            }
        }

        if (!removed) {
            throw new InvalidMeetingException("no such meeting: " + meetingId);
        }
    }

    public List<Meeting> getMeetings() {
        return List.copyOf(meetings);
    }
}
