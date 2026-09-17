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

Verdict thresholds (same as the sibling tracker): ✅ Pass ≥ 7 · ⚠️ Borderline 5.5–6.9 · ❌ Needs work < 5.5.

---

## Consolidated Tips (grouped by axis, weakest first)

- **Pattern Choice** `[S01][S02][S03]` — Actively ask "is there a concept in this problem that varies?" right
  after listing entities (step ②), before drawing the class diagram. Three sessions running with an obvious hook
  each time (Snake/Ladder jump type; spot-allocation policy; room-allocation policy) went unnoticed every time —
  and in Session 03 the missing Strategy also meant the API didn't satisfy its own stated requirement.
  Score history: 2/10, 2/10, 1/10.
- **Correctness (Walkthrough)** `[S01][S02][S03]` — Trace the primary flow with ≥2 actors, ≥2 uses of the same
  handle, *or* every relative ordering of a range/interval check, out loud, before writing code. Session 03's
  double-booking bug and Session 01's crash are the same root cause: an ordering nobody traced by hand.
  Score history: 2/10, 4/10, 2/10.
- **Requirements & Entities** `[S01][S02][S03]` — Write the one-sentence responsibility per entity as you list
  it, not after; keep exception types out of the entity list; keep the entity list in sync with the actual code
  (Session 03 drifted in *both* directions — an undone entity and mismatched class names).
  Score history: 5/10, 5/10, 6/10.
- **Class Diagram & Relationships** `[S01][S02][S03]` — Every service class must have at least one relationship
  line to the entities it operates on, each label must match the field it's drawn from, and — Session 03's new
  finding — every drawn edge must correspond to a field that actually exists in the code, not an aspirational one.
  Score history: 5/10, 6/10, 5/10.
- **Implementation & Exceptions** `[S01][S02][S03]` — Default to no setter unless something outside the
  constructor genuinely needs to mutate that field — Session 03 went further and exposed a raw mutable counter
  object via a getter, which is the same mistake one hop removed. Naming distinct custom exceptions is a
  consistently improving strength (3, 3, then 4 exception types across sessions) — keep it up.
  Score history: 5/10, 5/10, 4/10.

## Recurring Action Items

- **Pattern Choice** `[S01][S02][S03]` — the single most-repeated gap, now three sessions running, each with a
  real Strategy-shaped hook sitting in the requirements unnoticed (dispatch/allocation policy every time). Make
  "what's the one thing here that could vary?" a mandatory question immediately after step ②, every single
  session, until this stops repeating.
- **Unchecked setters and raw mutable getters** `[S01][S02][S03]` — every session so far has exposed either full
  public setters or a raw mutable collection/counter with no invariant checks. Default to constructor-only fields
  and named, intention-revealing methods; never return a live internal collection or a mutable helper object
  (`AtomicInteger`, `List`) from a getter.
- **An unwalked functional requirement hides the worst bugs** `[S03]` — the cancel-a-meeting requirement was
  never exercised in `Main.java` and turned out to be completely broken. Watch for this becoming a repeating
  pattern: if a stated requirement has no line in the demo, treat that as a red flag before moving on, not an
  oversight to catch later.
