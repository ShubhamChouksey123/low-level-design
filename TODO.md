# TODO

Interview prep tracker for **Low-Level Design (LLD) / Object-Oriented Design** rounds, targeting **SMTS @ Salesforce**
and onsite LLD/OOD rounds at companies like Google. HLD prep lives in the sibling repo:
https://github.com/ShubhamChouksey123/system-design — this repo stays scoped to class-design, patterns, and
in-memory implementations (no infra/scaling concerns here).

Convention for each new item: new package under `com.shubham.app.<name>` with `entity`/`dao`/`service` sub-packages,
a `Main.java`/`MainClass.java` demo entry point, and a test class under `src/test/java/...`. Update `README.md`'s
list and the class-diagram link when a design is added. See `CLAUDE.md` for build/format/test commands.

## Already covered

- Meeting Scheduler
- Snake and Ladder
- Logging System (Chain of Responsibility)
- Tic-Tac-Toe
- Null Object pattern
- Custom HashMap
- Elevator System (Strategy + State) — `com.shubham.app.elevatorsystem`

## Fundamentals to have crisp, on-demand explanations for

### Basic OOP concepts

Written up in [`concepts/basic-oop-concepts.md`](concepts/basic-oop-concepts.md):

- [x] Encapsulation — with a concrete "why a public field is worse" example from this repo
- [x] Inheritance — including the "favor composition over inheritance" caveat and when inheritance is still right
- [x] Polymorphism — compile-time (overloading) vs. runtime (overriding), tied to a Strategy/State example above
- [x] Abstraction — interface vs. abstract class, and when to reach for which

### UML diagrams (practice drawing these live, not just reading them)

Written up in [`concepts/uml-diagrams.md`](concepts/uml-diagrams.md):

- [x] Class diagram — relationships: association, aggregation, composition, inheritance, realization; multiplicity notation
- [x] Sequence diagram — for at least one multi-object interaction (e.g. Parking Lot ticket issuance, or Ticket Booking seat-lock flow)
- [x] Use case diagram — actors vs. system boundary, for one of the bigger designs (e.g. Ride Sharing or Hotel Booking)

### Design principles

Written up in [`concepts/design-principles.md`](concepts/design-principles.md):

- [x] SOLID — one principle per bullet, each with a concrete before/after refactor example from this repo (not just the definition)
- [x] DRY (Don't Repeat Yourself) — and the counter-caveat: premature DRY-ing unrelated code into a shared abstraction is a smell too
- [x] KISS (Keep It Simple) — tie to this repo's own "no premature abstraction" convention in `CLAUDE.md`
- [x] YAGNI (You Aren't Gonna Need It) — contrast with over-engineering an LLD answer with unused extension points just to "show flexibility"

### Concurrency & extensibility

- [ ] Concurrency: synchronized vs. locks vs. atomic types, deadlock avoidance — tie to Thread Pool / Blocking Queue items below
- [ ] Extensibility trade-offs: composition vs. inheritance, interface segregation — call out explicitly in each new design's package-level notes

## Design patterns to explicitly demonstrate (one canonical example each)

- [x] Strategy — dispatch policy in Elevator System (`DispatchStrategy` / `NearestElevatorDispatchStrategy`)
- [ ] Observer (Notification System / Pub-Sub)
- [x] State — elevator motion in Elevator System (`ElevatorState` / `IdleState`, `MovingUpState`, `MovingDownState`)
- [ ] Factory / Abstract Factory (vehicle or notification-channel creation)
- [ ] Builder (complex object construction, e.g. Booking or Order)
- [ ] Decorator (already partially demonstrated via Logging System's chain — consider a distinct decorator-only example)
- [ ] Singleton (thread-safe, e.g. a config/registry service) — with a note on why it's often overused/anti-pattern in interviews
- [ ] Command (undo/redo, e.g. in Chess or a text editor)
- [ ] Visitor (e.g. traversing File System or an AST)
- [ ] Composite (File System)

## Answer framework (how to run an LLD interview, not just what to build)

A repeatable in-the-room sequence to rehearse until automatic — mirrors the sibling `system-design` repo's
`practice/answer-framework.md`, but scoped to class design instead of architecture.

Written up in [`concepts/answer-framework.md`](concepts/answer-framework.md), with
[`elevatorsystem/README.md`](src/main/java/com/shubham/app/elevatorsystem/README.md) as a fully worked example:

- [x] Clarify functional requirements and scope (which actors, which operations, explicitly *out* of scope)
- [x] Identify core objects/entities and their responsibilities (nouns → classes, verbs → methods)
- [x] Define relationships between entities (is-a vs. has-a vs. uses-a) and sketch the class diagram
- [x] Choose and justify design pattern(s) for the flexible/varying parts of the system
- [x] Write the core classes/interfaces (skip boilerplate getters/setters in a live round — say them, don't type them)
  - [x] Add core method/operation signatures first, across all classes, before fleshing out any one of them
  - [x] Implement only the methods necessary to demonstrate the design — no speculative helpers
- [x] Exception handling — name these explicitly rather than leaving them implicit:
  - [x] Errors (unrecoverable/system-level failures)
  - [x] Edge cases (empty/boundary/concurrent-access scenarios specific to this design)
  - [x] Exceptions (which checked/unchecked exception types the API throws, and why)
  - [x] Invalid input (validation at the boundary — what's rejected and how the caller finds out)
- [x] Walk through the primary flow end-to-end against the classes just drawn (catches gaps before the interviewer does)
- [x] Call out remaining edge cases and concurrency concerns explicitly (even if not fully implemented live)
- [x] State extensibility: what would change to add a new variant, and confirm it's a small, localized change

## Classic LLD/OOD problems to add

Roughly ordered by how frequently they show up in SMTS-level LLD rounds:

- [ ] Parking Lot (multi-level, multiple vehicle/spot types, pricing strategy)
- [ ] LRU Cache / LFU Cache (from scratch, then compare to `java.util.LinkedHashMap` based impl)
- [ ] Rate Limiter (token bucket, sliding window — pluggable strategy)
- [ ] ATM / Vending Machine (State pattern)
- [ ] Splitwise / Expense Sharing (settlement algorithm, group balances)
- [ ] Ride Sharing (Uber/Lyft-style: driver-rider matching, pricing strategy)
- [ ] Hotel / Movie Ticket Booking (BookMyShow-style: seat locking, concurrency around booking)
- [ ] Library Management System
- [ ] Chess Game (Board, Piece hierarchy, move validation, check/checkmate)
- [ ] In-memory Pub-Sub / Event Bus (Observer pattern)
- [ ] Notification System (multi-channel: email/SMS/push, Strategy + Observer)
- [ ] File System (Composite pattern: File/Directory hierarchy)
- [ ] Rule/Workflow Engine (Salesforce-flavored: chained conditions/actions — good fit given target role)
- [ ] Multi-tenant resource quota manager (Salesforce-flavored: per-org limits, useful for SMTS-level enterprise SaaS framing)
- [ ] Thread-safe Bounded Blocking Queue / Producer-Consumer (concurrency primitives from scratch)
- [ ] Thread Pool Executor (from scratch, no `java.util.concurrent`)

## Repo hygiene

- [ ] Add a short `README.md` (or package-level Javadoc) per design summarizing the problem, key classes, and pattern(s) used — started with `elevatorsystem/README.md`; backfill the remaining designs in "Already covered"
- [ ] Add/update class diagrams and link them from `README.md` (mirror the existing diagrams.net link pattern)
- [ ] Ensure every new design has a corresponding test class exercising the core flow, not just the demo `main`
