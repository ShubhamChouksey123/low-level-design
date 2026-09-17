package com.shubham.app.meetingscheduler2.ideal.entity;

import org.junit.jupiter.api.Test;

import com.shubham.app.meetingscheduler2.ideal.exception.InvalidMeetingException;
import com.shubham.app.meetingscheduler2.ideal.exception.NoRoomAvailableException;
import com.shubham.app.meetingscheduler2.ideal.service.FirstAvailableRoomAllocationStrategy;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MeetingRoomBoardTest {

    private static final TimeSlot SLOT = new TimeSlot(Instant.parse("2026-09-17T12:00:00Z"),
            Instant.parse("2026-09-17T13:59:00Z"));

    @Test
    void bookingReservesTheOnlyFreeRoom() {
        MeetingRoomBoard board = new MeetingRoomBoard(List.of(new Room(0)), new FirstAvailableRoomAllocationStrategy());

        Meeting meeting = board.book(0, SLOT, 1);

        assertEquals(0, meeting.getRoom().getId());
        assertThrows(NoRoomAvailableException.class, () -> board.book(1, SLOT, 1));
    }

    @Test
    void cancellingFreesTheRoomForTheNextBooking() {
        MeetingRoomBoard board = new MeetingRoomBoard(List.of(new Room(0)), new FirstAvailableRoomAllocationStrategy());
        Meeting meeting = board.book(0, SLOT, 1);

        board.cancel(meeting.getId());

        Meeting rebooked = board.book(1, SLOT, 1);
        assertEquals(meeting.getRoom().getId(), rebooked.getRoom().getId());
    }

    @Test
    void cancellingAnUnknownMeetingIdThrows() {
        MeetingRoomBoard board = new MeetingRoomBoard(List.of(new Room(0)), new FirstAvailableRoomAllocationStrategy());

        assertThrows(InvalidMeetingException.class, () -> board.cancel(999));
    }

    @Test
    void concurrentBookingsForTheSameSlotNeverDoubleBookARoom() throws InterruptedException {
        int roomCount = 20;
        List<Room> rooms = java.util.stream.IntStream.range(0, roomCount).mapToObj(Room::new).toList();
        MeetingRoomBoard board = new MeetingRoomBoard(rooms, new FirstAvailableRoomAllocationStrategy());
        AtomicInteger nextId = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(roomCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger rejectedCount = new AtomicInteger(0);

        for (int i = 0; i < roomCount * 2; i++) {
            executor.submit(() -> {
                try {
                    board.book(nextId.getAndIncrement(), SLOT, 1);
                    successCount.incrementAndGet();
                } catch (NoRoomAvailableException e) {
                    rejectedCount.incrementAndGet();
                }
            });
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));

        assertEquals(roomCount, successCount.get());
        assertEquals(roomCount, rejectedCount.get());
        assertEquals(roomCount, board.getMeetings().size());
    }
}
