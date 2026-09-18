# Connection Pool — Ideal Design (implemented)

This package is the working implementation of the **"The ideal design"** section in
[`practice/session-06-connection-pool.md`](../../../../../../../../practice/session-06-connection-pool.md) — the
reference answer written after reviewing [`../practice`](../practice)'s mock-interview attempt against
[`concepts/answer-framework.md`](../../../../../../../../concepts/answer-framework.md).

## What changed vs. the practice attempt (and why)

- **`releaseConnection` checks the waiting queue before polling it.** The practice attempt called
  `requestQueue.pollFirst()` unconditionally and passed the result straight into `acquireConnection(int)` — when
  nobody was waiting, `pollFirst()` returned `null` and auto-unboxing it into an `int` threw a
  `NullPointerException`. Verified with a scratch test: releasing a connection with an empty queue crashed every
  time. The fix is a one-line `if (!waitingRequestIds.isEmpty())` guard.
- **`ConnectionPoolCreationService` validates the pool size.** The practice attempt's version took any `int`,
  including zero or negative, with no error — every other `*CreationService` in this repo
  (`ParkingLotCreationService`, `MeetingRoomCreationService`) validates its size argument; this one hadn't.
- **`Connection` has no public setter for its id.** The practice attempt's `Connection.setId(int)` could change a
  connection's identity after construction, while nothing updates the map key it's stored under elsewhere —
  a real desync hazard. `id` is `final` here.
- **The `Request` entity was dropped.** It was named in the practice attempt's entity list and had its own class,
  but nothing in `ConnectionPool` ever used it — every operation worked with a raw `requestId` int directly. A
  class with no behavior and no state beyond an id nothing reads is one to cut, not keep.
- **`ConnectionAssigningStrategy` takes a `List<Connection>`, not a `Map<Integer, Connection>`.** The practice
  attempt's interface exposed `ConnectionPool`'s own internal map type to every strategy implementation. A
  strategy only ever needs to iterate the connections, so it only needs a `List`.
- **The strategy is not `synchronized`.** The practice attempt's `FirstEmptyConnectionAssigningStrategy` was
  independently `synchronized` on itself, but the actual atomicity guarantee already comes entirely from
  `ConnectionPool.acquireConnection`'s own `synchronized` wrapping the whole find-and-claim sequence. A second,
  unrelated lock adds nothing and would become a real (if currently harmless) bottleneck the moment one strategy
  instance were shared across multiple pools.

## The bug this design fixes

`ConnectionPool.releaseConnection` in the practice attempt assumed a waiting request was always available to
hand a freed connection to. That's true only when the pool was already full — the far more common case (release
a connection nobody's queued for) crashed with a `NullPointerException` every single time. `Main.java`'s demo
here exercises exactly that path (`releaseConnection(2)` with an empty queue) without crashing, and
`ConnectionPoolTest#releasingWithNoWaitingRequestsDoesNotThrow` is the regression test.
