# Rate Limiter

> Atlassian Technical Screen — reported coding/design question. Moved here from the [DSA repo](https://github.com/ShubhamChouksey123/data-structures-java-solution/blob/master/docs/company-specific/atlassian/README.md) since this problem is system-design-flavored (scaling, concurrency, distributed state) rather than a pure DSA exercise — see that repo's `README.md` for the overall round context. This file only explains the problem itself, no solution/design yet.

## Problem Statement

Design and implement a **rate limiter**: a component that decides, for a given client/key, whether a request should be **allowed** or **rejected** based on how many requests that client has made recently.

The problem is reported as **multi-part and escalating** — expect to build it up in stages rather than receive the full spec up front:

1. **Stage 1 — Fixed Window**: allow at most `N` requests per client within a fixed time window (e.g. 100 requests per 60 seconds); once the window resets, the count resets to zero.
2. **Stage 2 — Sliding Window**: fix the fixed-window edge-burst problem (a client can send `N` requests at the very end of one window and another `N` at the very start of the next, doubling the effective rate) by tracking requests on a rolling/sliding basis instead of a hard reset boundary.
3. **Stage 3 — Token Bucket** (alternate/follow-up algorithm): each client has a bucket that holds up to `capacity` tokens, refilling at a fixed `rate` per second; a request consumes one token and is rejected if the bucket is empty.
4. **Stage 4 — Thread-Safe**: make the chosen implementation safe under concurrent access from multiple threads checking/updating the same client's count simultaneously.
5. **Stage 5 — Distributed**: discuss how the design changes when requests for the same client can land on *any* of several server instances (i.e. state can no longer live in a single process's memory).

## Input / Output Shape

- **Input**: a client/key identifier (e.g. user ID, API key, IP address) and the current timestamp of the incoming request (or the system clock, if timestamps aren't passed explicitly).
- **Output**: a boolean — `allowed` or `rejected` — for each incoming request, decided at the moment it's checked.
- **Configuration** (likely provided or asked about up front): the limit `N`, the window/refill parameters, and whether limits are per-client or global.

## Worked Example

Fixed window, `N = 3` requests per 10-second window, single client `"user1"`:

```
t=0s  -> allowed  (count: 1)
t=2s  -> allowed  (count: 2)
t=5s  -> allowed  (count: 3)
t=7s  -> rejected (count would be 4, limit is 3)
t=11s -> allowed  (new window started, count: 1)
```

Sliding window, same limit, same requests — note how it differs at the fixed-window boundary:

```
t=0s  -> allowed
t=2s  -> allowed
t=5s  -> allowed
t=7s  -> rejected
t=11s -> rejected (the request at t=2s is still within the last 10s of t=11s, so the window
                    still "contains" 2 earlier requests — fixed window would have allowed this)
```

Token bucket, `capacity = 3`, refill rate = 1 token/3s:

```
t=0s  -> allowed  (bucket: 3 -> 2)
t=1s  -> allowed  (bucket: 2 -> 1)
t=2s  -> allowed  (bucket: 1 -> 0)
t=3s  -> rejected (bucket: 0, but +1 token refilled at t=3 -> actually 1, so this depends on
                    exact refill timing — worth clarifying refill granularity before coding)
```

## Things to Clarify Before Designing

- **Which algorithm first?** Does the interviewer want Fixed Window as the starting point, or should you propose Sliding Window / Token Bucket directly? Confirm before committing to an approach, since the escalation path differs for each.
- **Per-client or global limit?** Is the limiter keyed by client ID (most likely, given "backend/API" framing), or is it a single global limiter with no per-client distinction?
- **Window boundaries**: for fixed window, is the window aligned to clock time (e.g. every minute on the minute) or relative to the client's first request?
- **Timestamp source**: is the current time passed into each check call (easier to test deterministically), or does the implementation read the system clock itself?
- **Rejection behavior**: does a rejected request just return `false`/throw, or does the design need to report *when* the client can retry (a `Retry-After`-style value)?
- **Concurrency model**: when asked to make it thread-safe, is a single-process multi-threaded model assumed (e.g. `ConcurrentHashMap` + per-key locking), or does "thread-safe" extend into the distributed discussion?
- **Distributed state**: if asked to extend to multiple server instances, is a shared store (e.g. Redis) assumed as the answer, or is the interviewer looking for you to identify the problem (shared mutable state across instances) before proposing a fix?
- **Cleanup/memory**: what happens to a client's tracking data if they stop sending requests — is unbounded growth of the client-tracking map a concern worth raising?
- **Scale**: how many distinct clients/keys should this handle efficiently — does that affect the per-client state representation (e.g. a deque of timestamps for sliding window vs. a single counter for fixed window)?

## Where This Fits in This Repo

This problem spans both halves of this repo's structure:
- Stages 1–4 (algorithm choice, single-process thread-safety) are a good fit for a timed drill using the [`answer-framework.md`](../../../practice/answer-framework.md) micro-template, or a standalone walkthrough here.
- Stage 5 (distributed/multi-instance) connects directly to [`concepts/06-caching/caching.md`](../../../concepts/06-caching/caching.md) (shared Redis-backed counters) and [`concepts/08-distributed-systems/`](../../../concepts/08-distributed-systems/) (consistency of the shared counter across instances) — worth reviewing both before attempting a full design pass on this problem.
