package com.shubham.app.meetingscheduler2.ideal.entity;

import org.junit.jupiter.api.Test;

import com.shubham.app.meetingscheduler2.ideal.exception.InvalidTimeSlotException;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TimeSlotTest {

    @Test
    void startMustBeBeforeEnd() {
        Instant now = Instant.now();

        assertThrows(InvalidTimeSlotException.class, () -> new TimeSlot(now, now));
        assertThrows(InvalidTimeSlotException.class, () -> new TimeSlot(now, now.minusSeconds(60)));
    }

    @Test
    void nonOverlappingSlotsDoNotOverlap() {
        TimeSlot first = new TimeSlot(Instant.parse("2026-09-17T10:00:00Z"), Instant.parse("2026-09-17T11:00:00Z"));
        TimeSlot second = new TimeSlot(Instant.parse("2026-09-17T11:00:00Z"), Instant.parse("2026-09-17T12:00:00Z"));

        assertFalse(first.overlaps(second));
        assertFalse(second.overlaps(first));
    }

    @Test
    void aSlotThatWrapsAnotherSlotOverlapsIt() {
        TimeSlot inner = new TimeSlot(Instant.parse("2026-09-17T12:00:00Z"), Instant.parse("2026-09-17T13:00:00Z"));
        TimeSlot wrapping = new TimeSlot(Instant.parse("2026-09-17T11:30:00Z"), Instant.parse("2026-09-17T14:00:00Z"));

        assertTrue(inner.overlaps(wrapping));
        assertTrue(wrapping.overlaps(inner));
    }

    @Test
    void aSlotNestedInsideAnotherSlotOverlapsIt() {
        TimeSlot outer = new TimeSlot(Instant.parse("2026-09-17T12:00:00Z"), Instant.parse("2026-09-17T14:00:00Z"));
        TimeSlot nested = new TimeSlot(Instant.parse("2026-09-17T12:30:00Z"), Instant.parse("2026-09-17T13:00:00Z"));

        assertTrue(outer.overlaps(nested));
        assertTrue(nested.overlaps(outer));
    }
}
