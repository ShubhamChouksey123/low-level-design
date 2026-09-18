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

Verdict thresholds (same as the sibling tracker): ✅ Pass ≥ 7 · ⚠️ Borderline 5.5–6.9 · ❌ Needs work < 5.5.

---

## Consolidated Tips (grouped by axis, weakest first)

- **Correctness (Walkthrough)** `[S01][S02][S03][S04][S05][S06]` — Trace the primary flow with ≥2 actors, ≥2
  uses of the same handle, every relative ordering of a range/interval check, both directions of a two-sided
  structure, *and* — Session 06's finding — the case where a demo's last action (release/close/cancel) happens
  with nothing left waiting for it. A demo that only ever exercises the "someone's waiting" branch will hide a
  crash on the far more common "nobody's waiting" branch. Score history: 2/10, 4/10, 2/10, 1/10, 5/10, 3/10.
- **Implementation & Exceptions** `[S01][S02][S03][S04][S05][S06]` — Default to no setter unless something
  outside the constructor genuinely needs to mutate that field, and validate every invariant where the mutation
  actually happens. Session 06's `ConnectionPool` is the first resource-pool entity with zero raw getters — real
  progress — but a *different* class's setter (`Connection.setId`) still risks desyncing an identity from a map
  key, and pool-size validation regressed to none at all. Score history: 5/10, 5/10, 4/10, 1/10, 6/10, 4/10.
- **Class Diagram & Relationships** `[S01][S02][S03][S04][S05][S06]` — Every service class must have at least one
  relationship line to the entities it operates on, each label must match the field it's drawn from, every drawn
  edge must match what the code actually has, and — Session 06's finding — every drawn *class* must actually
  exist in the code. A floating, unconnected box for a class that was never built is the same gap as Session 01's
  disconnected services, just with an invented class this time. Score history: 5/10, 6/10, 5/10, 1/10, 7/10, 5/10.
- **Pattern Choice** `[S01][S02][S03][S04][S05][S06]` — Session 06 kept the streak Session 05 started: a second
  correctly-wired Strategy pattern in a row. The remaining gap is still the framework's own golden rule — say
  *why*, in writing — which no session has done yet even twice in a row with the pattern itself right.
  Score history: 2/10, 2/10, 1/10, 1/10, 6/10, 6/10.
- **Requirements & Entities** `[S01][S02][S03][S04][S05][S06]` — Write the one-sentence responsibility per entity
  as you list it, not after, and keep an out-of-scope list on every single session — Session 06 is the first
  session since Session 01 to name zero out-of-scope items, a real regression after four sessions of steady
  improvement on exactly this. Score history: 5/10, 5/10, 6/10, 5/10, 7/10, 5/10.

## Recurring Action Items

- **A named-but-unused entity slips into the entity list** `[S04][S06]` — `Stock` in the stock-trading sessions
  and `Request` in the connection-pool session were both listed, given their own class, and never actually
  constructed or read anywhere in the design. Now two sessions running — check every entity in the list is
  actually referenced somewhere in the code before finishing the design.
- **An unwalked functional requirement (or unwalked *branch* of one) hides the worst bugs** `[S03][S04][S06]` —
  Session 03's cancel-a-meeting flow, Session 04's entire matching requirement, and Session 06's "release with
  nobody waiting" branch were all never exercised in a demo, and all three turned out to be broken or missing.
  Treat a requirement (or an obvious branch of one) with no line in the demo as a red flag before moving on.
- **Pattern Choice** `[S01][S02][S03][S04]` — RESOLVED, and now confirmed twice running (Sessions 05 and 06) with
  correctly-wired Strategy patterns. Still open: writing down *why* the pattern was chosen, which neither session
  has done yet even with the pattern itself right.
- **Out-of-scope list regressed to zero** `[S06]` — new this session, after Sessions 02–05 each named at least
  one out-of-scope item. Watch whether this repeats; if it does, treat "name at least one out-of-scope item" as
  a hard checklist item for step ①, not just a habit that's expected to stick once learned.
- **Unchecked setters and raw mutable getters** `[S01][S02][S03]` — four of six sessions have exposed either full
  public setters or a raw mutable collection/counter with no invariant checks; Session 06 is real, partial
  progress (`ConnectionPool` itself is clean) but `Connection.setId` still repeats the identity-mutation shape of
  this same gap. Default to constructor-only fields and named, intention-revealing methods.
- **Verify both directions of a two-sided structure independently** `[S05]` — RESOLVED in Session 05's second
  follow-up fix, but only after a first attempt fixed the wrong side entirely. Watch for this repeating in any
  future problem with two symmetric-but-opposite sides.
- **No runnable demo** `[S04]` — RESOLVED in Session 05 and confirmed again in Session 06. Watch closely whether
  this repeats in a future session.
