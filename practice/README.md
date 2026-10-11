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
| 08 | [Subscription / Billing Service](session-08-subscription-billing.md) | 3/10 | 1/10 | 1/10 | 2/10 | 2/10 | **1.8/10** | ❌ Needs work |
| 09 | [Voting Algorithm](session-09-voting-algorithm.md) | 3/10 | 1/10 | 4/10 | 1/10 | 2/10 | **2.2/10** | ❌ Needs work |
| 10 | [Design Snake Game](session-10-snake-game.md) | 4/10 | 1/10 | 1/10 | 2/10 | 2/10 | **2.0/10** | ❌ Needs work |
| 11 | [LRU Cache](session-11-lru-cache.md) | 3/10 | 1/10 | 1/10 | 3/10 | 4/10 | **2.4/10** | ❌ Needs work |

Verdict thresholds (same as the sibling tracker): ✅ Pass ≥ 7 · ⚠️ Borderline 5.5–6.9 · ❌ Needs work < 5.5.

---

## Consolidated Tips (grouped by axis, weakest first)

- **Correctness (Walkthrough)** `[S01][S02][S03][S04][S05][S06][S07][S08][S09][S10][S11]` — Trace the primary
  flow with ≥2 actors, ≥2 uses of the same handle, every relative ordering of a range/interval check, both
  directions of a two-sided structure, the case where a demo's last action happens with nothing left waiting
  for it, every stage/branch named in the problem statement, when the problem statement itself scopes the API
  check the method signature actually matches that sentence, a size constraint phrased as "up to N" means
  smaller inputs are valid, a state machine's "losing" branch needs a follow-up call tried against it too, not
  just the branch itself, *and* — Session 11's finding — "insert or update" in a problem statement names two
  behaviors in one sentence; Session 11's demo only ever exercised insert, which hid a real bug (the value
  never updating) in the update path. Score history: 2/10, 4/10, 2/10, 1/10, 5/10, 3/10, 3/10, 2/10, 2/10,
  2/10, 4/10.
- **Implementation & Exceptions** `[S01][S02][S03][S04][S05][S06][S07][S08][S09][S10][S11]` — Default to no
  setter unless something outside the constructor genuinely needs to mutate that field, validate every
  invariant where the mutation actually happens, a config value that varies by context shouldn't be stored as
  a field on an unrelated entity, a `static` field assigned inside a non-static constructor turns per-instance
  configuration into shared global state, a boolean/collision check that depends on what else happens *this
  same step* has to be computed in dependency order, not have the dependent fact baked in as already decided,
  *and* — Session 11's finding — a mutator that can either insert or update needs a setter for the field it
  updates; `Node` had no `setValue` at all, so the "update" branch of `add` could move an entry's position but
  structurally could not touch its payload. Score history: 5/10, 5/10, 4/10, 1/10, 6/10, 4/10, 3/10, 2/10,
  1/10, 2/10, 3/10.
- **Class Diagram & Relationships** `[S01][S02][S03][S04][S05][S06][S07][S08][S09][S10][S11]` — Every service
  class must have at least one relationship line to the entities it operates on, each label must match the
  field it's drawn from, every drawn edge must match what the code actually has, every drawn class must
  actually exist in the code, and a diagram has to be drawn *at all* — now five sessions running with zero
  diagram produced. Score history: 5/10, 6/10, 5/10, 1/10, 7/10, 5/10, 1/10, 1/10, 1/10, 1/10, 1/10.
- **Pattern Choice** `[S01][S02][S03][S04][S05][S06][S07][S08][S09][S10][S11]` — Sessions 05 and 06 strung
  together two correctly-wired Strategy patterns in a row; Session 07 broke that streak with a
  shaped-but-unwired interface; Session 08 had no pattern at all; Session 09 got furthest (shaped and wired,
  defeated by a `static` field); Session 10 was back to no pattern at all despite the problem's own source
  naming a second growth rule; Session 11 is a special case worth naming separately — "no pattern" is actually
  the *correct* answer here (eviction order is structural, not pluggable, per the ideal design's own
  reasoning), but the practice session never reached that conclusion deliberately, it just never raised the
  question. Score history: 2/10, 2/10, 1/10, 1/10, 6/10, 6/10, 3/10, 1/10, 4/10, 1/10, 1/10.
- **Requirements & Entities** `[S01][S02][S03][S04][S05][S06][S07][S08][S09][S10][S11]` — Write the
  one-sentence responsibility per entity as you list it, keep an out-of-scope list every session, keep the
  written entity list in sync with the classes actually built, delete an entity the moment it's noticed to be
  unused, a domain concept that's genuinely load-bearing still needs its own entity even when a primitive can
  technically hold the same data, a field shared by every variant of a problem shouldn't hold a concept only
  one variant actually has, *and* — Session 11's finding — a correct entity list is not the same as a
  *documented* one; `Node`/`Lru` were the right two entities but neither had a written responsibility sentence
  anywhere. Score history: 5/10, 5/10, 6/10, 5/10, 7/10, 5/10, 4/10, 3/10, 3/10, 4/10, 3/10.

## Recurring Action Items

- **A named-but-unused entity slips into the entity list** `[S04][S06][S08]` — `Stock` in the stock-trading
  sessions, `Request` in the connection-pool session, and *both* `User` and `Bill` in Session 08 were listed,
  given their own class, and never actually constructed or read anywhere in the design. Three sessions running
  now, and Session 08 doubled up — check every entity in the list is actually referenced somewhere in the code
  before finishing the design.
- **An unwalked functional requirement (or unwalked *branch* of one) hides the worst bugs** `[S03][S04][S06][S07][S08][S09][S10][S11]`
  — Session 03's cancel-a-meeting flow, Session 04's entire matching requirement, Session 06's "release with
  nobody waiting" branch, Session 07's entire Fixed Window algorithm, Session 08's entire *year* dimension,
  Session 09's short-ballot case, Session 10's tail-reversal case *and* its post-game-over continuation check,
  and Session 11's re-`add`-on-an-existing-key path were all never exercised in a demo, and every one of them
  turned out to be broken or missing. Treat a requirement (or an obvious branch of one) with no line in the
  demo as a red flag before moving on.
- **Pattern Choice** `[S01][S02][S03][S04]` — RESOLVED for two sessions (05, 06), but Session 07 showed one way
  this can still fail (shaped-but-unwired), Session 09 showed another (shaped and wired, defeated by a
  `static` field), Session 10 regressed all the way back to no pattern at all for the thing that varies, and
  Session 11 showed a third failure mode: "no pattern" was actually the right call, but reached by never
  asking the question rather than by deliberate justification. This axis's score history (6/10, 6/10, 3/10,
  1/10, 4/10, 1/10, 1/10 across Sessions 05-11) shows no stable trend yet even after two genuinely strong
  sessions.
- **Out-of-scope list regressed to zero** `[S06][S07][S08][S09][S10][S11]` — repeated a sixth time. Six
  sessions running now with no out-of-scope list — treat "name at least one out-of-scope item" as a hard
  checklist item for step ①, not just a habit expected to stick once learned.
- **Unchecked setters and raw mutable getters** `[S01][S02][S03]` — four of six early sessions have exposed
  either full public setters or a raw mutable collection/counter with no invariant checks; default to
  constructor-only fields and named, intention-revealing methods.
- **Verify both directions of a two-sided structure independently** `[S05]` — RESOLVED in Session 05's second
  follow-up fix, but only after a first attempt fixed the wrong side entirely. Watch for this repeating in any
  future problem with two symmetric-but-opposite sides.
- **No runnable demo** `[S04]` — RESOLVED in Sessions 05, 06, and 07. Watch closely whether this repeats in a
  future session.
- **No diagram produced at all** `[S07][S08][S09][S10][S11]` — now five sessions running. Treat "produce at
  least a rough as-drawn diagram" as a hard checklist item before writing any code, not an expected habit that
  was ever actually confirmed.
- **A `static` field assigned inside a non-static constructor** `[S09]` — new this session: per-instance
  configuration (which `TieBreakerStrategy` a `ResultService` was built with) was actually global state in
  disguise. Watch whether this repeats in any future session that injects a collaborator through a
  constructor — it's a one-line, easy-to-miss root cause that defeats otherwise-correct Strategy wiring.
