# Concurrency Primitives

Asked in almost every LLD round the moment a design has shared mutable state touched by more than one thread
(a connection pool, an order book, a cache). Being able to *name* the right primitive isn't the bar; being able to
say *why this one over that one* for the specific access pattern is.

## 1. `synchronized`

Intrinsic lock on an object/method — only one thread executes the guarded block at a time. Reentrant (a thread
already holding the lock can re-enter without blocking itself).

```java
public synchronized void withdraw(double amount) {
    if (amount > balance) throw new IllegalArgumentException("insufficient funds");
    balance -= amount;
}
```

**When to reach for it:** simplest mutual exclusion case, no need for timeouts, `tryLock`, or interrupting a
waiting thread. In this repo: the elevator system's naive single-lock approach before the Strategy/State
refactor — see
[`elevatorsystem/entity/Elevator.java`](../src/main/java/com/shubham/app/elevatorsystem/entity/Elevator.java).

## 2. `ReentrantLock`

Explicit lock with the same reentrancy guarantee as `synchronized`, but adds `tryLock()` (non-blocking attempt),
`tryLock(timeout)`, `lockInterruptibly()`, and an optional fairness policy (`new ReentrantLock(true)`).

```java
private final ReentrantLock lock = new ReentrantLock();

public boolean tryWithdraw(double amount) {
    if (!lock.tryLock()) return false; // don't block — bail out instead
    try {
        if (amount > balance) return false;
        balance -= amount;
        return true;
    } finally {
        lock.unlock();
    }
}
```

**When to reach for it over `synchronized`:** need a timeout/non-blocking attempt, need to lock/unlock across
non-nested method boundaries, or need fairness (first-come-first-served) to avoid starving a waiting thread.
**Gotcha:** `unlock()` must be in a `finally` block — `synchronized` releases automatically on exit/exception,
`ReentrantLock` does not.

**Runnable demo:**
[`concurrencyprimitives/reentrantlock/BankAccount.java`](../src/main/java/com/shubham/app/concurrencyprimitives/reentrantlock/BankAccount.java) +
[`Main.java`](../src/main/java/com/shubham/app/concurrencyprimitives/reentrantlock/Main.java) — demonstrates
mutual exclusion under real concurrent withdrawals, `tryLock()` returning `false` immediately instead of
blocking, `tryLock(timeout)` waiting then succeeding, and reentrancy itself (the same thread re-locking without
deadlocking). Run with
`./mvnw compile exec:java -Dexec.mainClass=com.shubham.app.concurrencyprimitives.reentrantlock.Main`.

## 3. `Semaphore`

Counter-based permit system — up to N threads can hold a permit concurrently; the (N+1)th blocks until one is
released.

```java
private final Semaphore pool = new Semaphore(MAX_CONNECTIONS);

public Connection acquire() throws InterruptedException {
    pool.acquire(); // blocks if all permits are checked out
    return getNextConnection();
}

public void release(Connection c) {
    returnConnection(c);
    pool.release();
}
```

**When to reach for it:** bounding concurrent access to a fixed-size resource pool. In this repo: the natural fit
for `session-06-connection-pool.md`'s "fixed number of active connections" requirement.

**Runnable demo:**
[`concurrencyprimitives/semaphore/ConnectionPool.java`](../src/main/java/com/shubham/app/concurrencyprimitives/semaphore/ConnectionPool.java) +
[`Main.java`](../src/main/java/com/shubham/app/concurrencyprimitives/semaphore/Main.java) — demonstrates
concurrent access actually staying bounded at the permit count under load, `acquire()` blocking until a permit
is released, and `tryAcquire()` returning `false` immediately instead of waiting. Run with
`./mvnw compile exec:java -Dexec.mainClass=com.shubham.app.concurrencyprimitives.semaphore.Main`.

## 4. `volatile`

Guarantees **visibility** of writes across threads (no thread caches a stale value in a local register/cache
line) — it does **not** guarantee atomicity for compound operations.

```java
private volatile boolean running = true; // one thread writes, others read

public void stop() { running = false; }      // visible to all threads immediately
public void run() { while (running) { ... } } // other threads see the update, not stale
```

**❌ Common mistake:** using `volatile` for a counter.

```java
private volatile int count = 0;
count++; // NOT atomic — read-modify-write is three steps, a race can still interleave
```

**When to reach for it:** a single flag/reference read by multiple threads where only one thread ever writes to
it. The moment two threads can both *write* based on the current value, `volatile` alone isn't enough — reach for
an atomic type or a lock instead.

**Runnable demo:**
[`concurrencyprimitives/volatiledemo/StoppableWorker.java`](../src/main/java/com/shubham/app/concurrencyprimitives/volatiledemo/StoppableWorker.java) +
[`VolatileCounter.java`](../src/main/java/com/shubham/app/concurrencyprimitives/volatiledemo/VolatileCounter.java) +
[`Main.java`](../src/main/java/com/shubham/app/concurrencyprimitives/volatiledemo/Main.java) — demonstrates the
visibility guarantee (a flag write from one thread stops another thread's loop almost immediately) and the
common mistake above under real contention (`count++` from 10 threads loses updates even though every read sees
the latest write). Run with
`./mvnw compile exec:java -Dexec.mainClass=com.shubham.app.concurrencyprimitives.volatiledemo.Main`.

## 5. Atomic Types (`AtomicInteger`, `AtomicLong`, `AtomicReference`)

Lock-free atomic read-modify-write via CAS (compare-and-swap) — the hardware retries the operation if another
thread changed the value in between, instead of blocking.

```java
private final AtomicInteger count = new AtomicInteger(0);

public int increment() {
    return count.incrementAndGet(); // atomic, no lock needed
}
```

**When to reach for it:** single-variable counters/state transitions under contention — avoids lock overhead
entirely. **Don't** reach for it when an operation spans multiple variables that must change together (that needs
a lock, since CAS only atomically updates one reference/primitive at a time).

---

## Quick Decision Table

| Need | Reach for |
|---|---|
| Guard a block of code, simplest case | `synchronized` |
| Need `tryLock`/timeout/fairness | `ReentrantLock` |
| Bound concurrent access to N resources | `Semaphore` |
| One writer, many readers, single flag/reference | `volatile` |
| Single-variable counter under contention | `AtomicInteger`/`AtomicLong` |
| Multi-variable invariant must change atomically | A lock (`synchronized` or `ReentrantLock`), not `volatile`/atomics |

**Interview tip:** if asked "why not just use `synchronized` everywhere?" — locks add contention/deadlock risk and
block the thread; atomics are lock-free and faster for single-variable ops; `volatile` is cheaper still but only
covers visibility, not atomicity. Picking the cheapest primitive that's still *correct* for the access pattern is
the senior signal here, not reaching for the heaviest tool by default.
