# Session 07 — Rate Limiter (LLD/OOD) · 2.8/10

> This session shows a real split in quality within one mock: `SlidingWindowCounter`'s algorithm itself is
> correct on the first attempt (verified by hand-trace against the actual demo output), but almost everything
> around it is weak — `FixWindowCounter` was a non-functional stub until fixed in-session, the `RateLimitChecker`
> Strategy interface was never actually wired up (one implementation is hardcoded, making the other dead code),
> no diagram was drawn at all, there's no exception handling anywhere, and the per-client map has a real
> concurrency race. Weakest axis by far: Class Diagram & Relationships, scoring 1/10 — the first session with
> *zero* diagram produced.

**Problem:** Design a rate limiter: allow/reject a client's request based on how many requests it's made
recently, built up in stages (Fixed Window → Sliding Window → Token Bucket → thread-safe → distributed) ·
**Focus:** class design, no architecture/scale concerns · **Overall:** 2.8/10 · **Weakest areas:** Class Diagram
& Relationships, Pattern Choice & Justification, Implementation & Exceptions, Correctness · **Artifacts:**
[`Main.java`](../src/main/java/com/shubham/app/ratelimiter/practice/Main.java),
[entity/](../src/main/java/com/shubham/app/ratelimiter/practice/entity/),
[service/](../src/main/java/com/shubham/app/ratelimiter/practice/service/),
[rate-limiter.md](../src/main/java/com/shubham/app/ratelimiter/practice/rate-limiter.md) (no class diagram was
produced for this mock)

## The problem

> Design and implement a **rate limiter**: a component that decides, for a given client/key, whether a request
> should be **allowed** or **rejected** based on how many requests that client has made recently.
>
> 1. **Stage 1 — Fixed Window**: allow at most `N` requests per client within a fixed time window; once the
>    window resets, the count resets to zero.
> 2. **Stage 2 — Sliding Window**: fix the fixed-window edge-burst problem (a client can send `N` requests at
>    the very end of one window and another `N` at the very start of the next, doubling the effective rate) by
>    tracking requests on a rolling basis instead of a hard reset boundary.
> 3. **Stage 3 — Token Bucket**: a bucket of `capacity` tokens refilling at a fixed rate; a request consumes a
>    token and is rejected if the bucket is empty.
> 4. **Stage 4 — Thread-Safe**: make the chosen implementation safe under concurrent access.
> 5. **Stage 5 — Distributed**: discuss the design across multiple server instances.

What it really tests: whether a swappable *algorithm* (Fixed vs. Sliding Window) can actually be swapped at
runtime through one real selection point, not just declared through an interface that nothing ever chooses
between — plus whether per-client mutable state is protected under concurrent access, which Stage 4 asks for
explicitly.

## Requirements & entities gathered

What was produced: [`practice/README.md`](../src/main/java/com/shubham/app/ratelimiter/practice/README.md)
lists three functional requirements (the three algorithms) and a two-line, incomplete entity list — just
`Request`, with no responsibility sentence and a second bullet left blank.

Gaps against [step ① and ②](../concepts/answer-framework.md#-requirements--scope) of the framework:
- No actors named, and no out-of-scope list at all — not even Stage 3/4/5 are explicitly deferred in writing,
  even though the code never attempts them.
- The entity list names only `Request`, even though the actual code that got built has four more classes
  (`RateLimitChecker`, `FixWindowCounter`, `SlidingWindowCounter`, `RateLimiterService`) — the written
  requirements/entities doc and the actual implementation diverged, and nothing was updated to reconcile them.
- `Request` itself (`practice/entity/Request.java:7`) was listed with no stated responsibility, and ships with
  a field (`userId`) that's stored but never read anywhere in the design (see lost-point #6 below).

## The design produced

No diagram — as-drawn or otherwise — exists anywhere under
[`src/main/java/com/shubham/app/ratelimiter/practice/`](../src/main/java/com/shubham/app/ratelimiter/practice)
for this mock. Every prior session had at least a rough as-drawn diagram to review, even when the diagram
itself had gaps; this is the first session where none was produced at all, so there's nothing to embed here.

What the code's actual (undrawn) relationships are:
- `FixWindowCounter` — implements the contract of `RateLimitChecker`.
- `SlidingWindowCounter` — implements the contract of `RateLimitChecker`.
- `RateLimiterService` — has-a `Map<Integer, RateLimitChecker>`, but **uses** only `SlidingWindowCounter`
  directly (`practice/service/RateLimiterService.java:30`) — the interface exists, but nothing in the design
  ever treats it as swappable.

## Scorecard

| Axis | Score | Note |
|---|---|---|
| Requirements & Entities | 4/10 | Three algorithms named, but the entity list is a two-line stub that doesn't match the four extra classes actually built. |
| Class Diagram & Relationships | 1/10 | No diagram produced at all — the floor for this axis. |
| Pattern Choice & Justification | 3/10 | `RateLimitChecker`'s *shape* is a correct Strategy interface, but `RateLimiterService` hardcodes one implementation, so nothing ever actually varies at runtime — and no justification was written either way. |
| Implementation & Exception Handling | 3/10 | `SlidingWindowCounter` is solid, but `FixWindowCounter`/`Request` were non-functional stubs pre-fix, there's zero exception handling anywhere, and a real concurrency race exists despite Stage 4 asking for thread-safety by name. |
| Correctness (Primary-Flow Walkthrough) | 3/10 | The demo correctly walks Sliding Window (verified by hand-trace), but Stage 1 (Fixed Window) was never walked at all — it was unreachable dead code until this session fixed it. |
| **Overall** | **2.8/10** | ❌ Needs work |

> **Update:** `FixWindowCounter.addRequest` (originally a `private`, unreachable stub with its logic commented
> out and a hardcoded `return false`) and `Request` (originally had no constructor or getters at all) were both
> completed during this session, and `rollingInterval`'s type was changed from a unit-ambiguous `int` (ms) to
> `Duration` on request. Scores above are left as the record of what the mock originally produced — see
> [`src/main/java/com/shubham/app/ratelimiter/ideal/`](../src/main/java/com/shubham/app/ratelimiter/ideal) for
> the fully-fixed, Strategy-wired, thread-safe version.

> **Second update:** lost-point #1 below (the hardcoded algorithm) is now genuinely fixed in `practice/` too —
> `RateLimiterService` was restructured to take a `RateLimitAlgorithmFactory` in its constructor instead of
> hardcoding `SlidingWindowCounter`, and the `Request` wrapper was dropped entirely (algorithms now take
> `Instant` directly), matching `ideal/`'s design. A **Token Bucket** implementation
> (`practice/service/TokenBucketRateLimitAlgorithm.java`) was also added afterward and wired through the same
> factory — but a dedicated review of it found three new, serious bugs, documented as new rows in "What lost
> points" below. These were found in a follow-up review, not the original mock, so they don't change the
> Scorecard above, but they're real gaps in the same practice code and worth recording in the same ledger.

## What lost points — and the fix

| What I missed | The senior answer | Study link |
|---|---|---|
| `practice/service/RateLimiterService.java:30` hardcodes `new SlidingWindowCounter(...)`, so `RateLimitChecker`'s two implementations are never actually selected between — `FixWindowCounter` was unreachable, dead code. | Accept the algorithm as a constructor-injected Strategy (a factory, since each client needs its own stateful instance) — see `ideal/RateLimiter`. | [`concepts/answer-framework.md`](../concepts/answer-framework.md) ④ Pattern Choice |
| `practice/entity/FixWindowCounter.java`'s original submission: `addRequest` was `private`, unreachable, and its body was a commented-out `windows.push(request)` with a hardcoded `return false` — Stage 1 was stubbed, not implemented. | Completed in-session (see "Update" note above); `ideal/FixedWindowRateLimitingAlgorithm` is the finished, tested version. | [`concepts/answer-framework.md`](../concepts/answer-framework.md) ⑤ Core Classes/Interfaces |
| No diagram — as-drawn or otherwise — was produced anywhere for this mock. | Draw the as-drawn diagram, even a rough one, before moving to code; this axis can't score above the floor without one. | [`concepts/uml-diagrams.md`](../concepts/uml-diagrams.md#1-class-diagram) |
| `practice/service/RateLimiterService.java:26-32` — a check-then-act race: two threads hitting a brand-new client concurrently can each build their own `SlidingWindowCounter`, and the second `put` silently discards the first's recorded request. Nothing here (or in either counter) is `synchronized`, despite Stage 4 asking for thread-safety by name. | A `ConcurrentHashMap` + `computeIfAbsent` for the per-client lookup-or-create, plus a `synchronized` method on each algorithm for its own state — not one lock around every client, since unrelated clients don't need to be serialized against each other. See `ideal/RateLimiter` + ⑧ below. | [`concepts/answer-framework.md`](../concepts/answer-framework.md) ⑧ Concurrency & Extensibility |
| No custom exception anywhere in `practice/` — a non-positive `maxLimit`, a null/non-positive `rollingInterval`, or a null `Instant` all fail silently or with a raw NPE instead of a named failure. | `InvalidRateLimiterConfigException`, thrown at construction — see `ideal/`. | [`concepts/answer-framework.md`](../concepts/answer-framework.md) ⑥ Exception Handling |
| `practice/entity/Request.java:7` — `userId` is stored in every queued `Request` but has no getter; it's dead data riding along in the deque, never read by either counter. | `ideal/` dropped the `Request` wrapper entirely — the algorithms operate directly on `Instant`, since it's `RateLimiter` (not the algorithm) that's keyed per client. | [`concepts/answer-framework.md`](../concepts/answer-framework.md) ② Core Entities & Responsibilities |
| `practice/service/RateLimiterService.java:14` — `userToChecker` never evicts a client's state; a one-off caller occupies memory forever. | **Not fixed in `ideal/` either** — explicitly scoped out there as "idle-client cleanup." Flagging this as a known, still-open gap rather than silently treating it as solved. | [`rate-limiter.md`](../src/main/java/com/shubham/app/ratelimiter/practice/rate-limiter.md)'s own "Cleanup/memory" question |
| `practice/service/TokenBucketRateLimitAlgorithm.java` (found in a later review, added after the original mock) — `remainingTokens = Math.max(remainingTokens + newTokens, bucketSize)` is inverted; it should be `Math.min`. Since `remainingTokens + newTokens` is almost always `<= bucketSize`, `Math.max` forces the bucket back to full on nearly every call, so it never actually depletes — verified by running the demo: all 6 requests in a `bucketSize=3` burst were allowed. | Cap refill with `Math.min`/`BigDecimal.min` against capacity, not `Math.max`. | [`concepts/answer-framework.md`](../concepts/answer-framework.md) ⑤ Core Classes/Interfaces |
| Same file — `Duration.between(...).toSeconds() * rate` truncates to whole seconds before multiplying, so any sub-second gap (this mock's own demo uses `10ms`/`20ms` gaps) computes to exactly 0 new tokens, and the fractional remainder is lost every call since the baseline timestamp still advances regardless. | Compute elapsed time as fractional seconds (e.g. `toNanos() / 1e9`) and accumulate a fractional token balance — see `ideal/TokenBucketRateLimitingAlgorithm`. | [`concepts/answer-framework.md`](../concepts/answer-framework.md) ⑤ Core Classes/Interfaces |
| Same file — `lastFilledAt = Instant.now()` seeds the refill baseline from the wall clock, but every caller in this codebase drives `addRequest`/`allowRequest` with explicit, often historical `Instant`s — comparing the two produces a nonsensical (even negative) elapsed duration on the first call. | Seed the baseline from the first call's own timestamp, not `Instant.now()`. | [`concepts/answer-framework.md`](../concepts/answer-framework.md) ⑤ Core Classes/Interfaces |

## What went well

- `SlidingWindowCounter.addRequest` (`practice/entity/SlidingWindowCounter.java:19-39`) implements the
  sliding-window-log algorithm correctly on the first attempt — verified by hand-tracing all 13 calls in the
  original `Main.java` demo against the code; the printed output matched the trace exactly, with no bugs found
  in the core eviction logic.
- `RateLimiterService`'s one-checker-per-client map correctly implements "per-client, not global" rate
  limiting — exactly what `rate-limiter.md`'s own "Per-client or global?" clarifying question calls for.
- `RateLimitChecker`'s *shape* is right: one method, implemented identically by both counters. The Strategy
  pattern's shape was a correct instinct — the gap was only in wiring it up to actually be selected between
  (lost-point #1), not in defining it.

---

## The ideal design

> **Implemented** in
> [`src/main/java/com/shubham/app/ratelimiter/ideal/`](../src/main/java/com/shubham/app/ratelimiter/ideal) —
> see that package's own
> [`README.md`](../src/main/java/com/shubham/app/ratelimiter/ideal/README.md) for what changed vs. this doc and
> why. Fixed Window and Sliding Window were the initial scope; **Token Bucket** (Stage 3) was added afterward,
> deliberately avoiding all three bugs found in `practice/`'s version of it (see the new rows in "What lost
> points" above). Distributed state (Stage 5) is still explicitly out of scope.

**Framing sentence:** the algorithm is a swappable policy, but its state is per-client — so the Strategy here
has to be a *factory* that mints a fresh stateful instance per client, not a single shared stateless object the
way this repo's other Strategy usages work.

The rest of this section follows [`concepts/answer-framework.md`](../concepts/answer-framework.md)'s 8 steps in
order.

### ① Functional Requirements & Scope

**Actors:** Any caller checking whether a client's request should proceed.

**Operations:** Configure a limiter with `maxRequestsPerWindow`, a `windowSize`, and an algorithm; then
`allowRequest(clientId, timestamp)` — per-client, returns `true`/`false`.

**Explicitly out of scope:** distributed/multi-instance state, and idle-client cleanup (a real gap, reproduced
unchanged from `practice/` — see the relevant row of "What lost points" above).

### ② Core Entities & Responsibilities

| Entity | Responsibility |
|---|---|
| `RateLimiter` | Owns one `clientId -> RateLimitingAlgorithm` map and the single lock guarding it. |
| `RateLimitingAlgorithm` (interface) | One client's rate-limiting state + decision for one algorithm. |
| `RateLimitingAlgorithmFactory` (interface) | The Strategy: builds a fresh `RateLimitingAlgorithm` for a client's first request. |
| `FixedWindowRateLimitingAlgorithm` / `SlidingWindowRateLimitingAlgorithm` / `TokenBucketRateLimitingAlgorithm` | The three concrete algorithms. |
| `FixedWindowRateLimitingAlgorithmFactory` / `SlidingWindowRateLimitingAlgorithmFactory` / `TokenBucketRateLimitingAlgorithmFactory` | Build the matching algorithm, holding its own config. |
| `InvalidRateLimiterConfigException` | Thrown for a null factory or a non-positive limit/window/bucket config. |

### ③ Relationships & Class Diagram

- **Implements the contract of** — `FixedWindowRateLimitingAlgorithm`/`SlidingWindowRateLimitingAlgorithm`/
  `TokenBucketRateLimitingAlgorithm` implement `RateLimitingAlgorithm`; their factories implement
  `RateLimitingAlgorithmFactory`.
- **Has-a** — `RateLimiter` has-a `Map<String, RateLimitingAlgorithm>`.
- **Uses** — `RateLimiter` uses a `RateLimitingAlgorithmFactory`; each factory uses its matching algorithm's
  constructor to create instances.
- **Is-a** — deliberately absent; there's no genuine inheritance hierarchy here.

**Ideal class diagram:** source at
[`diagrams/session07-ideal-class-diagram.mmd`](diagrams/session07-ideal-class-diagram.mmd).

![A class diagram showing RateLimiter using a RateLimitingAlgorithmFactory and having a map of RateLimitingAlgorithm instances, with FixedWindowRateLimitingAlgorithmFactory and SlidingWindowRateLimitingAlgorithmFactory each implementing the contract of RateLimitingAlgorithmFactory and using their matching algorithm class, both of which implement the contract of RateLimitingAlgorithm.](diagrams/session07-ideal-class-diagram.png)

> This diagram predates `TokenBucketRateLimitingAlgorithm`/`TokenBucketRateLimitingAlgorithmFactory` and
> wasn't regenerated when they were added — it still only shows the two window algorithms. Left as-is rather
> than silently redrawn, since the diagram itself wasn't the focus of that follow-up work.

### ④ Pattern Choice & Justification

**Pattern used: Strategy** (`RateLimitingAlgorithmFactory` / `RateLimitingAlgorithm`).

This directly fixes this session's #1 lost-point: `RateLimiterService.checkRateLimitStatus` hardcoded
`new SlidingWindowCounter(...)`, so the already-written `FixWindowCounter` was unreachable dead code even
though the `RateLimitChecker` interface existed. Here, which algorithm runs is decided purely by which factory
`RateLimiter` is constructed with — zero branching inside `RateLimiter` itself.

- **Why a *factory* of the strategy, not the strategy object directly:** a rate limiter's algorithm is
  **stateful per client** (a `Deque` of timestamps belonging to exactly one client), unlike this repo's other
  Strategy usages (e.g. `ConnectionAssigningStrategy`), which are stateless and can be shared across every
  caller. A single shared algorithm instance can't serve every client at once, so `RateLimiter` has to hold a
  factory that mints one instance per client, not the instance itself.
- **This is proven, not just claimed:** `TokenBucketRateLimitingAlgorithm` + its matching factory were added
  after the fact, implementing the same two interfaces — `RateLimiter`'s constructor, `allowRequest`, and its
  map never changed. `RateLimiterTest#tokenBucketPlugsIntoRateLimiterThroughTheSameFactorySeam` is the
  regression test locking this in.

### ⑤ Core Classes/Interfaces

Signatures only — full implementations are in
[`src/main/java/com/shubham/app/ratelimiter/ideal/`](../src/main/java/com/shubham/app/ratelimiter/ideal), and
tests proving each class's behavior are in
[`src/test/java/com/shubham/app/ratelimiter/ideal/`](../src/test/java/com/shubham/app/ratelimiter/ideal):

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

Token bucket tracks its running token balance as a `BigDecimal`, not a `double` — elapsed nanoseconds divided
by a power of ten (1e9) is always an exact, terminating decimal, so comparisons against a whole token are
exact too, with no epsilon tolerance needed for floating-point rounding (the kind of rounding that bit
`practice/`'s own `int`-truncated version — see the new lost-point rows above).

### ⑥ Exception Handling

- `InvalidRateLimiterConfigException` — thrown by `RateLimiter`'s constructor for a null factory, by the
  window algorithms' constructors for a non-positive `maxRequestsPerWindow` or a null/non-positive
  `windowSize`, and by `TokenBucketRateLimitingAlgorithm`'s constructor for a non-positive `bucketCapacity` or
  `tokensPerSecond`.
- Edge case handled **without** an exception: a request for a client `RateLimiter` has never seen is not an
  error — a new algorithm instance (a fresh window) is created transparently on that first request.
- Edge case handled **without** an exception: a rejected request (`allowRequest` returns `false`) is a routine
  outcome, not a failure.

### ⑦ Primary-Flow Walkthrough — the actual fix for this session's bug

Source at
[`diagrams/session07-ideal-sequence-diagram.mmd`](diagrams/session07-ideal-sequence-diagram.mmd).

![A sequence diagram comparing a fixed window and a sliding window rate limiter processing the same six requests, where the fixed window incorrectly allows all six due to a wholesale reset while the sliding window correctly rejects the last two because three earlier requests are still inside the trailing ten second span.](diagrams/session07-ideal-sequence-diagram.png)

This walks the exact bug this session's gaps hid: Stage 1 (Fixed Window) was never exercised by the original
mock at all, because `RateLimiterService` never constructed `FixWindowCounter` — so the classic fixed-window
edge-burst problem (`rate-limiter.md`'s own Stage 2 description: `N` requests at the end of one window plus
`N` more at the start of the next) was never demonstrated, let alone fixed. With `N=3`, a 10s window, and
requests at `t=0, 9.8, 9.9, 10.0, 10.05, 10.1`, Fixed Window wrongly allows all six (its reset at `t=10.0`
wipes the whole count, forgetting `t=9.8`/`t=9.9` are still only fractions of a second old — 5 of the 6 land
within a trailing 10s span, double the limit). Sliding Window correctly allows only 4 and rejects the last two,
since it ages entries out one at a time instead of resetting wholesale. This is proven by
`RateLimiterTest#fixedWindowWronglyAllowsAnEdgeBurstThatSlidingWindowCorrectlyRejects` and reproduced in
`Main.java`'s "Edge burst" demo section.

`Main.java` separately runs the exact `bucketCapacity=3`/`tokensPerSecond=1.0` scenario that exposed the
Token Bucket bugs in `practice/` (new lost-point rows above): `allowed, allowed, allowed, rejected, rejected,
allowed` — the 3 initial tokens cover the first 3 quick calls, the 4th/5th are correctly rejected since well
under a second has passed, and the 6th is allowed again once enough time has elapsed to refill almost exactly
1 token. `TokenBucketRateLimitingAlgorithmTest#refillsFractionallyOverTimeInsteadOfResettingOrTruncatingToZero`
locks this in.

### ⑧ Concurrency & Extensibility

**Concurrency:** a first version of this made `RateLimiter.allowRequest` one `synchronized` method — simple,
and it did close the check-then-act race from lost-point #4, but it also meant *every* client's requests
were serialized against *every other* client's, with no correctness reason for it (each client's state is
already independent). That was caught and fixed: `RateLimiter.clientIdToAlgorithm` is now a
`ConcurrentHashMap`, and the lookup-or-create uses `computeIfAbsent` (atomic per key, so unrelated
`clientId`s never block each other); each concrete `RateLimitingAlgorithm` makes its own `allowRequest`
`synchronized` instead, so only concurrent calls for the *same* client (same instance, same monitor) are
serialized. `RateLimiterTest#concurrentFirstRequestsForTheSameNewClientNeverLoseAWindow` still covers the
same-client race, and `RateLimiterTest#differentClientsAreRateLimitedIndependentlyUnderConcurrentAccess` is
the new regression test proving different clients stay correct — and independent — under this finer-grained
locking.

**Extensibility:** Token Bucket (Stage 3) proved this: one new `RateLimitingAlgorithm` + one new
`RateLimitingAlgorithmFactory`, zero changes to `RateLimiter` (see ④). A per-client-configurable limit is
**not** a drop-in extension — today one `RateLimiter` is built with one factory holding one fixed config for
every client it serves.

## Takeaways to drill

1. A Strategy interface with two implementations isn't actually a Strategy pattern until something *chooses*
   between them at a real decision point — check whether the consuming class takes the interface as a
   constructor/parameter, or whether it still hardcodes one concrete class, before calling the pattern "done."
2. When a per-client (or per-key) algorithm needs its own mutable state, the thing injected into the owning
   class has to be a *factory* of the strategy, not the strategy object itself — a single shared stateful
   instance can't serve multiple clients the way a stateless strategy object can.
3. Draw the diagram before writing the code, every single time — this session is proof the Class Diagram axis
   has a floor of 1/10 with nothing to show, worse than any diagram-with-gaps from a prior session.
4. An entire algorithm that's unreachable (dead code, like `FixWindowCounter` here) is the most severe version
   of "an unwalked functional requirement hides the worst bugs" — it's not just untested, it's never even
   called, so the classic failure mode (fixed-window edge-burst) that Stage 2 exists to fix was never actually
   demonstrated at all.
5. Check-then-act on a shared map (`get` → maybe `new` → `put`) is a race the instant two threads can reach it
   concurrently — Stage 4's "thread-safe" requirement means the whole lookup-or-create sequence needs one lock,
   not just the final mutation.
6. `Math.max`/`Math.min` are easy to swap by accident, and the bug can be invisible by eye — the Token Bucket
   refill-cap line read as "clamp the total" either way, but only one direction is actually a clamp. Run the
   actual numbers through the actual code (as the review did here) rather than trusting that a `Math.*` call
   near a capacity check must be doing the capping correctly.
7. A truncating cast (`(int)`, or `Duration.toSeconds()` instead of `toNanos()`) silently discards whatever
   doesn't divide evenly — for anything accumulating a fractional quantity over many small time steps (like
   token refill), that's not a rounding nicety, it's the difference between "refills correctly" and "never
   refills at all" for realistic sub-second request rates.
8. "Correct" and "correctly scoped" are different bars for a lock. One `synchronized` method around every
   client's call was *correct* (no lost windows), but it was also wrong for a per-client resource — it
   serialized clients that share no state at all. When the thing being protected is naturally partitioned
   (one independent algorithm instance per key), ask whether the lock should be partitioned the same way
   (`ConcurrentHashMap.computeIfAbsent` + a lock per instance) instead of defaulting to one lock for
   everything.

> Rehearse this against [`concepts/answer-framework.md`](../concepts/answer-framework.md) before the next mock.

## How to Improve

Before writing any code, walk every stage/branch named in the problem statement (even ones you plan to defer)
and confirm each one has an actual call path in the demo — Stage 1 here existed as a class but had zero call
path, which hid it from both testing and review until this session's follow-up explicitly asked to fix it.
