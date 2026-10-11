# LRU Cache — Ideal Design (implemented)

This package was originally written directly against
[`concepts/answer-framework.md`](../../../../../../../concepts/answer-framework.md)'s 8 steps, since there was
no mock/practice attempt to review yet at the time. A practice attempt now exists at
[`practice/`](../practice) and has been reviewed — see
[`practice/session-11-lru-cache.md`](../../../../../../../practice/session-11-lru-cache.md) for that review and
how it compares against this design.

## ① Functional Requirements & Scope

**Actor:** Any caller — a cache has exactly one kind of user.

**Operations:**
- Create a cache with a fixed capacity.
- `get(key)` — return the value if present, marking it as the most recently used entry.
- `put(key, value)` — insert or update a value. If the cache is already at capacity and this is a new key,
  evict the **least recently used** entry first.

**Explicitly out of scope:** entry expiration/TTL, persistence, sharding across multiple cache instances, and
cache-wide statistics (hit rate, size) beyond what a caller can already infer from `get`'s return value.

## ② Core Entities & Responsibilities

| Entity | Responsibility |
|---|---|
| `LRUCache<K, V>` | Owns the map and the recency ordering; the *only* place an entry's position or existence ever changes, always as one atomic step. |
| `Node<K, V>` (private, nested) | Pure doubly-linked-list bookkeeping — no identity or behavior worth exposing, so it never became its own file (see "What's different" below). |
| `EvictionListener<K, V>` (interface) | The one thing that varies: what happens when an entry is evicted. `LoggingEvictionListener` is the one implementation. |
| `InvalidCapacityException` | Thrown for a non-positive capacity. |

## ③ Relationships & Class Diagram

- **Has-a** — `LRUCache` has-a `Map<K, Node<K,V>>` and has-a the two sentinel `Node`s (`head`/`tail`).
- **Uses** — `LRUCache` uses an `EvictionListener` (a swappable, optional collaborator — `null` is a valid "don't
  notify anyone" choice, not an error).
- **Implements the contract of** — `LoggingEvictionListener` implements the contract of `EvictionListener`.
- **Is-a** — deliberately absent; there's no genuine inheritance hierarchy here.

No diagram was drawn for this design — there's no mock attempt's diagram to review against, and the class shape
here (one cache class + one small interface) is simple enough that a class diagram wouldn't earn its keep the way
it does for the multi-class designs elsewhere in this repo.

## ④ Pattern Choice & Justification

**Pattern used: Observer** (via `EvictionListener`), **not** Strategy.

Every other `ideal/` package in this repo uses Strategy for its "the one thing that varies" pattern
(`DispatchStrategy`, `SpotAllocationStrategy`, `RoomAllocationStrategy`, `TradePricingStrategy`,
`ConnectionAssigningStrategy`). LRU cache is different: *which* entry gets evicted isn't a pluggable policy here
— it's structural, determined entirely by the doubly-linked-list's own ordering. A genuinely different eviction
policy (LFU, FIFO) needs a different underlying data structure, not a swapped comparator or strategy object — so
forcing a `EvictionStrategy` interface here would be exactly the "don't add a pattern nothing asked for" mistake
the framework's own golden rule warns against.

What *does* vary legitimately is what happens **after** an eviction decision is made — logging it, writing it to
a backing store, invalidating a downstream cache. That's Observer: `EvictionListener.onEvict(key, value)` is
called once eviction has already happened, and the cache itself doesn't know or care who's listening (`null` is
a valid "nobody's listening" choice).

## ⑤ Core Classes/Interfaces

Signatures only — full implementations are in
[`src/main/java/com/shubham/app/lru/ideal/`](../../../../../../../src/main/java/com/shubham/app/lru/ideal), and
tests proving each behavior are in
[`src/test/java/com/shubham/app/lru/ideal/`](../../../../../../../src/test/java/com/shubham/app/lru/ideal)
(`LRUCacheTest`):

```java
interface EvictionListener<K, V> { void onEvict(K key, V value); }
class LoggingEvictionListener<K, V> implements EvictionListener<K, V> { }

class LRUCache<K, V> {
    LRUCache(int capacity, EvictionListener<K, V> evictionListener);   // requires capacity > 0
    Optional<V> get(K key);    // Optional.empty() on a miss — a miss is not an error
    void put(K key, V value);
}
```

## ⑥ Exception Handling

- `InvalidCapacityException` — thrown by the constructor for a non-positive capacity.
- Edge case handled **without** an exception: `get` on a missing key returns `Optional.empty()`. A cache miss is
  the expected, routine outcome of calling `get` — not a failure.
- Edge case handled **without** an exception: `put`ting an already-present key just updates its value and moves
  it to the front — no eviction happens, because nothing new was added.

## ⑦ Primary-Flow Walkthrough

`Main.java`'s demo: put two entries into a capacity-2 cache, `get` the first one (which marks it as recently
used), then `put` a third entry. The eviction removes the **second** entry — the one that was never touched again
— not the first one, even though the first one was inserted earlier. That's the entire point of "least recently
used": recency of *access*, not recency of *insertion*, decides who gets evicted.
`LRUCacheTest#accessingAnEntryProtectsItFromEviction` is the regression test for this exact behavior.

## ⑧ Concurrency & Extensibility

**Concurrency:**
- `get` and `put` are each one `synchronized` method — every mutation to the map or the linked list happens
  under one lock, the same boundary used by every other resource entity in this repo
  (`ParkingLot`, `MeetingRoomBoard`, `OrderBook`, `ConnectionPool`).
- A test (`LRUCacheTest#concurrentPutsNeverGrowTheCacheBeyondItsCapacity`) fires far more concurrent puts than the
  capacity allows and checks the cache never holds more entries than its capacity.
- A companion test (`LRUCacheTest#concurrentGetsAndPutsNeverCorruptTheCache`) interleaves `get` and `put` from
  many threads at once, exercising both entry points together rather than `put` in isolation — mirroring the
  practice package's own thread-safety-escalation test.

**Extensibility:**
- A new eviction side-effect (write-through to a database, metrics) → one new `EvictionListener` implementation,
  zero changes to `LRUCache`.
- A genuinely different eviction *policy* (LFU, FIFO) is explicitly **not** a drop-in extension of this design —
  see step ④. That would be a different class, not a new `EvictionListener`.

## What's different from this repo's other `ideal/` designs

- **No `*CreationService`.** `ParkingLotCreationService`, `MeetingRoomCreationService`, and
  `ConnectionPoolCreationService` all exist because building their respective resource pools involves real setup
  logic (building N `Spot`/`Room`/`Connection` objects from a count). `LRUCache`'s constructor *is* that setup —
  wrapping `new LRUCache<>(capacity, listener)` in a service that does nothing but call it would be a class that
  exists for no reason.
- **`Node` is a private nested class**, not its own file. Every other design's core entities
  (`Spot`, `Room`, `Order`, `Connection`) have public identity and behavior other classes call into. `Node` has
  neither — it's pure internal bookkeeping for the linked list, so keeping it private is *more* encapsulated, not
  less, than giving it its own public file.
