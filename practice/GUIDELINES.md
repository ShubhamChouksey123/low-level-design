# Authoring Guidelines — `practice/`

Rules for every `session-NN-<problem>.md` file created inside `practice/`. Each file logs one mock LLD/OOD
interview and turns it into a reusable study asset. Mirrors the sibling `system-design` repo's
[`practice/GUIDELINES.md`](https://github.com/ShubhamChouksey123/system-design/blob/master/practice/GUIDELINES.md),
adapted for class-design rounds instead of architecture rounds.

---

## 1. What a session file is

Each mock is **one file**: `practice/session-NN-<problem-slug>.md` (zero-padded `NN`, ordered by when the mock
happened; `<problem-slug>` is the kebab-case problem name, e.g. `session-02-parking-lot.md`). No separate raw
transcript file — the mock's real code/diagram already lives under
`src/main/java/com/shubham/app/<designname>/practice/`, and the session file links straight to it as evidence.

The session file is two things at once:

1. An **honest post-mortem** — what actually happened, scored, with the gaps named plainly. A low score is the
   *reason the page is worth reading*, not something to soften.
2. A **reference answer** — the "ideal design" section, showing how the problem *should* be solved, following
   [`concepts/answer-framework.md`](../concepts/answer-framework.md)'s 8 steps. This is what makes the page useful
   to a reader who never saw the mock.

Write both. The post-mortem builds trust; the ideal design makes it teachable.

## 2. Required structure (in this order)

Every `session-NN-<problem>.md` MUST have these headings, in order:

```
# Session NN — <Problem> (LLD/OOD) · <overall>/10
> intro blockquote — one honest paragraph: what this session shows + weakest areas (+ delta vs the previous
  session once there is one)
<snapshot line>              Problem · Focus · Overall · Weakest areas · Artifacts (links into the practice code)
## The problem                verbatim prompt (blockquote) + "what it really tests" (one sentence naming the
                               concept the problem is actually checking for)
## Requirements & entities gathered   what was produced + gaps against step ① and ② of the answer framework
## The design produced        embed the as-drawn class diagram; bullet the relationships actually drawn
## Scorecard                  5-axis table + Overall + a one-line note per axis
## What lost points — and the fix   3-col table: what I missed | the senior answer | Study link
## What went well              the instincts that landed (keep morale + reinforce habits)
---
## The ideal design            the reference answer (§3) — the heart of the file
## Takeaways to drill          numbered, specific, drill-able lessons
> pointer line to concepts/answer-framework.md + a concrete next practice move
## How to Improve              the single highest-leverage habit to build next
```

The five scored axes are fixed: **Requirements & Entities · Class Diagram & Relationships · Pattern Choice &
Justification · Implementation & Exception Handling · Correctness (Primary-Flow Walkthrough)**, plus **Overall**.
Verdict thresholds (same as the sibling tracker): **✅ Pass ≥ 7 · ⚠️ Borderline 5.5–6.9 · ❌ Needs work < 5.5**.

## 3. The **Ideal Design** section — mandatory contents, in order

This is the reference answer. It MUST be self-contained (a reader learns the solution here without ever reading
the post-mortem above it) and MUST follow
[`concepts/answer-framework.md`](../concepts/answer-framework.md)'s 8 steps exactly, using the same ①–⑧ heading
numbers so cross-links (`answer-framework.md#-<slug>`) keep working:

| Sub-heading | What it holds |
|---|---|
| **Framing sentence** (before ①) | One line naming the *crux* of the problem — the single abstraction or invariant everything else follows from. Then one sentence pointing at where the real implementation lives (`src/main/java/com/shubham/app/<designname>/ideal/`) and its own `README.md`. |
| **① Functional Requirements & Scope** | Actors, operations as verbs, and an explicit out-of-scope list — even items the mock also scoped out are worth restating here. |
| **② Core Entities & Responsibilities** | A table: `Entity \| Responsibility`, one sentence each, no "and". Flag whichever entity is the varying concept behind an interface — that's the seed for ④. |
| **③ Relationships & Class Diagram** | Bulleted by relationship type — **implements the contract of / has-a / uses / is-a** — each with the one-sentence reason, per [`concepts/uml-diagrams.md`](../concepts/uml-diagrams.md#1-class-diagram). Then the diagram: source link + embedded PNG (§4). |
| **④ Pattern Choice & Justification** | Name the exact pattern (e.g. "Strategy Pattern"), one sentence on what it buys ("a new X is one new class, not an edit"), then bullet each concrete use if the pattern is used more than once. |
| **⑤ Core Classes/Interfaces** | A Java code block, **signatures only** — no bodies. Precede it with links to the real implementation package and to the test package that proves each class's behavior. |
| **⑥ Exception Handling** | Which custom exception is thrown for which failure case, and the one edge case handled *without* an exception (a no-op, not an error). |
| **⑦ Primary-Flow Walkthrough — the actual fix for this session's bug** | Diagram (source link + PNG) tracing the flow that exposed the mock's real bug, then 2–4 sentences naming the fix and linking the regression test that proves it. |
| **⑧ Concurrency & Extensibility** | **Concurrency:** what's synchronized and why, plus the test that proves no lost/duplicated state under concurrent access (or an explicit "out of scope, single-threaded" statement). **Extensibility:** one new requirement → the one new class it costs. |

## 4. Diagrams — Mermaid, in `practice/diagrams/`

- Every diagram referenced from a session file lives in `practice/diagrams/`, named
  `session<NN>-ideal-class-diagram.mmd`/`.png` and `session<NN>-ideal-sequence-diagram.mmd`/`.png`.
- Author the `.mmd`, then render: `npx -y @mermaid-js/mermaid-cli@11 -i name.mmd -o name.png -b white -s 5`.
  Bump to `-s 6` for a wide/dense diagram (many classes, overlapping edges); `-s 5` is the default baseline.
- **Read the rendered PNG back before embedding it** to check it actually rendered (a Mermaid parse error fails
  silently into a truncated or blank image otherwise).
- Class diagram edges MUST be labeled with one of the four relationship types (**implements the contract of /
  has-a / uses / is-a**) — bare arrows are a diagram gap, not a style choice (this was the concrete finding that
  first justified writing this rule down, in Session 01's review).
- Alt text must be a plain, full descriptive sentence — no parentheses or brackets (they break Markdown image
  rendering).
- The mock's **as-drawn** diagram (screenshot/export from the mock itself) stays wherever the practice code lives
  (`src/main/java/com/shubham/app/<designname>/practice/diagram*/`) and is embedded by reference from "## The
  design produced" — it is not moved or re-rendered, since it's evidence of what actually happened in the room.

## 5. Honesty & scoring conventions

- **Score exactly what the review found** — never round up. Once a second session exists for the same axis,
  show the trend (score history) rather than just the latest number.
- **Name each gap once, concretely, with a file:line reference into the practice code**, and attach the fix + a
  `Study` cross-link into `concepts/` or `concepts/answer-framework.md`. A gap with no linked concept is an
  incomplete entry.
- **Tag recurring misses** with `[S01][S02]`-style session tags in `README.md`'s Consolidated Tips /
  Recurring Action Items (§6) — if a gap repeats, say so explicitly ("2/10 on both sessions logged so far").
- Give credit precisely too — "## What went well" and the Scorecard notes should call out *specific* things that
  improved over the previous session, not just what's still missing.

## 6. Update `practice/README.md` (every new session)

A session isn't logged until the index is updated — `practice/README.md` is a **short index only** (intro +
Sessions table + the two cross-session aggregate sections). It does not hold session content itself:

- Add a **row** to the Sessions table — Problem column links to the new `session-NN-<problem>.md` file, all five
  axis scores + Overall + verdict emoji.
- **Promote any repeated feedback** into `## Consolidated Tips` (grouped by axis, weakest first, with score
  histories and `[SNN]` tags) and `## Recurring Action Items` (only gaps that have now repeated across sessions).
  **That aggregation is the whole point of the tracker** — a finding that isn't rolled up here is lost the moment
  the next session starts.

## 7. Blank template — copy this to start a new session file

```markdown
# Session NN — <Problem> (LLD/OOD) · <overall>/10

> One honest paragraph: what worked, what's weakest, and the delta vs the previous session if there is one.

**Problem:** <one line> · **Focus:** class design, no architecture/scale concerns · **Overall:** <n>/10 ·
**Weakest areas:** <axis>, <axis> · **Artifacts:**
[`Main.java`](../src/main/java/com/shubham/app/<designname>/practice/Main.java),
[entity/](../src/main/java/com/shubham/app/<designname>/practice/entity/),
[service/](../src/main/java/com/shubham/app/<designname>/practice/service/),
[class-diagram.png](../src/main/java/com/shubham/app/<designname>/practice/diagram/class-diagram.png)

## The problem

> - <verbatim functional requirement>
> - <verbatim functional requirement>

What it really tests: <one sentence naming the concept this problem actually checks for>.

## Requirements & entities gathered

What was produced: <n> functional requirements, <out-of-scope items named or "none">, and an entity list
(`Entity1`, `Entity2`, ...).

Gaps against [step ① and ②](../concepts/answer-framework.md#-requirements--scope) of the framework: <specific gaps>.

## The design produced

![Plain descriptive alt text for the as-drawn class diagram](../src/main/java/com/shubham/app/<designname>/practice/diagram/class-diagram.png)

- `Entity` — <relationship> `OtherEntity`.

<1-3 sentences on relationship/labeling gaps in the as-drawn diagram.>

## Scorecard

| Axis | Score /10 | Note |
|---|---|---|
| Requirements & Entities | n/10 | |
| Class Diagram & Relationships | n/10 | |
| Pattern Choice & Justification | n/10 | |
| Implementation & Exception Handling | n/10 | |
| Correctness (Primary-Flow Walkthrough) | n/10 | |
| **Overall** | **n/10** | |

## What lost points — and the fix

| What I missed | The senior answer | Study link |
|---|---|---|
| <file:line reference + what's wrong> | <the fix> | [`concepts/...`](../concepts/...) |

## What went well

- <specific, concrete credit>

---

## The ideal design

> **Implemented** in [`src/main/java/com/shubham/app/<designname>/ideal/`](../src/main/java/com/shubham/app/<designname>/ideal)
> — see that package's own [`README.md`](../src/main/java/com/shubham/app/<designname>/ideal/README.md) for what
> changed vs. this doc and why.

**Framing sentence:** <the one abstraction/invariant everything else follows from>.

The rest of this section follows [`concepts/answer-framework.md`](../concepts/answer-framework.md)'s 8 steps in order.

### ① Functional Requirements & Scope

**Actors:** ...

**Operations:** ...

**Explicitly out of scope:** ...

### ② Core Entities & Responsibilities

| Entity | Responsibility |
|---|---|

### ③ Relationships & Class Diagram

- **Implements the contract of** — ...
- **Has-a** — ...
- **Uses** — ...
- **Is-a** — ... (or "deliberately absent", with why)

**Ideal class diagram:** source at
[`diagrams/session<NN>-ideal-class-diagram.mmd`](diagrams/session<NN>-ideal-class-diagram.mmd).

![Plain descriptive alt text for the ideal class diagram](diagrams/session<NN>-ideal-class-diagram.png)

### ④ Pattern Choice & Justification

**Pattern used: <Pattern Name>.**

<one sentence on what it buys.>

- **<use case>:** ...

### ⑤ Core Classes/Interfaces

Signatures only — full implementations are in
[`src/main/java/com/shubham/app/<designname>/ideal/`](../src/main/java/com/shubham/app/<designname>/ideal), and
tests proving each class's behavior are in
[`src/test/java/com/shubham/app/<designname>/ideal/`](../src/test/java/com/shubham/app/<designname>/ideal):

```java
// signatures only
```

### ⑥ Exception Handling

- `<Exception>` — thrown for ...
- Edge case handled **without** an exception: ...

### ⑦ Primary-Flow Walkthrough — the actual fix for this session's bug

Source at
[`diagrams/session<NN>-ideal-sequence-diagram.mmd`](diagrams/session<NN>-ideal-sequence-diagram.mmd).

![Plain descriptive alt text for the ideal sequence diagram](diagrams/session<NN>-ideal-sequence-diagram.png)

<what the fix is, and a link to the regression test that proves it.>

### ⑧ Concurrency & Extensibility

**Concurrency:** ...

**Extensibility:** ...

## Takeaways to drill

1. ...

> Rehearse this against [`concepts/answer-framework.md`](../concepts/answer-framework.md) before the next mock.

## How to Improve

<the single highest-leverage habit to build next.>
```

## 8. Before you save — checklist

```
□ File is practice/session-NN-<problem-slug>.md (zero-padded NN, kebab-case problem slug)
□ All required headings present, in order (§2)
□ Snapshot line + Scorecard use the five fixed axes + Overall; verdict matches the threshold
□ Every lost-point row has a file:line reference + a concrete fix + a Study cross-link into concepts/
□ Ideal Design section is self-contained and follows all 8 numbered steps (§3), matching
  concepts/answer-framework.md's heading text and anchors exactly
□ Ideal class + sequence diagrams are Mermaid (.mmd), rendered to same-named .png at -s 5 (or 6 if dense),
  in practice/diagrams/, with every edge labeled has-a/uses/is-a/implements-the-contract-of (§4)
□ Diagram alt text is plain (no parentheses/brackets)
□ Ideal design implementation actually exists under src/main/java/.../ideal/, with its own README.md and tests
  under src/test/java/.../ideal/ that the session file links to
□ practice/README.md updated: Sessions table row + Consolidated Tips + Recurring Action Items (§6)
□ Score history / [SNN] tags added wherever a gap repeats across sessions
□ All relative cross-links resolve
```
