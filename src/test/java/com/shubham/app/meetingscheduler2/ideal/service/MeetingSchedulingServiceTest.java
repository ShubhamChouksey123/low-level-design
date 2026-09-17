package com.shubham.app.meetingscheduler2.ideal.service;

import org.junit.jupiter.api.Test;

import com.shubham.app.meetingscheduler2.ideal.entity.Meeting;
import com.shubham.app.meetingscheduler2.ideal.entity.MeetingRoomBoard;
import com.shubham.app.meetingscheduler2.ideal.entity.Room;
import com.shubham.app.meetingscheduler2.ideal.entity.User;
import com.shubham.app.meetingscheduler2.ideal.exception.InvalidMeetingException;
import com.shubham.app.meetingscheduler2.ideal.exception.NoRoomAvailableException;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MeetingSchedulingServiceTest {

    private final User user = new User(1, "Shubham");

    // Regression test for practice/session-03-meeting-room-scheduler.md's bug:
    // every booked
    // meeting must get a distinct id. The practice attempt read AtomicInteger.get()
    // instead of
    // getAndIncrement(), so every meeting shared id 0 and cancelling one by id
    // could match any
    // of them.
    @Test
    void eachBookedMeetingGetsADistinctId() {
        MeetingRoomBoard board = new MeetingRoomBoard(List.of(new Room(0), new Room(1)),
                new FirstAvailableRoomAllocationStrategy());
        MeetingSchedulingService service = new MeetingSchedulingService(board);

        Meeting first = service.bookMeeting(Instant.parse("2026-09-17T10:00:00Z"),
                Instant.parse("2026-09-17T11:00:00Z"), user);
        Meeting second = service.bookMeeting(Instant.parse("2026-09-17T13:00:00Z"),
                Instant.parse("2026-09-17T14:00:00Z"), user);

        assertNotEquals(first.getId(), second.getId());
    }

    @Test
    void cancellingOneMeetingDoesNotAffectAnother() {
        MeetingRoomBoard board = new MeetingRoomBoard(List.of(new Room(0), new Room(1)),
                new FirstAvailableRoomAllocationStrategy());
        MeetingSchedulingService service = new MeetingSchedulingService(board);
        Meeting first = service.bookMeeting(Instant.parse("2026-09-17T10:00:00Z"),
                Instant.parse("2026-09-17T11:00:00Z"), user);
        Meeting second = service.bookMeeting(Instant.parse("2026-09-17T13:00:00Z"),
                Instant.parse("2026-09-17T14:00:00Z"), user);

        service.cancelMeeting(first.getId());

        assertEquals(1, board.getMeetings().size());
        assertEquals(second.getId(), board.getMeetings().get(0).getId());
    }

    // Regression test for the overlap-detection bug: a slot that wraps an existing
    // meeting must
    // be rejected, not silently accepted because neither endpoint falls inside the
    // existing slot.
    @Test
    void bookingASlotThatWrapsAnExistingMeetingIsRejected() {
        MeetingRoomBoard board = new MeetingRoomBoard(List.of(new Room(0)), new FirstAvailableRoomAllocationStrategy());
        MeetingSchedulingService service = new MeetingSchedulingService(board);
        service.bookMeeting(Instant.parse("2026-09-17T12:00:00Z"), Instant.parse("2026-09-17T13:59:00Z"), user);

        assertThrows(NoRoomAvailableException.class, () -> service.bookMeeting(Instant.parse("2026-09-17T11:30:00Z"),
                Instant.parse("2026-09-17T14:15:00Z"), user));
    }

    @Test
    void cancellingAnUnknownMeetingThrows() {
        MeetingRoomBoard board = new MeetingRoomBoard(List.of(new Room(0)), new FirstAvailableRoomAllocationStrategy());
        MeetingSchedulingService service = new MeetingSchedulingService(board);

        assertThrows(InvalidMeetingException.class, () -> service.cancelMeeting(999));
    }
}
