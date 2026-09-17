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

Verdict thresholds (same as the sibling tracker): ✅ Pass ≥ 7 · ⚠️ Borderline 5.5–6.9 · ❌ Needs work < 5.5.

---

## Consolidated Tips (grouped by axis, weakest first)

- **Pattern Choice** `[S01][S02]` — Actively ask "is there a concept in this problem that varies?" right after
  listing entities (step ②), before drawing the class diagram. Two sessions running with an obvious hook
  (Snake/Ladder jump type; spot-allocation policy) went unnoticed both times. Score history: 2/10, 2/10.
- **Correctness (Walkthrough)** `[S01][S02]` — Trace the primary flow with ≥2 actors *or* ≥2 uses of the same
  handle (ticket/token), out loud, before writing code. Improving (no crash this time), but the underlying habit —
  replay the flow with a repeated or shared identifier — is still the gap. Score history: 2/10, 4/10.
- **Requirements & Entities** `[S01][S02]` — Write the one-sentence responsibility per entity as you list it, not
  after; keep exception types out of the entity list. Score history: 5/10, 5/10.
- **Class Diagram & Relationships** `[S01][S02]` — Every service class must have at least one relationship line to
  the entities it operates on, and each label must match the field it's drawn from (a stored field is "has-a," not
  "uses," even if the class also calls methods on it). Score history: 5/10, 6/10.
- **Implementation & Exceptions** `[S01][S02]` — Default to no setter unless something outside the constructor
  genuinely needs to mutate that field; naming distinct custom exceptions (Session 02's strength) is worth keeping
  up. Score history: 5/10, 5/10.

## Recurring Action Items

- **Pattern Choice** `[S01][S02]` — the single most-repeated gap: 2/10 on both sessions logged so far, each time
  with a real Strategy-shaped hook sitting in the requirements unnoticed. Make "what's the one thing here that
  could vary?" a mandatory question immediately after step ②, every single session, until this stops repeating.
- **Unchecked setters on every entity** `[S01][S02]` — both sessions exposed full public setters with no invariant
  checks. Default to constructor-only fields; add a setter only when a specific, named caller after construction
  needs one.
