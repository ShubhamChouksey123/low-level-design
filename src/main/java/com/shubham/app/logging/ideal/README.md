# Logging Library — Ideal Design (implemented)

Written against [`concepts/answer-framework.md`](../../../../../../../../concepts/answer-framework.md)'s 8 steps after
the Enterpret interview attempt (in the parent [`logging/`](..) package) was reviewed — see
[`practice/session-12-logging-library.md`](../../../../../../../../practice/session-12-logging-library.md) for the
review and for every step's full write-up. This file only records what this package does and the decisions that
differ from the attempt's README.

## What it does

`LoggerFactory` hands out one `Logger` per class. `logger.info("...")` builds a `LogEvent` (timestamp, level,
logger name, thread name, trace id, message) **on the caller's thread**, then hands it to every configured
`Appender`. `ConsoleAppender` and `FileAppender` write synchronously; wrapping one in `AsyncAppender` makes the call
return after an enqueue, with a single worker thread delivering to the wrapped appender and flushing it every
`batchSize` events or `flushInterval`, whichever comes first.

## Decisions that differ from the attempt's README

- **One queue and one worker per destination, not one queue per class.** A queue per class means a thread per
  class and a destination that interleaves several queues in no defined order. One queue per destination keeps
  arrival order and uses one thread per slow destination.
- **Thread name and trace id are captured by the caller, not looked up by the consumer.** An async worker runs on
  a different thread, so a lookup there returns the worker's thread and no trace id at all.
- **`BLOCK` or `DROP_NEWEST` is an explicit choice** (`OverflowPolicy`) instead of an unbounded queue that hides
  the problem until memory runs out.
- **A failing appender never reaches the caller or the other appenders.**

## Known limits

- Events still queued when the process is killed (not shut down cleanly) are lost. `LoggerFactory.shutdown()` is
  the only thing that drains them; wiring it to a JVM shutdown hook is left to the application.
- Events appended concurrently with `close()` may be dropped (they are counted in `getDroppedCount()`).
- No log rotation, no per-appender level, no structured (JSON) formatter — each is one new class, see
  step ⑧ in the session file.

## Run

```bash
./mvnw compile exec:java -Dexec.mainClass=com.shubham.app.logging.ideal.Main
./mvnw test -Dtest='com.shubham.app.logging.ideal.**.*Test'
```
