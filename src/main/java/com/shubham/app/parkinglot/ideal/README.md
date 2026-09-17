# Parking Lot — Ideal Design (implemented)

This package is the working implementation of the **"The ideal design"** section in
[`practice/session-02-parking-lot.md`](../../../../../../../../practice/session-02-parking-lot.md) — the
reference answer written after reviewing [`../practice`](../practice)'s mock-interview attempt against
[`concepts/answer-framework.md`](../../../../../../../../concepts/answer-framework.md).

## What changed vs. the doc (and why)

- **`Spot` validates its own id in the constructor**, not only in a setter. The practice attempt's `setId(...)`
  rejected a negative id, but the single-arg constructor everyone actually called (`new Spot(id)`) bypassed it
  entirely — dead validation. There's also no `setStatus(...)` here at all: `occupy()`/`vacate()` are
  package-private, so only `ParkingLot` (in the same package) can drive a spot's status, and only as part of an
  already-atomic claim/release.
- **`ParkingLot.claimVacantSpot()` is one `synchronized` method**, not a separate "find" then "claim." The
  practice attempt scanned for a vacant spot, then called a `synchronized` setter on it as a second step — that
  doesn't stop two threads from finding the *same* spot before either claims it. Here the whole
  find-and-claim sequence is the critical section.
- **`Ticket.close()` is single-use.** The practice attempt's `vacateSpot(ticketId)` never marked a ticket used, so
  replaying it after the spot was reissued would silently free the new occupant's spot. `close()` throws
  `InvalidTicketException` the second time it's called on the same ticket — see "The bug this design fixes" below.
- **Spot allocation is a `SpotAllocationStrategy`**, not an inline loop. `FirstAvailableSpotAllocationStrategy` is
  the only implementation today, but it's the identified "policy that varies" from step ④ — a nearest-to-entrance
  or size-matched policy is a second class, not an edit to `ParkingLot`.
- **`ParkingService`'s ticket counter is an instance `AtomicInteger`**, not a `static int` reset inside the
  constructor. The practice attempt's static counter meant a second `ParkingService` (a second lot) would reset
  the shared counter and could collide on ticket ids.

## The bug this design fixes

The practice attempt's `vacateSpot(ticketId)` never invalidated the ticket it was called with. Calling it twice
with the same id succeeded both times with no error — and if the freed spot had since been reissued to a new
vehicle, the second (stale) call silently freed that new vehicle's spot instead, with nothing to indicate anything
had gone wrong. `Ticket.close()` here throws on a second call, and `ParkingService.vacateSpot` propagates that —
see `Main.java`'s walkthrough and the regression test in
`ParkingServiceTest#replayingAnAlreadyVacatedTicketDoesNotFreeTheNextOccupantsSpot`.
