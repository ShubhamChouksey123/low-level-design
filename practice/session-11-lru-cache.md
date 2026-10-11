# Session 11 — LRU Cache, plus thread-safety escalation (LLD/OOD) · 3.2/10

> The core doubly-linked-list + hashmap mechanics for eviction and recency were right from the start — a hand
> trace of `Main.java`'s own demo matched its inline comments exactly on the first read. But the implementation
> shipped with one genuinely correct-looking first draft that silently violated the cache contract: calling
> `add` on a key that's already present moved it to the front but never updated its stored value, because
> `Node` had no setter for `value` at all. The bug was only found by writing a scratch test that called `add`
> twice on the same key — a case `Main.java`'s own demo never exercised. The session then escalated to "make
> this thread-safe for concurrent `get`/`put`": the actual lock placement (`synchronized` on both mutating
> methods) was correct on the first attempt and held up under a real concurrent stress test, but the "this is
> now thread-safe" claim itself had no regression test backing it until one was explicitly requested as a
> separate follow-up. No requirements doc, no diagram, and no written pattern/concurrency rationale were
> produced at any point, continuing a run of regressions already flagged in most of the last six sessions.

**Problem:** Design an LRU (Least Recently Used) cache — `get(key)` returns the value and marks it most recently
used, `put(key, value)` inserts/updates a value and evicts the least recently used entry when the cache is full
on a new key (LeetCode 146, a standard/well-known problem — no external source verification needed for this one)
— then escalated to: *Thread-Safe LRU Cache — take your LRU Cache solution above and make it safe for concurrent
`get`/`put`.* · **Focus:** class design + concurrency, no architecture/scale concerns · **Overall:** 3.2/10 ·
**Weakest areas:** Class Diagram & Relationships, Pattern Choice & Justification · **Artifacts:**
[`Main.java`](../src/main/java/com/shubham/app/lru/practice/Main.java),
[entity/Lru.java](../src/main/java/com/shubham/app/lru/practice/entity/Lru.java),
[exception/InvalidCapacityException.java](../src/main/java/com/shubham/app/lru/practice/exception/InvalidCapacityException.java),
[test/LruTest.java](../src/test/java/com/shubham/app/lru/practice/entity/LruTest.java)

## The problem

> - Design a data structure that follows the constraints of a Least Recently Used (LRU) cache with a fixed
>   capacity.
> - `get(key)` — return the value if the key exists, otherwise return a "not found" result; accessing a key
>   counts as a use.
> - `put(key, value)` — insert or update the value; if inserting a new key would exceed capacity, evict the
>   least recently used entry first.
>
> **Escalation:** Thread-Safe LRU Cache — take your LRU Cache solution above and make it safe for concurrent
> `get`/`put`.

What it really tests: whether *every* operation that counts as a "use" — not just the obvious ones — actually
updates recency, whether an update-in-place path does everything a fresh insert does except allocate a new
node, and — for the escalation — whether the fix actually serializes every mutation path through one shared
lock, backed by a test that can fail if that claim is wrong, rather than just a keyword added and never
exercised under real concurrent load.

## Requirements & entities gathered

What was produced: no written requirements list and no out-of-scope list at all — the practice package has no
`README.md` and no comment block naming actors, operations, or scope. The entity list that exists in the code
(`Node<K,V>`, `Lru<K,V>`) is reasonable and matches what the problem actually needs, but neither entity has a
one-sentence responsibility documented anywhere.

Gaps against [step ① and ②](../concepts/answer-framework.md#-requirements--scope) of the framework: no actor
named, no explicit out-of-scope list (TTL/expiry, thread-safety, eviction callbacks — all silently unaddressed
rather than named and deferred), and no entity-responsibility table produced before or after writing the code.
The thread-safety escalation didn't add this either: nothing named *what* needed protecting (the linked-list
pointers and `keyToNode` map) or *why* method-level locking was the chosen granularity.

## The design produced

No diagram was produced — `src/main/java/com/shubham/app/lru/practice/` has no `diagram/` directory at all.

- `Lru<K, V>` — has-a `Map<K, Node<K,V>>` keyed by `K`, has-a two sentinel `Node`s (`first`/`last`).
- `Node<K, V>` — has-a `prev`/`next` pointer to another `Node`.

Those are the only two relationships that exist in the code; there's no written or drawn diagram to check them
against, so there's nothing to compare for labeling/edge-correctness this session. The thread-safety escalation
adds no new relationship — only a behavior change (`add`/`get` are now each `synchronized`).

## Scorecard

| Axis | Score /10 | Note |
|---|---|---|
| Requirements & Entities | 3/10 | Entities match what's needed, but nothing was written down — no actors, no out-of-scope list, no responsibility sentences; the escalation didn't add a written scope either. |
| Class Diagram & Relationships | 1/10 | No diagram at all, drawn or described. |
| Pattern Choice & Justification | 1/10 | No pattern considered or discussed for eviction — not even to conclude "none needed," which is actually the correct call here (eviction order is structural, not pluggable), but that conclusion has to be reached deliberately, not by omission; the escalation's concurrency-control choice (coarse-grained `synchronized`) was correct but equally undocumented. |
| Implementation & Exception Handling | 6/10 | ▲ from 3/10 — the original value-update bug is fixed, capacity is now validated, and `synchronized` was placed correctly on both methods on the first attempt; a review-flagged exception typo/misplacement (`InvaliCapacityException` outside the `exception/` package) was fixed cleanly on request. |
| Correctness (Primary-Flow Walkthrough) | 5/10 | ▲ from 4/10 — the exercised flow (insert, access, evict) traced out correct on the first read, but the one unexercised branch (re-`add` on an existing key) was exactly where the real bug hid; the thread-safety claim had the same shape of gap — correct in practice, but unverified by any test until one was explicitly requested. |
| **Overall** | **3.2/10** | ▲ from 2.4/10 — reflects the escalation and its follow-up fixes folded into this session's final state. |

## What lost points — and the fix

| What I missed | The senior answer | Study link |
|---|---|---|
| `Lru.java:36-38` (first draft) — the existing-key branch of `add` called `removeNode`/`addNodeAtStart` but never updated `node`'s value, and `Node` had no `setValue` method to even call. Verified via scratch test: `add(1,100)` then `add(1,999)` then `get(1)` returned `Optional[100]`, not `999`. | Every mutator that changes *what's stored*, not just *where it sits*, has to update the stored value as its first step, before touching the ordering structure at all. Add the setter, call it first. | [`concepts/answer-framework.md`](../concepts/answer-framework.md#-core-classesinterfaces) |
| `Lru.java:15-17` — `capacity` is accepted with no validation; `new Lru<>(0)` or a negative capacity silently builds a cache that evicts every entry it ever holds, with no error telling the caller why. | Validate the one invariant that makes the rest of the class meaningful (`capacity > 0`) in the constructor, with a named exception — see `ideal/exception/InvalidCapacityException.java`. | [`concepts/answer-framework.md`](../concepts/answer-framework.md#-exception-handling) |
| `Main.java` — the demo never calls `add` twice on the same key, so the value-update bug had zero chance of surfacing from running the demo alone. | Walk every operation named in the problem statement at least once, including the "obvious" ones — "insert or **update**" names two behaviors, not one. | [`concepts/answer-framework.md`](../concepts/answer-framework.md#-primary-flow-walkthrough) |
| No test anywhere asserted the actual thing the escalation is about — safety under concurrent `add`/`get`. The only verification that existed at first was a scratch stress test (20 threads × 2000 ops) run during review and then deleted. | For a requirement phrased as "make X safe for concurrent access," the regression test *is* the deliverable — write a multithreaded test (many threads hammering `add`/`get`, then assert no corruption/overgrowth) in the same sitting as the `synchronized` fix. | [`concepts/answer-framework.md`](../concepts/answer-framework.md#-concurrency--extensibility) §⑧ |
| The new `InvaliCapacityException` (typo'd name, typo'd message, and placed directly in the `practice` package root instead of an `exception` sub-package) — caught on review, not before. | Every other design in this repo keeps custom exceptions in their own `exception/` sub-package; check that convention before placing a new one, and proofread a new public class name once before it's load-bearing. | [`CLAUDE.md`](../CLAUDE.md) (Architecture / conventions) |

## What went well

- The doubly-linked-list-with-sentinels structure (`first`/`last` dummy nodes so no node ever has a null
  `prev`/`next` to special-case) was correctly built and wired on the first attempt — no off-by-one errors in
  `addNodeAtStart`/`removeNode`/`removeLastNode`.
- Once the value-update bug was flagged, the fix applied (`Node.setValue` + calling it in `add`'s existing-key
  branch) was exactly the minimal, correct fix — no overcorrection, no unrelated changes.
- `Main.java`'s demo for the flows it *did* exercise (insert past capacity, access-then-insert) was accurately
  commented with the expected list state at each step, and every one of those comments checked out against the
  actual code.
- The escalation's core fix is exactly right: `synchronized` on both `add` and `get`, sharing one lock, is
  precisely the boundary needed to stop the linked-list pointers and `keyToNode` map from being torn
  mid-mutation — chosen independently, and it matches the boundary `ideal/LRUCache` already uses. Verified
  directly: 20 threads × 2000 interleaved `add`/`get` calls against a capacity-50 cache produced zero
  exceptions and no corruption.
- When asked to fix the exception's typo and move it, the fix was minimal and correct — rename, move file, fix
  message, update the one import — no unrelated changes.
- The requested concurrency test (`LruTest#concurrentAddAndGetNeverCorruptOrOvergrowTheCache`) passed cleanly
  across three separate runs with no flakiness once added.

---

## The ideal design

> **Implemented** in [`src/main/java/com/shubham/app/lru/ideal/`](../src/main/java/com/shubham/app/lru/ideal)
> — see that package's own [`README.md`](../src/main/java/com/shubham/app/lru/ideal/README.md) for the full
> write-up. That package predates this practice session (built directly against the answer framework, since
> there was no mock attempt to review yet); this section summarizes it for a reader who starts here instead.

**Framing sentence:** a cache's correctness is entirely about *when* the recency order changes — an
update-in-place has to move an entry to the front exactly as much as a fresh insert does, and nothing is allowed
to change position or existence outside of one atomic step.

The rest of this section follows [`concepts/answer-framework.md`](../concepts/answer-framework.md)'s 8 steps in
order.

### ① Functional Requirements & Scope

**Actor:** any caller — a cache has exactly one kind of user.

**Operations:**
- Create a cache with a fixed capacity.
- `get(key)` — return the value if present, marking it as the most recently used entry.
- `put(key, value)` — insert or update a value; evict the least recently used entry first if the cache is full
  on a new key.

**Explicitly out of scope:** entry expiration/TTL, persistence, sharding across multiple cache instances, and
cache-wide statistics beyond what `get`'s return value already implies.

### ② Core Entities & Responsibilities

| Entity | Responsibility |
|---|---|
| `LRUCache<K, V>` | Owns the map and the recency ordering; the only place an entry's position or existence ever changes, always as one atomic step. |
| `Node<K, V>` (private, nested) | Pure doubly-linked-list bookkeeping — no identity or behavior worth exposing publicly. |
| `EvictionListener<K, V>` (interface) | The one thing that varies: what happens *after* an entry is evicted. |
| `InvalidCapacityException` | Thrown for a non-positive capacity. |

### ③ Relationships & Class Diagram

- **Has-a** — `LRUCache` has-a `Map<K, Node<K,V>>` and has-a the two sentinel `Node`s (`head`/`tail`).
- **Uses** — `LRUCache` uses an `EvictionListener` (a swappable, optional collaborator — `null` is a valid
  "don't notify anyone" choice, not an error).
- **Implements the contract of** — `LoggingEvictionListener` implements the contract of `EvictionListener`.
- **Is-a** — deliberately absent; no genuine inheritance hierarchy here.

No diagram was drawn for this design, deliberately: there was no mock diagram to review against, and the class
shape (one cache class + one small interface) is simple enough that a diagram wouldn't earn its keep the way it
does for the multi-class designs elsewhere in this repo. See `ideal/README.md` for the full reasoning.

### ④ Pattern Choice & Justification

**Pattern used: Observer** (via `EvictionListener`), **not** Strategy.

Unlike every other `ideal/` package in this repo, *which* entry gets evicted isn't a pluggable policy here —
it's structural, fixed by the doubly-linked-list's own ordering. A genuinely different eviction policy (LFU,
FIFO) needs a different data structure, not a swapped strategy object, so forcing a `EvictionStrategy` interface
would add a pattern nothing asked for. What *does* vary legitimately is what happens **after** eviction —
logging it, writing it to a backing store — and that's Observer: `EvictionListener.onEvict(key, value)` fires
once eviction has already happened, with the cache itself never knowing or caring who's listening.

### ⑤ Core Classes/Interfaces

Signatures only — full implementations are in
[`src/main/java/com/shubham/app/lru/ideal/`](../src/main/java/com/shubham/app/lru/ideal), and tests proving
each class's behavior are in
[`src/test/java/com/shubham/app/lru/ideal/`](../src/test/java/com/shubham/app/lru/ideal) (`LRUCacheTest`):

```java
interface EvictionListener<K, V> { void onEvict(K key, V value); }
class LoggingEvictionListener<K, V> implements EvictionListener<K, V> { }

class LRUCache<K, V> {
    LRUCache(int capacity, EvictionListener<K, V> evictionListener);   // requires capacity > 0
    Optional<V> get(K key);    // Optional.empty() on a miss — a miss is not an error
    void put(K key, V value);
}
```

### ⑥ Exception Handling

- `InvalidCapacityException` — thrown by the constructor for a non-positive capacity.
- Edge case handled **without** an exception: `get` on a missing key returns `Optional.empty()` — a cache miss
  is the expected, routine outcome, not a failure.
- Edge case handled **without** an exception: `put`ting an already-present key just updates its value and moves
  it to the front — no eviction happens, since nothing new was added. (This is exactly the branch the practice
  code got wrong — see "What lost points" above.)

### ⑦ Primary-Flow Walkthrough — the actual fix for this session's bug

`ideal/Main.java`'s demo: `put` two entries into a capacity-2 cache, `get` the first one (marking it recently
used), then `put` a third entry. The eviction removes the **second** entry — never touched again — not the
first one, even though the first was inserted earlier. That is "least recently used" working correctly: recency
of *access*, not of *insertion*, decides who's evicted.

Separately, `LRUCacheTest` also directly covers this session's actual bug: a test that calls `put` twice on the
same key and asserts `get` returns the *second* value, not the first — the exact case the practice code's first
draft got wrong, and the exact case `practice/Main.java` never exercised.

### ⑧ Concurrency & Extensibility

**Concurrency:**
- `get` and `put` are each one `synchronized` method — every mutation to the map or the linked list happens
  under one lock, the same boundary used by every other resource entity in this repo (`ParkingLot`,
  `MeetingRoomBoard`, `OrderBook`, `ConnectionPool`). This is also the exact fix the practice code's
  thread-safety escalation converged on independently.
- `LRUCacheTest#concurrentPutsNeverGrowTheCacheBeyondItsCapacity` fires far more concurrent puts than the
  capacity allows and checks the cache never holds more entries than its capacity; a companion test,
  `LRUCacheTest#concurrentGetsAndPutsNeverCorruptTheCache`, interleaves `get` and `put` together, matching the
  practice code's own `LruTest#concurrentAddAndGetNeverCorruptOrOvergrowTheCache` — the regression test the
  escalation was missing until requested explicitly.

**Extensibility:**
- A new eviction side-effect (write-through to a database, metrics) → one new `EvictionListener`
  implementation, zero changes to `LRUCache`.
- A genuinely different eviction *policy* (LFU, FIFO) is explicitly **not** a drop-in extension of this design —
  see step ④. That would be a different class, not a new `EvictionListener`.

## Takeaways to drill

1. Any mutator that can either *insert* or *update* has two distinct bodies of work: moving within the ordering
   structure, and overwriting the stored payload — write a test that exercises the update path specifically,
   not just the insert path, before trusting the method.
2. A node/record type with "no setter" for a field is a design decision, not an oversight — check it was an
   intentional immutability call, not just the setter nobody got around to writing yet.
3. Validate the one constructor parameter the rest of the class's correctness actually depends on
   (`capacity > 0` here) — a cache that silently can't hold anything is worse than one that fails loudly at
   construction time.
4. "Insert or update" in a problem statement is two behaviors in the same sentence — walk both, even when one of
   them feels like a trivial variant of the other.
5. A requirement phrased as "make X safe for concurrent access" has its own deliverable: a multithreaded test
   that can actually fail if the fix is wrong. Write it in the same sitting as the lock, not as a follow-up.
6. Before adding a new class (even a tiny exception), check the existing convention for where it lives — a new
   one placed at the package root is a one-line fix now but a recurring inconsistency later if it's not caught.

> Rehearse this against [`concepts/answer-framework.md`](../concepts/answer-framework.md) before the next mock.

## How to Improve

Before writing any code, spend two minutes writing the entity-responsibility table and the out-of-scope list —
this session (and most of the last six) shipped a correct-enough implementation with zero written requirements
or diagram, which is exactly the gap that let the real bug hide until a scratch test, rather than a planning
step, found it. The same pattern repeated at the escalation: the lock placement was right immediately, but
proving it was right — the actual deliverable for "make it thread-safe" — only happened after being asked for
separately.
