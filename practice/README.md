# LLD Practice — Mock Interview Log

Mirrors the sibling `system-design` repo's
[`practice/README.md`](https://github.com/ShubhamChouksey123/system-design/blob/master/practice/README.md) —
same shape (honest post-mortem + scorecard + reference "ideal design" + drillable takeaways), adapted for
LLD/OOD rounds instead of architecture rounds. The five scored axes below are this repo's LLD equivalent of that
tracker's fixed axes, mapped onto [`concepts/answer-framework.md`](../concepts/answer-framework.md)'s 8 steps.

Each mock session gets its **own file** (`session-NN-<problem>.md`) — this README stays a short index: the
Sessions table plus the two tracker sections that aggregate *across* every session (`Consolidated Tips`,
`Recurring Action Items`). Log every future mock as a new row here + a new `session-NN-<problem>.md` file — see
[`GUIDELINES.md`](GUIDELINES.md) for the required structure and a copy-paste template.

## Sessions

| # | Problem | Requirements & Entities | Class Diagram | Pattern Choice | Implementation & Exceptions | Correctness (Walkthrough) | Overall | Verdict |
|---|---|:---:|:---:|:---:|:---:|:---:|:---:|---|
| 01 | [Snake and Ladder](session-01-snake-and-ladder.md) | 5/10 | 5/10 | 2/10 | 5/10 | 2/10 | **4/10** | ❌ Needs work |
| 02 | [Parking Lot](session-02-parking-lot.md) | 5/10 | 6/10 | 2/10 | 5/10 | 4/10 | **4.4/10** | ❌ Needs work |
| 03 | [Meeting Room Scheduler](session-03-meeting-room-scheduler.md) | 6/10 | 5/10 | 1/10 | 4/10 | 2/10 | **3.6/10** | ❌ Needs work |
| 04 | [Stock Trading & Matching](session-04-stock-trading.md) | 5/10 | 1/10 | 1/10 | 1/10 | 1/10 | **1.8/10** | ❌ Needs work |
| 05 | [Stock Trading & Matching, retry](session-05-stock-trading-retry.md) | 7/10 | 7/10 | 6/10 | 6/10 | 5/10 | **6.2/10** | ⚠️ Borderline |
| 06 | [Connection Pool](session-06-connection-pool.md) | 5/10 | 5/10 | 6/10 | 4/10 | 3/10 | **4.6/10** | ❌ Needs work |
| 07 | [Rate Limiter](session-07-rate-limiter.md) | 4/10 | 1/10 | 3/10 | 3/10 | 3/10 | **2.8/10** | ❌ Needs work |

Verdict thresholds (same as the sibling tracker): ✅ Pass ≥ 7 · ⚠️ Borderline 5.5–6.9 · ❌ Needs work < 5.5.

---

## Consolidated Tips (grouped by axis, weakest first)

- **Correctness (Walkthrough)** `[S01][S02][S03][S04][S05][S06][S07]` — Trace the primary flow with ≥2 actors,
  ≥2 uses of the same handle, every relative ordering of a range/interval check, both directions of a two-sided
  structure, the case where a demo's last action happens with nothing left waiting for it, *and* — Session 07's
  finding — every stage/branch named in the problem statement, even deferred ones, actually has a call path in
  the demo. Session 07's `FixWindowCounter` was unreachable dead code, so Stage 1 was never walked at all; the
  classic fixed-window edge-burst bug Stage 2 exists to fix was never even demonstrated. Score history: 2/10,
  4/10, 2/10, 1/10, 5/10, 3/10, 3/10.
- **Implementation & Exceptions** `[S01][S02][S03][S04][S05][S06][S07]` — Default to no setter unless something
  outside the constructor genuinely needs to mutate that field, validate every invariant where the mutation
  actually happens, and — Session 07's finding — a `synchronized` boundary matters even for a per-client map,
  not just a shared resource pool: `RateLimiterService`'s check-then-act `get`/`new`/`put` race could silently
  drop a client's whole window under concurrent first requests. Score history: 5/10, 5/10, 4/10, 1/10, 6/10,
  4/10, 3/10.
- **Class Diagram & Relationships** `[S01][S02][S03][S04][S05][S06][S07]` — Every service class must have at
  least one relationship line to the entities it operates on, each label must match the field it's drawn from,
  every drawn edge must match what the code actually has, every drawn class must actually exist in the code,
  and — Session 07's finding, a new floor for this axis — a diagram has to be drawn *at all*. Session 07 is the
  first session with zero diagram produced, worse than any diagram-with-gaps from a prior session. Score
  history: 5/10, 6/10, 5/10, 1/10, 7/10, 5/10, 1/10.
- **Pattern Choice** `[S01][S02][S03][S04][S05][S06][S07]` — Sessions 05 and 06 strung together two
  correctly-wired Strategy patterns in a row; Session 07 breaks that streak with a *new* failure mode — the
  interface shape was right (`RateLimitChecker`, implemented identically by two classes), but the consuming
  class never actually selected between them at a real decision point, hardcoding one implementation instead.
  A Strategy interface with only one caller path isn't a Strategy pattern yet. Score history: 2/10, 2/10, 1/10,
  1/10, 6/10, 6/10, 3/10.
- **Requirements & Entities** `[S01][S02][S03][S04][S05][S06][S07]` — Write the one-sentence responsibility per
  entity as you list it, keep an out-of-scope list every session, and — Session 07's finding — keep the written
  entity list in sync with the classes actually built; Session 07's list named only `Request`, while the real
  design had four more classes it never mentioned. Score history: 5/10, 5/10, 6/10, 5/10, 7/10, 5/10, 4/10.

## Recurring Action Items

- **A named-but-unused entity slips into the entity list** `[S04][S06]` — `Stock` in the stock-trading sessions
  and `Request` in the connection-pool session were both listed, given their own class, and never actually
  constructed or read anywhere in the design. Now two sessions running — check every entity in the list is
  actually referenced somewhere in the code before finishing the design.
- **An unwalked functional requirement (or unwalked *branch* of one) hides the worst bugs** `[S03][S04][S06][S07]`
  — Session 03's cancel-a-meeting flow, Session 04's entire matching requirement, Session 06's "release with
  nobody waiting" branch, and Session 07's entire Fixed Window algorithm (unreachable dead code, so Stage 2's
  classic edge-burst bug was never demonstrated) were all never exercised in a demo, and all four turned out to
  be broken or missing. Treat a requirement (or an obvious branch of one) with no line in the demo as a red
  flag before moving on.
- **Pattern Choice** `[S01][S02][S03][S04]` — RESOLVED for two sessions (05, 06), but Session 07 shows a
  *different* way this can still fail: the interface shape was correct, but nothing ever selected between its
  implementations at a real decision point. Checking "is the pattern's shape right?" isn't enough — also check
  "does the consuming class actually accept it as a swappable parameter, or does it still hardcode one
  implementation?"
- **Out-of-scope list regressed to zero** `[S06][S07]` — repeated: Session 07's `practice/README.md` also named
  zero out-of-scope items (though its whole requirements doc was a two-line stub, not a scoping-specific slip).
  Two sessions running now with no out-of-scope list at all — treat "name at least one out-of-scope item" as a
  hard checklist item for step ①, not just a habit expected to stick once learned.
- **Unchecked setters and raw mutable getters** `[S01][S02][S03]` — four of six early sessions have exposed
  either full public setters or a raw mutable collection/counter with no invariant checks; default to
  constructor-only fields and named, intention-revealing methods.
- **Verify both directions of a two-sided structure independently** `[S05]` — RESOLVED in Session 05's second
  follow-up fix, but only after a first attempt fixed the wrong side entirely. Watch for this repeating in any
  future problem with two symmetric-but-opposite sides.
- **No runnable demo** `[S04]` — RESOLVED in Sessions 05, 06, and 07. Watch closely whether this repeats in a
  future session.
- **No diagram produced at all** `[S07]` — new this session, worse than any prior diagram-with-gaps. Watch
  whether this repeats; if it does, treat "produce at least a rough as-drawn diagram" as a hard checklist item
  before writing any code, not just an expected habit.
