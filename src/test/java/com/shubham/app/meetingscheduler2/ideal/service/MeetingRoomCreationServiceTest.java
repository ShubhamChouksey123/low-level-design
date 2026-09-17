package com.shubham.app.meetingscheduler2.ideal.service;

import org.junit.jupiter.api.Test;

import com.shubham.app.meetingscheduler2.ideal.entity.MeetingRoomBoard;
import com.shubham.app.meetingscheduler2.ideal.entity.TimeSlot;
import com.shubham.app.meetingscheduler2.ideal.exception.InvalidNumberOfRoomsException;
import com.shubham.app.meetingscheduler2.ideal.exception.NoRoomAvailableException;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MeetingRoomCreationServiceTest {

    @Test
    void createsABoardWithExactlyTheRequestedNumberOfRooms() {
        MeetingRoomBoard board = new MeetingRoomCreationService().createMeetingRooms(5,
                new FirstAvailableRoomAllocationStrategy());
        TimeSlot slot = new TimeSlot(Instant.parse("2026-09-17T12:00:00Z"), Instant.parse("2026-09-17T13:00:00Z"));

        for (int i = 0; i < 5; i++) {
            board.book(i, slot, 1);
        }

        assertEquals(5, board.getMeetings().size());
        assertThrows(NoRoomAvailableException.class, () -> board.book(5, slot, 1));
    }

    @Test
    void rejectsANonPositiveRoomCount() {
        MeetingRoomCreationService creationService = new MeetingRoomCreationService();

        assertThrows(InvalidNumberOfRoomsException.class,
                () -> creationService.createMeetingRooms(0, new FirstAvailableRoomAllocationStrategy()));
    }
}
