# Voting Algorithm — Ideal Design

Reviewed against [`practice/`](../practice) (see that package's `README.md` for the problem
statement and the review discussion), then written directly against
[`concepts/answer-framework.md`](../../../../../../../concepts/answer-framework.md)'s 8 steps.

## ① Functional Requirements & Scope

**Actor:** Any caller tallying a completed list of ballots.

**Operations:**
- `getResults(ballots)` — returns every named candidate, sorted by total points descending. A
  voter ranks 1–3 candidates per ballot; 1st place is worth 3 points, 2nd worth 2, 3rd worth 1.
- Tie-break between candidates with equal total points, via a swappable `TieBreakStrategy`:
  **Strategy 1** (`EarliestToReachTieBreakStrategy`) — whoever reached the tied total first;
  **Strategy 2** (`RankPositionTieBreakStrategy`) — compare 1st-place vote counts, then 2nd, then
  3rd.

**Explicitly out of scope:**
- Incremental/streaming results — the source is explicit that the winner is declared "only after
  getting all the votes," not computed progressively as ballots arrive.
- Concurrency — the problem never mentions concurrent access.

## ② Core Entities & Responsibilities

| Entity | Responsibility |
|---|---|
| `Ballot` | One voter's 1–3 ranked candidates; validates itself (no empty/oversized ballot, no duplicate candidate within it). |
| `CandidateTally` | One candidate's accumulated points, per-position vote counts, and the index of the last ballot that contributed to their score. |
| `TieBreakStrategy` (interface) | The Strategy: decides ordering between two candidates *only once their total points already tie*. |
| `RankPositionTieBreakStrategy` / `EarliestToReachTieBreakStrategy` | The two concrete tie-break rules. |
| `VotingService` | Processes ballots into `CandidateTally` objects and produces the sorted result. |
| `InvalidBallotException` / `InvalidVotingConfigException` | Thrown for malformed ballot input and for a null `TieBreakStrategy`, respectively. |

## ③ Relationships & Class Diagram

- **Uses** — `VotingService` uses a `TieBreakStrategy`, and uses `Ballot`/`CandidateTally` to do
  its work.
- **Implements the contract of** — `RankPositionTieBreakStrategy`/`EarliestToReachTieBreakStrategy`
  implement `TieBreakStrategy`.
- **Has-a** / **Is-a** — deliberately absent; there's no ownership relationship between
  `VotingService` and the per-call tallies it builds (they're local to one `getResults` call, not
  held as instance state — see ⑧), and no inheritance hierarchy here.

**Ideal class diagram:** source at
[`diagrams/session09-ideal-class-diagram.mmd`](../../../../../../../practice/diagrams/session09-ideal-class-diagram.mmd).

## ④ Pattern Choice & Justification

**Pattern used: Strategy** (`TieBreakStrategy`), consistent with this repo's convention.

The practice code's `TieBreakerStrategy` interface already had the right shape — this doesn't fix
a *missing* pattern, it fixes a **broken instance of a correctly-chosen one**: the strategy field
was `static` (`ResultService.java:13`), so constructing a second `ResultService` with a different
strategy silently rewired every existing instance's behavior. Here, `tieBreakStrategy` is a plain
`final` instance field, set once in the constructor and never shared across instances.

**Why the tie-break strategy only ever sees already-tied candidates:** `VotingService`'s own
comparator checks `getTotalPoints()` first and only delegates to `tieBreakStrategy.compare(...)`
when those are equal. This is a deliberate separation the practice code didn't have — both
`CountPlaceViseStrategy` and `FirstScorerStrategy` recomputed the full point total themselves
(duplicated in both classes) before ever reaching their actual tie-break logic. The base sort rule
("most points wins") is fixed by the problem statement itself, not something that varies — only
the tie-break does — so it doesn't belong inside the Strategy at all.

**Proof the two strategies genuinely disagree, not just differ in theory:** see ⑦.

## ⑤ Core Classes/Interfaces

Signatures only — full implementations are in
[`src/main/java/com/shubham/app/votingalgorithm/ideal/`](../../../../../../../src/main/java/com/shubham/app/votingalgorithm/ideal),
tests in
[`src/test/java/com/shubham/app/votingalgorithm/ideal/`](../../../../../../../src/test/java/com/shubham/app/votingalgorithm/ideal):

```java
class Ballot {
    Ballot(List<String> rankedCandidates);   // 1-3 candidates, no duplicates, no blank names
    List<String> getRankedCandidates();
}

class CandidateTally {
    CandidateTally(String name);
    int getTotalPoints();
    int getPositionCount(int rank);
    int getLastContributingBallotIndex();
    // recordVote(int rank, int ballotIndex) is package-private - only VotingService calls it
}

interface TieBreakStrategy { int compare(CandidateTally a, CandidateTally b); }

class VotingService {
    VotingService(TieBreakStrategy tieBreakStrategy);   // requires a non-null strategy
    List<String> getResults(List<Ballot> ballots);
}
```

**Why `recordVote` is package-private, not public:** `CandidateTally`'s state should only ever be
mutated by `VotingService` as it processes ballots in order — the same "mutator reachable only by
the owning service, in the same package" convention as `Connection.occupy()`/`free()` in the
connection-pool `ideal/` design.

**Why `VotingService.getResults` doesn't accumulate state across calls:** the verified source's
actual signature (`List<String> getResults(List<List<String>> ballots)`) is a pure function of
the ballots passed in, and the interviewer explicitly said the winner is only declared after all
votes are in. Each call builds a fresh `Map<String, CandidateTally>` locally rather than mutating
shared instance state — which also happens to be what makes the static-field bug (④) impossible to
reintroduce by accident.

## ⑥ Exception Handling

- `InvalidBallotException` — thrown by `Ballot`'s constructor for a null/empty ballot, more than 3
  ranked candidates, a blank/null candidate name, or the same candidate ranked twice; also thrown
  by `VotingService.getResults` for a null ballot list.
- `InvalidVotingConfigException` — thrown by `VotingService`'s constructor for a null
  `TieBreakStrategy`.
- Edge case handled **without** an exception: a ballot ranking only 1 or 2 candidates (not all 3)
  is explicitly valid — "up to three," per the problem statement — not an error. The practice
  code's fixed `for (i = 0; i < 3; i++)` loop crashed on exactly this input.

## ⑦ Primary-Flow Walkthrough — the actual fix for this session's bug

Source at
[`diagrams/session09-ideal-sequence-diagram.mmd`](../../../../../../../practice/diagrams/session09-ideal-sequence-diagram.mmd).

`Main.java` runs the same ballots through both strategies and gets genuinely different, both
individually correct, answers — proof the Strategy pattern here isn't cosmetic:

```
ballots = [[X,B,Y], [X,Y,B], [A,X,Y]]   // A and B both total 3 points
RankPositionTieBreakStrategy:    [X, Y, A, B]   (A has 1 first-place vote, B has 0)
EarliestToReachTieBreakStrategy: [X, Y, B, A]   (B's score was locked in at ballot index 1,
                                                   A's only at index 2)
```

This is also the fix for the practice code's `FirstScorerStrategy`, which was a `// TODO : fix
this` stub falling back to alphabetical order on every tie. The reason it was stuck: the data it
received (`int[] vote` — a position-count summary) has no timestamp information at all, so there
was no way to answer "who reached their total first" from it. `CandidateTally.lastContributingBallotIndex`
is the fix — since points only ever accumulate, a candidate's final total is reached exactly when
they're last awarded any points, so comparing that one index per candidate is sufficient; no
score-over-time history needs to be recorded. `VotingServiceTest#theTwoTieBreakStrategiesCanDisagreeOnTheSameInput`
is the regression test locking in the above example.

## ⑧ Concurrency & Extensibility

**Concurrency:** explicitly out of scope (see ①) — not addressed, consistent with the problem
never raising it.

**Extensibility:**
- A new tie-break rule → one new `TieBreakStrategy` implementation, zero changes to
  `VotingService` — this is the "Open-Closed Principle" extensibility the problem's own follow-up
  framing names explicitly.
- A points-per-rank scheme other than 3/2/1 is **not** a drop-in extension today —
  `CandidateTally.recordVote` hardcodes `RANK_POSITIONS - rank`. If that needed to vary, it would
  become a third Strategy (a `ScoringPolicy`), the same shape as the other two.
