# Voting Algorithm

> `[LLD]` — reported as an Atlassian DSA-round question that escalates into an OOD/extensibility
> discussion via follow-ups. Moved here from the
> [DSA repo](https://github.com/ShubhamChouksey123/data-structures-java-solution/blob/master/docs/company-specific/atlassian/README.md#design--simulation-multi-part-escalating--closest-match-to-the-recruiter-described-format),
> which flagged this entry as "medium confidence — exact source post couldn't be re-verified."
> That source **has now been verified directly** —
> [leetcode.com/discuss/interview-question/6061641](https://leetcode.com/discuss/interview-question/6061641)
> ("Atlassian Interview DSA round") — and matches closely: the base problem is LeetCode 1366
> ("Rank Teams by Votes"), with the Atlassian round's own follow-ups asking for two different,
> swappable tie-break strategies. This file only states the problem — no solution/design yet.

## Problem Statement (verbatim from the source)

> Process a list of ballots, and return all candidates sorted in descending order by their total
> number of points.
>
> ```
> List<String> getResults(List<List<String>> ballots)
> ```
>
> We pass in a list of ballots and we return a list of candidate names in descending order of the
> total number of points that each candidate received. Assume that we extract the candidates'
> names from the votes as we process them. A voter is allowed to vote for up to three different
> candidates. The order of the votes is important. The first vote that a voter places is worth
> three points. The second vote is worth two points. The third vote is worth one point. The
> function should return a list of candidates in descending order of the total number of points
> received by the candidate.

## Follow-ups (verbatim from the source)

1. **How to handle the scenario where the candidates have the same points?** What strategies can
   be used.
2. The interviewer proposed **two concrete tie-break strategies**, and asked for both to be
   implemented, selectable based on the input:
   - **Strategy 1:** if two candidates (A, B) have the same points, declare whichever one reached
     that point total *first* (i.e. earliest, across the sequence of ballots) as the winner.
   - **Strategy 2:** compare candidates by how many ballots ranked them **1st**; if still tied,
     compare by how many ranked them **2nd**; then **3rd** (matching LeetCode 1366's own
     published tie-break rule).

The DSA repo's framing adds that the reported extensibility angle is discussing this via the
**Open-Closed Principle** — i.e. the two strategies above should be swappable without modifying
the core ballot-counting logic, which is why this is tracked here as an OOD/extensibility exercise
rather than a pure algorithm problem.

## Worked Example

Base case — no tie (this exact example appears in one of the source's own comments):

```
ballots = [[A,B,C], [B,A,C], [C,A,B]]
```

| Ballot | 1st (3 pts) | 2nd (2 pts) | 3rd (1 pt) |
|---|---|---|---|
| `[A,B,C]` | A | B | C |
| `[B,A,C]` | B | A | C |
| `[C,A,B]` | C | A | B |

Totals: `A = 3+2+2 = 7`, `B = 2+3+1 = 6`, `C = 1+1+3 = 5`.

```
getResults(ballots) -> [A, B, C]
```

Tie-break case — Strategy 1 ("reached that point total first"):

```
ballots = [[A], [B]]
```

Ballot 1 (`[A]`) gives A 3 points (a ballot with only one name still awards that name the 1st-place
value); ballot 2 (`[B]`) gives B 3 points. Final totals are tied at `A = 3, B = 3` — but A reached
its final total at ballot index 1, while B only reached its final total at ballot index 2, so
Strategy 1 resolves the tie in favor of **A**.

```
getResults(ballots, strategy=1) -> [A, B]
```

(A worked example for Strategy 2 — the rank-position tie-break — is left for whoever implements
it; constructing one requires a scenario where the point totals tie *and* the 1st/2nd/3rd-place
counts differ in a way that doesn't also resolve under Strategy 1 the same way, which is easy to
get subtly wrong by hand — better verified against actual code than guessed here.)

## Things to clarify before designing

- **Candidate identity.** The source's own community solutions vary between `String` names and
  single `char`/`String` codes (`"A"`, `"B"`, ...) — not load-bearing for the algorithm, but worth
  picking deliberately.
- **"Reached that point total first" (Strategy 1), precisely.** The source doesn't define a tie
  within *this* tie-break — e.g. if A and B both reach the same final score on the exact same
  ballot index, Strategy 1 as stated doesn't say who wins. One commenter on the source raised
  exactly this ambiguity.
- **Whether the two strategies are chosen once per call, or configured once for the whole
  service.** The source says "implement both... based on the input," which suggests the former,
  but doesn't fully specify the selection mechanism.
- **Streaming vs. batch.** The DSA repo's description says "a stream of votes," but the verified
  source's actual signature (`getResults(List<List<String>> ballots)`) takes a fully-materialized
  list, and the interviewer explicitly said the winner is declared "only after getting all the
  votes" — not computed incrementally. Treat "stream" loosely here, not as a hard requirement to
  support partial/incremental results.
