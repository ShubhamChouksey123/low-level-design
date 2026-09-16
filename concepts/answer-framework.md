# Low-Level Design Interview — Answer Framework (Playbook)

> A step-by-step scaffold for **structuring your answer** to any LLD/OOD problem. Run it top-to-bottom in the room
> so nothing important is missed. Mirrors the sibling `system-design` repo's
> [`practice/answer-framework.md`](https://github.com/ShubhamChouksey123/system-design/blob/master/practice/answer-framework.md)
> — that one is the *content checklist* for architecture rounds (estimation, data model, trade-offs); this one is
> the equivalent for class-design rounds. The 8 steps below are the same ones tracked as a checklist in
> [`TODO.md`](../TODO.md)'s "Answer framework" section — this doc is the expanded, worked-through version of that
> checklist.

## At a glance — the 8 steps (and where they land in a 45-min LLD round)

| Phase | Time | Steps to cover here |
|---|---|---|
| 1. Clarify & identify | ~5–8 min | ① Functional requirements & scope · ② Core entities & responsibilities |
| 2. Model | ~8–12 min | ③ Relationships & class diagram · ④ Pattern choice & justification |
| 3. Code | ~15–20 min | ⑤ Core classes/interfaces · ⑥ Exception handling |
| 4. Verify & extend | ~5–10 min | ⑦ Primary-flow walkthrough · ⑧ Concurrency & extensibility |

> **Golden rule:** name what's explicitly *out of scope* before designing anything, and justify every pattern by
> pointing at the specific requirement it protects against — "I used Strategy because X will change" beats "I used
> Strategy because it's good practice," every time.

---

## ① Functional Requirements & Scope

**Goal:** pin down *what the system does* — the actors and the concrete operations they can perform. Designing
the wrong scope is an automatic miss, same as in an HLD round.

**Produce:** a short list of actors, a short list of operations, and an explicit out-of-scope list — confirmed
with the interviewer before you name a single class.

**Checklist:**
- Who are the actors? (Usually 1–2 for an LLD problem — over-modeling actors is a sign you're scoping too wide.)
- What are the core operations, stated as verbs? (e.g. "request an elevator," "select a destination floor," not
  "manage the elevator system.")
- What's explicitly **out of scope**? Naming this is half the point — an interviewer who doesn't hear it assumes
  you forgot it, not that you considered and excluded it.
- Is there a hidden non-functional requirement worth stating up front? (Concurrent access from multiple actors,
  a strict ordering guarantee, an extensibility axis the prompt hints at.)

---

## ② Core Entities & Responsibilities

**Goal:** turn the nouns and verbs from ① into a first pass at classes and methods — before worrying about how
they relate to each other.

**Produce:** a flat list — one line per entity, one sentence per responsibility. No relationships yet, no code.

**Checklist:**
- Nouns in the problem statement → candidate classes.
- Verbs attached to each noun → candidate methods on that class.
- For each entity, can you state its responsibility in one sentence *without* using "and"? If not, it's two
  entities (this is Single Responsibility Principle, applied at design time rather than as a post-hoc label — see
  [`concepts/design-principles.md`](design-principles.md)).
- Is there a concept in the problem that varies (a policy, a status, a mode)? Flag it now — it's a strong signal
  for step ④.

---

## ③ Relationships & Class Diagram

**Goal:** connect the entities from ② with the *right kind* of relationship, and put it on the whiteboard as an
actual class diagram — not just a verbal description.

**Produce:** a class diagram (see [`concepts/uml-diagrams.md`](uml-diagrams.md) §1 for notation), drawn
nouns-first, relationships-second, member-details-last.

**Checklist:**
- For every pair of connected entities, name the relationship: **is-a** (inheritance/realization), **has-a**
  (aggregation/composition), or **uses-a** (association). Getting this wrong here compounds into every later step.
- Prefer composition over inheritance by default — only use inheritance for a genuine is-a relationship where a
  subtype is fully substitutable for its supertype (Liskov Substitution Principle; see
  [`concepts/basic-oop-concepts.md`](basic-oop-concepts.md) §2).
- Mark multiplicities (`1`, `0..1`, `*`) on each relationship — this is often where a hidden requirement (can an
  entity exist without its "owner"?) surfaces.
- Sanity-check the diagram against ①'s operations: does every operation have a clear home among these classes?

---

## ④ Pattern Choice & Justification

**Goal:** identify the *varying* part of the system (flagged in ②) and isolate it behind an interface, using the
design pattern that fits the shape of the variation — not the pattern you happen to remember best.

**Produce:** one interface + N implementations for each varying concern, with a one-sentence justification each.

**Checklist:**
- Does behavior vary by an object's **type/mode over time** (idle vs. moving, draft vs. published)? → **State**.
- Does behavior vary by a **pluggable policy** the caller picks (pricing rule, dispatch rule)? → **Strategy**.
- Does one event need to **notify multiple, decoupled listeners**? → **Observer**.
- Does construction need to vary by **type** without the caller knowing the concrete class? → **Factory**.
- Is there a **multi-step construction** with optional parts? → **Builder**.
- For each pattern picked, say the *why* out loud: "if requirement X changes, this is one new class, not an edit
  to an existing one" (Open/Closed Principle — see [`concepts/design-principles.md`](design-principles.md)).
- **Resist the urge to add a pattern nothing asked for.** If there's only one implementation and no stated
  variation axis, a plain class beats a Strategy interface with one implementation (YAGNI).

---

## ⑤ Core Classes / Interfaces

**Goal:** put actual method signatures on the board — this is where the design becomes checkable.

**Produce:** signatures for every class from ③/④, filled in one pass across *all* classes before fleshing out any
single one.

**Checklist:**
- Add signatures first, across every class, before implementing any method body — this catches missing methods
  early, before you've sunk time into one class's internals.
- Implement only the methods the walkthrough (⑦) will actually exercise — no speculative helpers "in case they're
  asked for."
- Skip boilerplate getters/setters out loud ("I'd generate these") rather than typing them — that time is worth
  more spent elsewhere.
- Keep field visibility as tight as the design allows — if a state/strategy class in another package needs to
  mutate something, expose an intention-revealing method (`scheduleStop(floor)`), not the raw collection (see
  [`concepts/basic-oop-concepts.md`](basic-oop-concepts.md) §1).

---

## ⑥ Exception Handling

**Goal:** name the failure modes explicitly rather than leaving them implicit — this is one of the fastest ways to
read as senior in the room.

**Produce:** a short, spoken list mapped onto the four categories below, with at least the most important one or
two actually implemented as a custom exception type.

**Checklist:**
- **Errors** — unrecoverable/system-level failures (e.g. "no elevators configured"). Usually an unchecked
  exception with no expectation of recovery.
- **Edge cases** — boundary/empty/concurrent-access scenarios specific to *this* design (e.g. a request for the
  entity's current position, which should be a no-op, not an error).
- **Exceptions** — which checked/unchecked types the public API actually throws, and why each one is checked vs.
  unchecked.
- **Invalid input** — validation at the boundary: what's rejected, and how does the caller find out (exception
  type + message, not a silently-ignored no-op unless that's the deliberate edge-case behavior above)?

---

## ⑦ Primary-Flow Walkthrough

**Goal:** trace one end-to-end use case against the classes just written — this is where gaps in ③–⑤ get caught
*by you*, before the interviewer catches them.

**Produce:** a spoken (or sketched) sequence diagram for the single most important flow — see
[`concepts/uml-diagrams.md`](uml-diagrams.md) §2 for notation, and
[`src/main/java/com/shubham/app/elevatorsystem/README.md`](../src/main/java/com/shubham/app/elevatorsystem/README.md)
§7 for a fully worked example.

**Checklist:**
- Pick the single flow that touches the most classes — it's the highest-value trace.
- Walk it call-by-call: which object calls which method on which other object, in order.
- Does every step land on a method that actually exists from ⑤? If not, that's the gap ③/④/⑤ missed.
- If there's a second interesting flow (e.g. the reverse operation), name it verbally rather than diagramming
  both in full — time is the scarcest resource in the room.

---

## ⑧ Concurrency & Extensibility

**Goal:** close with the two questions a senior-level interviewer is most likely to ask if you don't volunteer
them first.

**Produce:** a spoken answer for each, with at least the concurrency answer backed by something concrete in the
code if time allows (e.g. a `synchronized` method, or a note on which method is the actual mutation boundary).

**Checklist:**
- **Concurrency:** can two actors call this system at the same time? If so, name the shared mutable state and how
  it's protected (or explicitly state it *isn't*, and why that's an acceptable scope cut for this session).
- Call out the *specific* race that would occur without protection — "two threads calling `add()` on the same
  unsynchronized `TreeSet` can corrupt it" is a much stronger answer than "we'd need thread safety."
- **Extensibility:** pick one plausible new requirement (a new mode, a new policy) and state exactly what class
  you'd add — confirm it's additive, not an edit to an existing class. This is the payoff of ④'s pattern choices,
  made concrete.

---

## Blank template (copy into each practice attempt)

```
Problem: ________________________________________________

① Functional requirements & scope
- actors / operations:
- in scope / out of scope:

② Core entities & responsibilities
- entity → one-sentence responsibility (no "and"):

③ Relationships & class diagram
- is-a / has-a / uses-a per pair, multiplicities:

④ Pattern choice & justification
- varying concern → pattern → "why this pattern, why now":

⑤ Core classes/interfaces
- signatures across all classes, then fill in bodies:

⑥ Exception handling
- errors · edge cases · exceptions · invalid input:

⑦ Primary-flow walkthrough
- the one flow, traced call-by-call:

⑧ Concurrency & extensibility
- shared mutable state + protection (or explicit scope cut):
- one new requirement → the one class you'd add:
```

## One-paragraph summary

Run the same eight steps every time, in order: clarify scope before naming classes, list entities before
connecting them, connect them before picking patterns, pick patterns before writing signatures, write signatures
before bodies, name failure modes before an interviewer asks about them, walk the primary flow to catch your own
gaps, and close with concurrency and extensibility so the interview ends on senior-level judgment rather than
"I ran out of time." `src/main/java/com/shubham/app/elevatorsystem/README.md` is this framework run start-to-finish
against a real problem — read it alongside this doc the first few times through.
