# Low-Level Design Interview — Answer Framework (Playbook)

> A simple step-by-step checklist for any LLD/OOD interview question. Follow it top to bottom so you never miss a
> step. Mirrors the sibling `system-design` repo's
> [`practice/answer-framework.md`](https://github.com/ShubhamChouksey123/system-design/blob/master/practice/answer-framework.md)
> — that one is for architecture rounds; this one is for class-design rounds. Same 8 steps tracked as a checklist
> in [`TODO.md`](../TODO.md).

## Real interview timing: 40 minutes for the design

A typical 60-minute LLD interview also has intro + questions at the start and end. That usually leaves about
**40 minutes** for the actual problem. Budget it like this:

| Phase | Time | Steps |
|---|---|---|
| 1. Clarify & identify | ~5 min | ① Requirements & Scope · ② Entities & Responsibilities |
| 2. Model | ~8 min | ③ Relationships & Class Diagram · ④ Pattern Choice |
| 3. Code | ~20 min | ⑤ Core Classes/Interfaces · ⑥ Exception Handling |
| 4. Verify & extend | ~7 min | ⑦ Primary-Flow Walkthrough · ⑧ Concurrency & Extensibility |

> **Golden rule:** always say what's *out of scope* before you design anything. And for every pattern you use,
> say **why** — "I used Strategy because X will change" is much better than "I used Strategy because it's good
> practice."

---

## ① Requirements & Scope

**Goal:** know *what* the system does before you design *how*.

**Do this:**
- List the actors (usually just 1–2 for an LLD problem).
- List the core operations as simple verbs — "request an elevator," not "manage the elevator system."
- Say what's **out of scope**, out loud. This matters as much as saying what's in scope.
- Ask if there's a hidden requirement — multiple users at once? A strict order to follow? Something that needs
  to be easy to extend later?

**Watch out for:** too many actors. If you have 3+, you're probably scoping too wide.

---

## ② Entities & Responsibilities

**Goal:** turn the requirements into a first list of classes — no relationships yet, no code yet.

**Do this:**
- Nouns in the problem → candidate classes.
- Verbs attached to each noun → candidate methods.
- For each class, say its job in **one sentence, with no "and"**. If you need "and," split it into two classes.
- Ask: is there anything here that *varies* — a policy, a status, a mode? Flag it now. You'll need it in step ④.

**Watch out for:** a class whose job needs "and" to describe. That's two responsibilities in one class.

---

## ③ Relationships & Class Diagram

**Goal:** connect the classes the right way, and draw it.

**Do this:**
- Draw the boxes (just names) first. Connect them. Fill in fields/methods last.
- For every connection, say which kind it is:
  - **is-a** — inheritance. Use only when one class can be swapped in wherever the other is used.
  - **has-a** — one class owns another (a field it holds).
  - **uses-a** — one class calls another but doesn't own it.
- Add numbers on the lines (`1`, `0..1`, `*`) — e.g. "one `Room` can have many `Meeting`s."
- Check: does every operation from step ① have a clear home in this diagram?

**Watch out for:** reaching for inheritance by default. Composition is the safer default — see
[`concepts/basic-oop-concepts.md`](basic-oop-concepts.md) §2.

---

## ④ Pattern Choice & Justification

**Goal:** find the part that varies (from step ②) and hide it behind an interface.

**Quick picks:**
- Behavior changes by **mode over time** (idle vs. moving)? → **State**
- Caller picks a **policy** (pricing rule, dispatch rule)? → **Strategy**
- One event needs to **notify many listeners**? → **Observer**
- Object creation **varies by type**? → **Factory**
- Building something with **many optional parts**? → **Builder**

**Do this:**
- For each pattern you pick, say the "why" in one sentence: *"If X changes later, I add one class — I don't edit
  this one."*
- Don't add a pattern nothing asked for. One implementation and no variation = just use a plain class.

---

## ⑤ Core Classes/Interfaces

**Goal:** write the actual signatures. This is where your design becomes checkable.

**Do this:**
- Write method signatures for **every** class first, before filling in any single one.
- Only implement what step ⑦'s walkthrough will actually use. No "just in case" helpers.
- Skip getters/setters out loud ("I'd generate these") instead of typing them — save the time.
- Don't hand out raw mutable fields/collections. If another class needs to change something, give it a named
  method instead (e.g. `scheduleStop(floor)`, not the raw list).

---

## ⑥ Exception Handling

**Goal:** name your failure cases out loud. This alone makes you sound senior.

**Say these four things:**
- **Errors** — things that can't be recovered from (e.g. "no elevators configured").
- **Edge cases** — empty input, boundaries, things unique to this problem (e.g. asking to move to where you
  already are — should be a no-op, not an error).
- **Exceptions** — which exception types your code actually throws, checked or unchecked, and why.
- **Invalid input** — what's rejected at the door, and how the caller finds out.

**Do this:** implement at least one real custom exception, not just a generic one.

---

## ⑦ Primary-Flow Walkthrough

**Goal:** trace one real flow through your classes. This is how you catch your own bugs before the interviewer
does.

**Do this:**
- Pick the flow that touches the *most* classes.
- Walk it step by step: who calls what, on whom, in order.
- Check: does every step land on a method that actually exists from step ⑤?
- If there's a second flow worth mentioning, just describe it in words — don't draw two full diagrams.

**Watch out for:** skipping this step. A 30-second trace with **2+ actors** is often the only thing that catches a
real bug — see `practice/README.md`'s Session 01 for a concrete example.

---

## ⑧ Concurrency & Extensibility

**Goal:** answer the two questions most interviewers ask if you don't bring them up first.

**Concurrency — say:**
- Can two people/threads call this at the same time?
- If yes: what shared state could break, and how do you protect it?
- If no: say that out loud too — don't just leave it unsaid.

**Extensibility — say:**
- Pick one plausible new requirement.
- Name the *one class* you'd add for it.
- Confirm it's a new class, not an edit to an old one — that's the payoff of step ④.

---

## Blank template (copy for each practice attempt)

```
Problem: ________________________________________________

① Requirements & Scope
- actors / operations:
- out of scope:

② Entities & Responsibilities
- entity → one-sentence job (no "and"):

③ Relationships & Class Diagram
- is-a / has-a / uses-a per pair, with numbers (1, *, etc.):

④ Pattern Choice & Justification
- what varies → which pattern → why:

⑤ Core Classes/Interfaces
- signatures for every class, then fill in bodies:

⑥ Exception Handling
- errors · edge cases · exceptions · invalid input:

⑦ Primary-Flow Walkthrough
- the one flow, step by step:

⑧ Concurrency & Extensibility
- shared state + how it's protected (or why not needed):
- one new requirement → the one class you'd add:
```

## Quick recap

- Say what's in scope and out of scope before naming a single class.
- List entities before connecting them. Connect them before picking patterns.
- Pick patterns before writing signatures. Write signatures before bodies.
- Name your failure cases before the interviewer asks about them.
- Walk the main flow yourself — that's how you catch your own gaps.
- End on concurrency and extensibility, even briefly — it's what makes the answer feel senior.

`src/main/java/com/shubham/app/elevatorsystem/README.md` runs through all 8 steps on a real problem — read it
alongside this doc a few times until the sequence feels automatic.
