# Subscription / Billing Service

> `[LLD]` — reported in Atlassian's interview loop as a standalone coding round. Moved here from the
> [DSA repo](https://github.com/ShubhamChouksey123/data-structures-java-solution/blob/master/docs/company-specific/atlassian/README.md#design--simulation-multi-part-escalating--closest-match-to-the-recruiter-described-format),
> modeled the way the Salesforce LLD checklist problems are tracked in that repo's
> `docs/company-specific/salesforce/README.md`. This file only states the problem — no
> solution/design yet.
>
> Verified directly against the primary source —
> [leetcode.com/discuss/post/6907694](https://leetcode.com/discuss/post/6907694/atlassian-p50p40-interview-experience-by-jtfe/)
> ("Atlassian P50/P40 interview experience") — rather than taken on trust from the DSA repo's paraphrase. See
> "Source note" below for what changed after verification.

## Problem Statement (verbatim from the source's "Coding Round")

> **Question:** Write scalable and extensible code for a subscription management service.
>
> **Scenario:**
> - `JIRA` — $10/month
> - `CONFLUENCE` — $7.5/month
> - `BITBUCKET` — $8/month
> - Users can buy any combination.
> - For a given year, calculate monthly billing based on usage.
>
> **Example:** `JIRA`: 14 days, `CONFLUENCE`: 30 days, `BITBUCKET`: 7 days

**Suggestion given in the source:** be ready to write clean, extensible code in your chosen language —
*interviewers look at how many lines you'd need to change to support new requirements.*

## Follow-ups (verbatim from the source)

1. How would you handle **discounts**?
2. How would you handle **trial periods** (extensible design)?

## Things to clarify before designing

The source states the scenario and the two follow-up questions, but — like the rate limiter problem's own
notes file — doesn't spell out every input/output detail. Worth clarifying before/during a mock attempt:

- **Exact API shape.** The source doesn't give method signatures. A `monthlyCostList()`/`annualCost()`-style
  API is a reasonable shape to propose (and is what the DSA repo's own tracker suggested), but it's an
  inference, not a quote — confirm/adjust during the mock rather than treating it as given.
- **What "usage" means.** Is "JIRA: 14 days" calendar days the subscription was active that month, or
  tracked activity days? The source doesn't say.
- **Proration.** Is the per-day rate `monthlyPrice / daysInThatMonth`, or something else (e.g. a flat
  30-day denominator)?
- **Discount shape.** "How would you handle discounts?" is open-ended in the source — e.g. a flat bundle
  discount for 2+ products, a percentage, or tiered by product count are all plausible follow-up designs.
- **Trial shape.** Same for trial periods — per-product trial length, one trial per customer, etc.

## Source note

An earlier version of this file quoted the source as saying "write modular, extensible code using OOP
principles" and listed an `[HLD]` "Cost Explorer" follow-up framed as a partitioning/caching discussion.
Neither held up under direct verification of the source:

- The actual quote is *"Write scalable and extensible code for a subscription management service"* — close
  in spirit, but not what was previously quoted.
- The "Cost Explorer" framing turned out to belong to a **different** Atlassian interview report
  ([leetcode.com/discuss/post/6725295](https://leetcode.com/discuss/post/6725295/atlassian-p40-software-engineer-2-by-ano-2tvf)),
  describing a genuinely different pricing model — tiered plans (`BASIC $9.99`, `STANDARD $49.99`,
  `PREMIUM $249.99`) with mid-year plan changes per product, not "any combination of JIRA/CONFLUENCE/BITBUCKET
  billed by usage-days." That post also doesn't mention partitioning or caching — the OP describes it as a
  coding exercise they only partially solved, same as this one. The `[HLD]`/partitioning-caching claim isn't
  substantiated by either source actually found, so it's been dropped here rather than carried forward
  unverified.
