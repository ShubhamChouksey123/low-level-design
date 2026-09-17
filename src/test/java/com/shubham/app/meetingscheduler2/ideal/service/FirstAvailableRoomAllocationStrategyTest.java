package com.shubham.app.meetingscheduler2.ideal.service;

import org.junit.jupiter.api.Test;

import com.shubham.app.meetingscheduler2.ideal.entity.Meeting;
import com.shubham.app.meetingscheduler2.ideal.entity.Room;
import com.shubham.app.meetingscheduler2.ideal.entity.TimeSlot;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FirstAvailableRoomAllocationStrategyTest {

    private static final TimeSlot BOOKED = new TimeSlot(Instant.parse("2026-09-17T12:00:00Z"),
            Instant.parse("2026-09-17T13:59:00Z"));

    private final FirstAvailableRoomAllocationStrategy strategy = new FirstAvailableRoomAllocationStrategy();

    // Regression test for practice/session-03-meeting-room-scheduler.md's bug: a
    // slot that
    // wraps entirely around an existing meeting must still be rejected as a
    // conflict.
    @Test
    void aRoomIsNotAvailableWhenTheRequestedSlotWrapsAnExistingMeeting() {
        Room room = new Room(0);
        Meeting existing = new Meeting(0, BOOKED, room, 1);
        TimeSlot wrapping = new TimeSlot(Instant.parse("2026-09-17T11:30:00Z"), Instant.parse("2026-09-17T14:15:00Z"));

        Optional<Room> chosen = strategy.chooseRoom(List.of(room), List.of(existing), wrapping);

        assertTrue(chosen.isEmpty());
    }

    @Test
    void aRoomIsNotAvailableWhenTheRequestedSlotIsNestedInsideAnExistingMeeting() {
        Room room = new Room(0);
        Meeting existing = new Meeting(0, BOOKED, room, 1);
        TimeSlot nested = new TimeSlot(Instant.parse("2026-09-17T12:30:00Z"), Instant.parse("2026-09-17T13:00:00Z"));

        Optional<Room> chosen = strategy.chooseRoom(List.of(room), List.of(existing), nested);

        assertTrue(chosen.isEmpty());
    }

    @Test
    void aDifferentFreeRoomIsChosenWhenTheFirstRoomConflicts() {
        Room busyRoom = new Room(0);
        Room freeRoom = new Room(1);
        Meeting existing = new Meeting(0, BOOKED, busyRoom, 1);

        Optional<Room> chosen = strategy.chooseRoom(List.of(busyRoom, freeRoom), List.of(existing), BOOKED);

        assertEquals(freeRoom.getId(), chosen.orElseThrow().getId());
    }
}
