# Meeting Room Scheduler — Ideal Design (implemented)

This package is the working implementation of the **"The ideal design"** section in
[`practice/session-03-meeting-room-scheduler.md`](../../../../../../../../practice/session-03-meeting-room-scheduler.md)
— the reference answer written after reviewing [`../../meetingscheduler2`](../../meetingscheduler2)'s mock-interview
attempt against [`concepts/answer-framework.md`](../../../../../../../../concepts/answer-framework.md).

## What changed vs. the practice attempt (and why)

- **Booking finds a free room automatically**, instead of requiring the caller to already know a `roomId`. The
  functional requirement was "book a **vacant** meeting room," not "book this specific room" — `MeetingRoomBoard`
  now takes a requested `TimeSlot` and hands the search to a `RoomAllocationStrategy`, the same shape as the
  parking lot design's `SpotAllocationStrategy` and the elevator design's `DispatchStrategy`.
- **`TimeSlot.overlaps(...)` uses the correct half-open-interval test** (`start.isBefore(other.end) &&
  other.start.isBefore(end)`). The practice attempt's check only tested whether one slot's *endpoint* fell inside
  the other slot — which misses the case where one slot fully **wraps** the other. See "The bug this design
  fixes" below.
- **Meeting ids are actually unique.** The practice attempt read `AtomicInteger.get()` instead of
  `getAndIncrement()`, so every meeting was assigned id `0` — `MeetingSchedulingService` now increments a
  per-instance counter on every booking.
- **`MeetingRoomBoard` hides its rooms and meetings entirely** — no getters/setters exposing the raw lists or the
  id counter. `book(...)` and `cancel(...)` are the only ways to change state, and both are `synchronized` so the
  find-and-reserve sequence is atomic (mirrors `ParkingLot.claimVacantSpot()`).
- **`TimeSlot` validates itself at construction** (`start` must be before `end`) instead of trusting every caller
  to pass a sane interval.
- **`Meeting` no longer holds a `User` reference it never used.** The practice attempt's diagram drew a `Meeting
  → User` "has-a" edge, but the code only ever stored a raw `createdByUserId` int — the ideal design keeps that
  same int (`bookedByUserId`) and drops the diagram's aspirational relationship instead of adding a field nothing
  needs.

## The bugs this design fixes

1. **Double-booking via a wrapping interval.** The practice attempt's overlap check
   (`scheduledMeeting.getStartTime() <= startTime && ... || scheduledMeeting.getStartTime() <= endTime && ...`)
   never considers the case where the *existing* meeting is nested entirely inside the *new* request. A slot like
   `11:30–14:15` around an existing `12:00–13:59` meeting slips through undetected. `TimeSlot.overlaps(...)` and
   its regression tests (`FirstAvailableRoomAllocationStrategyTest`, `MeetingSchedulingServiceTest`) fix this.
2. **Every meeting shared id `0`.** `MeetingRoomAllocationService.createMeeting` read the id counter with `.get()`
   and never advanced it, so `deleteMeeting(meetingId)` could only ever remove the *first* meeting in the list —
   the ideal design's `MeetingSchedulingServiceTest#eachBookedMeetingGetsADistinctId` and
   `#cancellingOneMeetingDoesNotAffectAnother` are the regression tests for this.

See `Main.java`'s demo for both fixes exercised end-to-end in one run.
