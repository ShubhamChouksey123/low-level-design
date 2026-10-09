# Rate Limiter — Ideal Design (Fixed Window + Sliding Window + Token Bucket)

Reviewed against [`practice/`](../practice) (see that package's `README.md`/review discussion), then
written directly against
[`concepts/answer-framework.md`](../../../../../../../concepts/answer-framework.md)'s 8 steps. Fixed Window
and Sliding Window were the user's initial scope for this `ideal/` package; **Token Bucket** (Stage 3 in
[`rate-limiter.md`](../practice/rate-limiter.md)) was added afterward as the proof of the ④ extensibility
claim below — it slots in through the exact same `RateLimitingAlgorithmFactory` seam, with zero changes to
`RateLimiter`. The distributed-state discussion (Stage 5) is still out of scope.

## ① Functional Requirements & Scope

**Actor:** Any caller checking whether a client's request should proceed.

**Operations:**
- Configure a limiter with an algorithm (fixed window, sliding window, or token bucket) and that
  algorithm's own parameters.
- `allowRequest(clientId, timestamp)` — returns `true`/`false`. The limit is **per-client**
  (`rate-limiter.md`'s own "Per-client or global?" question, resolved as per-client — matches the practice
  code's one-checker-per-user approach, which the review called out as the right call).

**Explicitly out of scope:**
- Distributed/multi-instance state (Stage 5) — this is a single-process, in-memory design.
- Idle-client cleanup — `RateLimiter` never evicts a client's state once created. This was flagged as a
  real gap during the `practice/` review (unbounded `userToChecker` growth); it's reproduced here
  unchanged because fixing it (an eviction policy, a TTL, a bounded cache) is new scope the user didn't ask
  for, not a side effect of fixing the Strategy-wiring and thread-safety gaps that *were* in scope.

## ② Core Entities & Responsibilities

| Entity | Responsibility |
|---|---|
| `RateLimiter` | Owns one `clientId -> RateLimitingAlgorithm` map and the single lock guarding it. The only place a client's algorithm instance is created or looked up. |
| `RateLimitingAlgorithm` (interface) | One client's rate-limiting state + decision for one algorithm. Never shared across clients. |
| `RateLimitingAlgorithmFactory` (interface) | The Strategy: builds a fresh `RateLimitingAlgorithm` for a client's first request. Which factory `RateLimiter` holds decides which algorithm is enforced. |
| `FixedWindowRateLimitingAlgorithm` / `SlidingWindowRateLimitingAlgorithm` / `TokenBucketRateLimitingAlgorithm` | The three concrete algorithms. |
| `FixedWindowRateLimitingAlgorithmFactory` / `SlidingWindowRateLimitingAlgorithmFactory` / `TokenBucketRateLimitingAlgorithmFactory` | Build the corresponding algorithm, holding its own config (`maxRequestsPerWindow`/`windowSize`, or `bucketCapacity`/`tokensPerSecond`). |
| `InvalidRateLimiterConfigException` | Thrown for a null factory, a non-positive limit, or a non-positive/null window size. |

## ③ Relationships & Class Diagram

- **Has-a** — `RateLimiter` has-a `RateLimitingAlgorithmFactory` and has-a `Map<String, RateLimitingAlgorithm>`.
- **Uses** — `RateLimiter` uses the factory to create algorithm instances on demand; it never constructs a
  concrete algorithm itself.
- **Implements the contract of** — `FixedWindowRateLimitingAlgorithm`/`SlidingWindowRateLimitingAlgorithm`/
  `TokenBucketRateLimitingAlgorithm` implement `RateLimitingAlgorithm`; their factories implement
  `RateLimitingAlgorithmFactory`.

No diagram was drawn — same reasoning as the LRU cache's `ideal/README.md`: there's no mock attempt's
diagram to review against, and the shape here (one controller + a two-interface Strategy pair) is simple
enough that a diagram wouldn't earn its keep.

## ④ Pattern Choice & Justification

**Pattern used: Strategy** (`RateLimitingAlgorithmFactory` / `RateLimitingAlgorithm`), consistent with every
other `ideal/` package's "the one thing that varies" convention.

This directly fixes the biggest gap found reviewing `practice/`:
`RateLimiterService.checkRateLimitStatus` hardcoded `new SlidingWindowCounter(...)`, so the already-written
`FixWindowCounter` was unreachable dead code — the `RateLimitChecker` interface existed but nothing ever
selected between its implementations. Here, which algorithm runs is decided purely by which factory
`RateLimiter` is constructed with (see `Main.java`, which builds one `RateLimiter` per algorithm from the
same two inputs) — zero branching inside `RateLimiter` itself.

**Why a *factory* of the strategy, not the strategy object directly** (unlike this repo's other Strategy
usages, e.g. `ConnectionAssigningStrategy`, which *is* the stateless policy object itself): a rate limiter's
algorithm is **stateful per client** — a `Deque` of timestamps that belongs to exactly one client. A single
shared `FixedWindowRateLimitingAlgorithm` instance can't serve every client at once the way a stateless
`ConnectionAssigningStrategy` can. So the thing `RateLimiter` holds has to be a *factory* that mints one
algorithm instance per client, not the algorithm instance itself.

**This is proven, not just claimed:** `TokenBucketRateLimitingAlgorithm` + `TokenBucketRateLimitingAlgorithmFactory`
were added after the fact, implementing the same two interfaces — `RateLimiter`'s constructor, `allowRequest`,
and its map never changed. `RateLimiterTest#tokenBucketPlugsIntoRateLimiterThroughTheSameFactorySeam` is the
regression test locking this in. This is exactly the OCP point every other `ideal/` package's ④ section makes
about its own Strategy.

Token bucket's own state is a continuously-refilling `BigDecimal availableTokens` (capped at
`bucketCapacity`, refilled from the elapsed time between calls) rather than a `Deque` of timestamps — a
structurally different kind of state from the two window algorithms, which is exactly why it needed its own
class rather than a branch inside an existing one. `BigDecimal` (not `double`) is deliberate: elapsed
nanoseconds divided by a power of ten (1e9) is always an exact, terminating decimal, so comparisons against
a whole token are exact too, with no epsilon tolerance needed for floating-point rounding.

## ⑤ Core Classes/Interfaces

Signatures only — full implementations are in
[`src/main/java/com/shubham/app/ratelimiter/ideal/`](../../../../../../../src/main/java/com/shubham/app/ratelimiter/ideal),
tests in
[`src/test/java/com/shubham/app/ratelimiter/ideal/`](../../../../../../../src/test/java/com/shubham/app/ratelimiter/ideal):

```java
interface RateLimitingAlgorithm { boolean allowRequest(Instant timestamp); }
interface RateLimitingAlgorithmFactory { RateLimitingAlgorithm create(); }

class RateLimiter {
    RateLimiter(RateLimitingAlgorithmFactory algorithmFactory);   // requires a non-null factory
    boolean allowRequest(String clientId, Instant timestamp);
}

class FixedWindowRateLimitingAlgorithm implements RateLimitingAlgorithm {
    FixedWindowRateLimitingAlgorithm(int maxRequestsPerWindow, Duration windowSize);
}
class SlidingWindowRateLimitingAlgorithm implements RateLimitingAlgorithm {
    SlidingWindowRateLimitingAlgorithm(int maxRequestsPerWindow, Duration windowSize);
}
class TokenBucketRateLimitingAlgorithm implements RateLimitingAlgorithm {
    TokenBucketRateLimitingAlgorithm(int bucketCapacity, double tokensPerSecond);
}
```

(Each algorithm's matching factory just holds the same constructor args and calls the algorithm's
constructor from `create()`.)

**Client identity is `String`, not `int`** (the practice code's `Request.userId` was an `int`) —
`rate-limiter.md`'s own Input/Output section says a client key can be "a user ID, API key, IP address",
which is realistically a `String` far more often than a dense integer ID.

## ⑥ Exception Handling

- `InvalidRateLimiterConfigException` — thrown by `RateLimiter`'s constructor for a null factory, by
  the window algorithms' constructors for a non-positive `maxRequestsPerWindow` or a null/non-positive
  `windowSize`, and by `TokenBucketRateLimitingAlgorithm`'s constructor for a non-positive `bucketCapacity`
  or `tokensPerSecond`.
- Edge case handled **without** an exception: a request for a client `RateLimiter` has never seen is not an
  error — a new algorithm instance (a fresh window) is created transparently on that first request.
- Edge case handled **without** an exception: a rejected request (`allowRequest` returns `false`) is a
  routine outcome, not a failure — same convention as every other boolean-decision method in this repo.

## ⑦ Primary-Flow Walkthrough

`Main.java` runs the **exact worked example from `rate-limiter.md`** (`N=3`, 10s window, requests at
`t=0,2,5,7,11s`) through both algorithms back-to-back, for the same client:

```
Fixed window:    allowed, allowed, allowed, rejected, allowed
Sliding window:  allowed, allowed, allowed, rejected, allowed
```

The fixed-window row matches `rate-limiter.md`'s own worked example exactly — `t=7` is the 4th request in
the still-open window from `t=0`, and `t=11` lands in a brand-new window (fixed-window's whole point: a
burst can straddle a window boundary).

**The sliding-window row intentionally diverges from `rate-limiter.md`'s prose**, which claims `t=11`
should be *rejected*. This implementation's sliding window log only ever stores *allowed* requests — a
rejected request never happened from the service's point of view, so it can't consume quota a real request
would need. Under that reading, by `t=11` only `t=2` and `t=5` are still within the last 10s (`t=0` has aged
out), so the window has room and `t=11` is allowed. `rate-limiter.md`'s own explanation text ("the window
still contains 2 earlier requests") isn't actually consistent with its stated conclusion either — 2
earlier requests plus 1 new one is 3, which is still `<= N=3` and should be allowed, not rejected, under
any reading. This is flagged rather than silently "corrected" one way or the other, since it's a
discrepancy in an informal problem-notes doc, not a verified spec — see
`SlidingWindowRateLimitingAlgorithmTest#onlyAcceptedRequestsConsumeTheSlidingWindowsQuota` for the behavior
this implementation actually has, asserted and locked in by a test.

The *actual* behavioral difference the two algorithms are supposed to demonstrate — a sliding window never
resets wholesale the way a fixed window does — is covered instead by
`SlidingWindowRateLimitingAlgorithmTest#theWindowNeverResetsWholesaleItOnlyAgesOutIndividualEntries`.

`Main.java` then runs a third, independent scenario — **Token Bucket** (`bucketCapacity=3`,
`tokensPerSecond=1.0`) against 6 requests at `t=0.01,0.02,0.03,0.04,0.08,1.01s` — the exact numbers from the
`practice/` code review that originally exposed the `Math.max`/`Math.min` inversion bug there (which let all
6 requests through regardless of capacity):

```
Token bucket:  allowed, allowed, allowed, rejected, rejected, allowed
```

The 3 initial tokens cover the first 3 calls (10/20/30ms apart); the 4th and 5th are correctly rejected
since well under a second has passed; the 6th is allowed again once ~0.93 tokens refill (930ms after the
5th) on top of the ~0.07 left over — `0.07 + 0.93` lands on exactly `1.00` thanks to the `BigDecimal`
arithmetic (see ④), not approximately `1.0` the way it would with `double`. This exact scenario is also
locked in by
`TokenBucketRateLimitingAlgorithmTest#refillsFractionallyOverTimeInsteadOfResettingOrTruncatingToZero`.

## ⑧ Concurrency & Extensibility

**Concurrency** — this directly addresses `rate-limiter.md`'s Stage 4 and a race flagged in the
`practice/` review:
- `RateLimiter.allowRequest` is one `synchronized` method — the lookup-or-create of a client's algorithm
  and the delegated `allowRequest` call happen as one atomic step, the same boundary convention as every
  other resource entity in this repo (`ParkingLot`, `MeetingRoomBoard`, `ConnectionPool`, `LRUCache`).
  This specifically closes the check-then-act race the practice review found: two threads racing on the
  same brand-new client's first request can no longer each create their own algorithm instance and have one
  silently overwrite (and lose the request recorded by) the other.
- `RateLimiterTest#concurrentFirstRequestsForTheSameNewClientNeverLoseAWindow` fires many concurrent first
  requests for one new client and asserts exactly `maxRequestsPerWindow` are allowed — if the race were
  still present, more could slip through.
- The individual algorithm classes (`FixedWindowRateLimitingAlgorithm`/`SlidingWindowRateLimitingAlgorithm`)
  are **not** separately synchronized — like `Connection`'s mutators in the connection-pool `ideal/` design,
  they're only ever reachable through `RateLimiter`'s lock, so a second lock inside them would be redundant.

**Extensibility:**
- Token Bucket (Stage 3) proved this: one new `RateLimitingAlgorithm` + one new
  `RateLimitingAlgorithmFactory`, zero changes to `RateLimiter` (see ④). The next such addition (e.g. a
  leaky-bucket variant) costs the same: one algorithm, one factory, nothing else.
- A per-client-configurable limit (different `maxRequestsPerWindow` for different clients) is **not** a
  drop-in extension of this design — today one `RateLimiter` is built with one factory holding one fixed
  config for every client it serves. Supporting per-client config would mean the factory needs the
  `clientId` to decide what to build, which is a real signature change, not just a new Strategy
  implementation.
