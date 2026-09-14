# Design Principles — SOLID, DRY, KISS, YAGNI

These are the *justifications* you reach for after drawing a class diagram — "I split this into two classes
because of SRP," "I made this an interface because of DIP." Naming a principle without pointing at the specific
line it fixes reads as reciting a definition; the examples below are deliberately before/after so you can practice
saying the "because" part out loud.

## SOLID

### S — Single Responsibility Principle

A class should have one reason to change. Not "one method" — one *axis of change*.

**Before:** `MeetingSchedulerService` doing scheduling *and* clash-detection math *and* room persistence would have
three reasons to change (new booking rule, new clash algorithm, new storage backend) all landing in one class.

**After — how this repo actually splits it:**

```java
// Reason to change #1: how clashes are computed
class HelperUtils {
    boolean isClashing(Meeting meeting, Date startDate, Date endDate) { /* ... */ }
}

// Reason to change #2: where rooms are stored/looked up
class RoomDao {
    Room getRoomById(Integer roomId) { /* ... */ }
}

// Reason to change #3: the scheduling policy itself — delegates the other two concerns out
class MeetingSchedulerService {
    private final HelperUtils helperUtils;
    private final RoomDao roomDao;
    void createMeeting(Integer roomId, Person head, List<Person> members, Date start, Date end) { /* ... */ }
}
```

**Interview tip:** if you can't name the *one* reason a class would change, or you find yourself saying "and
also," it's two classes.

### O — Open/Closed Principle

A class should be open for extension, closed for modification — add new behavior via new code, not by editing
existing, already-tested code.

**Before — adding a new room-pricing rule means editing this method every time:**

```java
double priceFor(Room room, int hours) {
    if (room.getType() == RoomType.STANDARD) return hours * 10;
    else if (room.getType() == RoomType.CONFERENCE) return hours * 25;
    else if (room.getType() == RoomType.EXECUTIVE) return hours * 50; // every new type edits this method
}
```

**After — Strategy pattern, new room types are pure addition:**

```java
interface PricingStrategy {
    double priceFor(int hours);
}

class StandardPricing implements PricingStrategy {
    public double priceFor(int hours) { return hours * 10; }
}

class ExecutivePricing implements PricingStrategy {
    public double priceFor(int hours) { return hours * 50; }
}

// Adding a new room type = a new class. MeetingSchedulerService is never touched again.
```

**Interview tip:** OCP is the principle behind almost every Strategy/State/Visitor example in
`concepts/../TODO.md`'s design-patterns list — when you justify picking a pattern, OCP is usually the "why."

### L — Liskov Substitution Principle

A subtype must be usable anywhere its supertype is expected without the caller noticing a behavior difference.
Covered with the classic `Square extends Rectangle` violation in
[`concepts/basic-oop-concepts.md`](basic-oop-concepts.md#2-inheritance) §2 — re-read that if the name alone doesn't
click, since it's really an inheritance-modeling principle wearing a SOLID initial.

### I — Interface Segregation Principle

Don't force a class to implement methods it doesn't use — split a fat interface into smaller, role-specific ones.

**Before — a single `RoomOperations` interface forces every implementer to support admin methods:**

```java
interface RoomOperations {
    Room getRoomById(Integer id);
    void addRoom(Room room);
    void deleteRoom(Integer id);       // only an admin tool needs this
    void setMaintenanceMode(Integer id); // only an admin tool needs this
}

class RoomDao implements RoomOperations {
    // forced to implement deleteRoom/setMaintenanceMode even though the
    // scheduling flow never calls them — probably throws UnsupportedOperationException
}
```

**After — split by caller role:**

```java
interface RoomReader {
    Room getRoomById(Integer id);
}

interface RoomAdmin extends RoomReader {
    void addRoom(Room room);
    void deleteRoom(Integer id);
    void setMaintenanceMode(Integer id);
}

// MeetingSchedulerService only depends on RoomReader — it can't even accidentally call delete/maintenance methods
```

**Interview tip:** this is ISP's actual payoff — the *caller's* dependency gets narrower, not just the
implementer's obligation gets lighter. Say that explicitly when justifying a split.

### D — Dependency Inversion Principle

High-level modules should depend on abstractions, not on concrete low-level modules; concrete implementations
depend on the abstraction too, so the dependency arrow points at the interface from both sides.

**Already demonstrated in this repo:** `nullobject.service.MainClass` depends on `UserDao` (the abstraction), not
`UserDaoImpl` directly — see [`concepts/basic-oop-concepts.md`](basic-oop-concepts.md#4-abstraction) §4 for the full
code example. DIP is *why* that seam exists: it's what lets `UserDaoImpl` be swapped for a different storage
backend, or a test double, without `MainClass` changing at all.

**Interview tip:** DIP and "dependency injection" are related but not the same thing — DIP is the *design
principle* (depend on abstractions); constructor injection (like `MeetingSchedulerService(RoomDao roomDao)`) is
just *one mechanical way* to satisfy it. You can violate DIP even while using a DI framework, if the abstraction
you're injecting is still a concrete class.

## DRY — Don't Repeat Yourself

Every piece of knowledge should have one authoritative representation in the system. The target is *duplicated
knowledge*, not duplicated *characters* — two methods that happen to look similar but encode different business
rules are not a DRY violation.

**Before — the same clash-detection logic re-derived in two places:**

```java
// Inside MeetingSchedulerService.createMeeting
if (startDate.after(m.getStartDate()) && startDate.before(m.getEndDate())) { /* clash */ }

// Inside a hypothetical MeetingSchedulerService.rescheduleMeeting, re-typed slightly differently
if (newStart.after(m.getStartDate()) && newStart.before(m.getEndDate())) { /* clash — same rule, drifted copy */ }
```

If the clash rule ever changes (e.g. to also reject exact-boundary overlaps), only one of the two copies gets
fixed — this is the actual cost DRY is protecting against, not the extra typing.

**After:** exactly what this repo does — both call sites go through the single `HelperUtils.isClashing(...)`.

**The counter-caveat:** don't extract a shared helper just because two blocks of code *look* similar today. If
`isClashing` and, say, a hypothetical "is a person double-booked" check happen to share a few lines of date-range
comparison but encode genuinely different business rules, forcing them into one shared method just because the
code *resembles* itself is the "premature abstraction" mistake — you'd be coupling two unrelated rules to a single
change point. DRY targets duplicated *knowledge*; coincidentally similar code is not automatically duplicated
knowledge.

## KISS — Keep It Simple

Prefer the simplest design that correctly satisfies the requirements — complexity should be justified by an actual
requirement, not added preemptively "to be safe" or "to look thorough" in an interview.

This is the same convention already stated in this repo's [`CLAUDE.md`](../CLAUDE.md): *"Don't add features,
refactor, or introduce abstractions beyond what the task requires... Three similar lines is better than a
premature abstraction."*

**In an LLD interview specifically:** KISS shows up as resisting the urge to add a generic `Visitor` or a plugin
registry to a design the interviewer scoped down to two variants. If asked "how would this extend to N variants?"
— answer the question, but don't pre-build the machinery for N when the stated requirement is 2.

## YAGNI — You Aren't Gonna Need It

Don't build a capability until a requirement actually demands it — a close cousin of KISS, but specifically aimed
at *speculative future-proofing* rather than *present-day complexity*.

**Contrast with over-engineering an LLD answer:**

```java
// YAGNI violation: the interviewer asked for two payment methods (Card, UPI).
// Building a full plugin-loader for "future payment methods we might add" answers
// a question nobody asked and eats time you needed for the actual requirements.
interface PaymentPluginLoader {
    void registerPlugin(String name, Class<? extends PaymentMethod> pluginClass);
    PaymentMethod loadPlugin(String name) throws ReflectiveOperationException;
}

// YAGNI-respecting version: a Strategy interface with the two implementations actually asked for.
// It's still open for extension (OCP) — adding a third PaymentMethod later is a small, localized
// change — but nothing was pre-built for extensions that were never requested.
interface PaymentMethod {
    void pay(double amount);
}
class CardPayment implements PaymentMethod { public void pay(double amount) { /* ... */ } }
class UpiPayment implements PaymentMethod { public void pay(double amount) { /* ... */ } }
```

**Interview tip:** YAGNI and OCP are not in tension — OCP says *make the seam easy to extend when a new
requirement lands*; YAGNI says *don't build the extension itself before the requirement lands*. A Strategy
interface with only the currently-required implementations satisfies both at once.

## One-paragraph summary

SOLID is five separate justifications for specific class-splitting decisions (SRP: one reason to change; OCP:
extend via new code, not edits; LSP: subtypes must honor the supertype's contract; ISP: narrow interfaces per
caller role; DIP: depend on abstractions from both sides) — always pair the principle's name with the specific
line it explains. DRY targets duplicated *knowledge*, not superficially similar code — don't force-merge two rules
that happen to look alike today. KISS and YAGNI are the counterweight to over-engineering an interview answer:
KISS resists unnecessary present-day complexity, YAGNI resists building for hypothetical future requirements —
both are really the same convention this repo's own `CLAUDE.md` already states.
