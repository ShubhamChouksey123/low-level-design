# Session 08 — Subscription / Billing Service (LLD/OOD) · 1.8/10

> This session's single demoed call computed the right number — but it's the only thing that was
> ever exercised. The problem's actual core requirement ("for a given year, calculate monthly
> billing") was never attempted even once: there's no method that produces more than one month at
> a time. Neither of the two explicit follow-ups (discounts, trial periods) has any
> implementation or even an extensibility seam, despite the interviewer explicitly grading "how
> many lines you'd need to change." No diagram was drawn. Two of the four entities (`User`,
> `Bill`) are dead code — `User` has a typo'd field and is never actually used for anything.

**Problem:** Design a subscription/billing service for JIRA ($10/mo), CONFLUENCE ($7.5/mo), and
BITBUCKET ($8/mo); users buy any combination; given a year of usage, calculate monthly billing;
follow-ups add discounts and trial periods · **Focus:** class design, no architecture/scale
concerns · **Overall:** 1.8/10 · **Weakest areas:** Pattern Choice & Justification, Class Diagram
& Relationships, Implementation & Exceptions, Correctness · **Artifacts:**
[`Main.java`](../src/main/java/com/shubham/app/subscriptionbilling/practice/Main.java),
[entity/](../src/main/java/com/shubham/app/subscriptionbilling/practice/entity/),
[service/](../src/main/java/com/shubham/app/subscriptionbilling/practice/service/),
[README.md](../src/main/java/com/shubham/app/subscriptionbilling/practice/README.md) (the problem
statement — no class diagram was produced for this mock)

## The problem

> Write scalable and extensible code for a subscription management service.
>
> - JIRA — $10/month, CONFLUENCE — $7.5/month, BITBUCKET — $8/month
> - Users can buy any combination.
> - For a given year, calculate monthly billing based on usage.
> - Example: JIRA 14 days, CONFLUENCE 30 days, BITBUCKET 7 days.
>
> Follow-ups: how would you handle discounts? How would you handle trial periods (extensible
> design)?

What it really tests: whether a design built around *one month at a time* can be read, by its own
author, as satisfying "given a year, calculate monthly billing" — and whether the two follow-ups
have an actual seam to plug into, or would require editing the core billing method's body.

## Requirements & entities gathered

No separate requirements/entities document was written for this mock — the only README under
`practice/` is the problem statement itself (verified against the primary source in an earlier
session). What was produced is the entity list the code actually contains:

- `Product` — id, name, `basePrice`, and a `days` field that doesn't belong on a product at all
  (see lost-point #1) — no responsibility beyond holding data, and no `getName()` to read it back.
- `ProductUsage` — reasonable: productId, usedInDays, userId, month.
- `User` (`practice/entity/User.java:5`) — a typo'd field (`private int it;` / `getIt()`) that is
  never actually used anywhere; `BillingService`/`UsageService` key everything by a raw
  `int userId` instead.
- `Bill` (`practice/entity/Bill.java`) — an entirely empty class, never instantiated or
  referenced anywhere in the code.

Gaps against [step ① and ②](../concepts/answer-framework.md#-requirements--scope) of the
framework: two of the four entities (`User`, `Bill`) are dead code, and the one entity that *is*
load-bearing (`Product`) conflates a billing-period concept (`days`) with the product itself.

## The design produced

No diagram — as-drawn or otherwise — exists anywhere under
[`src/main/java/com/shubham/app/subscriptionbilling/practice/`](../src/main/java/com/shubham/app/subscriptionbilling/practice)
for this mock, same gap as Session 07.

What the code's actual (undrawn) relationships are:
- `BillingService` — has-a `UsageService`, has-a `ProductService`; uses both directly, with no
  interface between `BillingService` and anything it calls.
- `ProductService` — has-a `List<Product>`. `UsageService` — has-a `List<ProductUsage>`.
- `User` and `Bill` have no relationships at all, because nothing references them.

## Scorecard

| Axis | Score | Note |
|---|---|---|
| Requirements & Entities | 3/10 | Two of four entities (`User`, `Bill`) are dead code; `Product` conflates a billing-period concept with product identity. |
| Class Diagram & Relationships | 1/10 | No diagram produced at all — same floor as Session 07. |
| Pattern Choice & Justification | 1/10 | No pattern anywhere — not even a correctly-shaped-but-unwired interface the way Session 07's `RateLimitChecker` was. Zero extensibility seam for either follow-up. |
| Implementation & Exception Handling | 2/10 | Only two narrow validations exist (unknown product id, negative days); non-positive price, zero `days` (division by zero), and usage exceeding a month's real length all pass silently. |
| Correctness (Primary-Flow Walkthrough) | 2/10 | The one demoed call's arithmetic is correct, but the entire "year" dimension — the problem's literal core ask — was never exercised even once. |
| **Overall** | **1.8/10** | ❌ Needs work |

## What lost points — and the fix

| What I missed | The senior answer | Study link |
|---|---|---|
| `practice/entity/Product.java:8` — `days` is a mutable field on `Product` (`setDays`), hardcoded to `30` for every product at construction (`practice/Main.java:16-18`), so proration always divides by 30 regardless of which calendar month is actually being billed. | Use `java.time.YearMonth` for the billing period — it already knows the real day count (`lengthOfMonth()`, 28/29/30/31) — see `ideal/UsageRecord`. | [`concepts/answer-framework.md`](../concepts/answer-framework.md) ② Core Entities & Responsibilities |
| `practice/service/BillingService.java:18` — `getMonthlyCost(userId, month)` only ever computes one month. There is no method that produces a full year's bills or an annual total, even though the problem explicitly says "for a given year, calculate monthly billing." | `BillingService.monthlyBills(userId, year)` / `annualCost(userId, year)` — see `ideal/BillingService`. | [`concepts/answer-framework.md`](../concepts/answer-framework.md) ① Functional Requirements & Scope |
| No discount or trial-period logic, and no extensibility seam for either — both are the interviewer's explicit follow-ups, graded on "how many lines you'd need to change." | Two Strategy interfaces, `TrialPolicy`/`DiscountPolicy`, injected into `BillingService` — see ④ in `ideal/README.md`. | [`concepts/answer-framework.md`](../concepts/answer-framework.md) ④ Pattern Choice & Justification |
| `practice/service/UsageService.java:20-28` — `addUsage` just appends; `BillingService.getMonthlyCost` sums *every* matching record with no dedup. Two usage entries for the same product in the same month silently double-bill it. | `UsageRepository.recordUsage` upserts per `(userId, productId, period)` — see `ideal/UsageRepository`. | [`concepts/answer-framework.md`](../concepts/answer-framework.md) ⑥ Exception Handling |
| `practice/entity/Bill.java` is a completely empty class, never used — `getMonthlyCost` returns a bare `double` with no per-product breakdown. | `ideal/Bill` + `ideal/BillLineItem` — an itemized invoice with per-product amounts, subtotal, discount, and total. | [`concepts/answer-framework.md`](../concepts/answer-framework.md) ② Core Entities & Responsibilities |
| `practice/entity/User.java:5` — `private int it;` (typo for `id`), `getIt()` — and the class is never actually used anywhere; everything is keyed by a raw `int userId` instead. | Dropped entirely in `ideal/` — nothing in the data shape needs a `User` object; see `ideal/README.md`'s ① out-of-scope note for why. | [`concepts/answer-framework.md`](../concepts/answer-framework.md) ② Core Entities & Responsibilities |
| No validation anywhere of a non-positive `basePrice` or a zero/negative `days` (division by zero on `getPricePerDay()`), and no check that `usedInDays` doesn't exceed the actual days in that month. | `InvalidProductException`/`InvalidUsageException` thrown at construction — see `ideal/Product`/`ideal/UsageRecord`. | [`concepts/answer-framework.md`](../concepts/answer-framework.md) ⑥ Exception Handling |
| `practice/service/ProductService.java:25-31` and `UsageService.java:30-41` — linear scans over every product/every usage record on every single lookup, despite the interviewer explicitly saying "write scalable... code." | `ProductCatalog`/`UsageRepository` indexed by id and by `(userId, period)` for O(1)-ish lookups — see `ideal/`. | [`concepts/answer-framework.md`](../concepts/answer-framework.md) ⑧ Concurrency & Extensibility |

## What went well

- The core proration formula itself (`basePrice / days * usedInDays`) is correct for the one
  scenario it was run against — verified by hand-trace against the actual printed output
  (`14.033333333333333`, matching `10×14/30 + 7.5×30/30 + 8×7/30` exactly).
- `ProductService`/`UsageService`/`BillingService` are at least split into three
  single-responsibility classes rather than one monolith — a reasonable structural instinct, even
  though no interface/pattern connects them.
- `InvalidProductId`/`InvalidUsageException` exist and are thrown for the two cases they do cover
  (unknown product id, negative days) — not comprehensive, but not nothing either.

---

## The ideal design

> **Implemented** in
> [`src/main/java/com/shubham/app/subscriptionbilling/ideal/`](../src/main/java/com/shubham/app/subscriptionbilling/ideal)
> — see that package's own
> [`README.md`](../src/main/java/com/shubham/app/subscriptionbilling/ideal/README.md) for the full
> write-up.

**Framing sentence:** the problem names two independent extensibility follow-ups (discounts,
trial periods), so the crux is recognizing they're two separate Strategies, not one — and that
"given a year, calculate monthly billing" means the API has to operate across all twelve months,
not just the one the practice code ever tried.

The rest of this section follows [`concepts/answer-framework.md`](../concepts/answer-framework.md)'s
8 steps in order; see `ideal/README.md` for the complete version with full prose per step. Summary:

### ① Functional Requirements & Scope

Register products with a monthly price; record per-product, per-month usage days; produce an
itemized `generateMonthlyBill`, a `monthlyBills` across a year, and an `annualCost`. Out of scope:
persistence, mid-month plan changes, and a `User` entity (nothing in the data shape needs one).

### ② Core Entities & Responsibilities

`Product`, `UsageRecord`, `ProductCatalog`, `UsageRepository`, `BillLineItem`, `Bill`,
`TrialPolicy`/`DiscountPolicy` (interfaces), `BillingService`. See `ideal/README.md` for the full
responsibility table.

### ③ Relationships & Class Diagram

Source at
[`diagrams/session08-ideal-class-diagram.mmd`](diagrams/session08-ideal-class-diagram.mmd).

![A class diagram showing BillingService using a ProductCatalog, a UsageRepository, a TrialPolicy, and a DiscountPolicy, with ProductCatalog having a collection of Product and UsageRepository having a collection of UsageRecord, Bill having a collection of BillLineItem, and NoTrialPolicy and FixedTrialPeriodPolicy each implementing the contract of TrialPolicy while NoDiscountPolicy and BundleDiscountPolicy each implement the contract of DiscountPolicy.](diagrams/session08-ideal-class-diagram.png)

### ④ Pattern Choice & Justification

**Pattern used: Strategy, applied twice** — `TrialPolicy` and `DiscountPolicy` — because the two
follow-ups are independent and shouldn't be coupled into one god-interface. This directly fixes
this session's #1 lost-point: there was no extensibility seam at all. See `ideal/README.md` ④ for
the full justification, including why `TrialPolicy` operates per-product while `DiscountPolicy`
operates on the whole bill.

### ⑤ Core Classes/Interfaces

See `ideal/README.md` ⑤ for signatures. Two choices worth calling out: money is `BigDecimal` (for
deliberate cent-rounding, not the exactness reason the rate limiter's `TokenBucketRateLimitingAlgorithm`
used it), and the billing period is `java.time.YearMonth` (fixes the hardcoded-`30` bug directly).

### ⑥ Exception Handling

`InvalidProductException`, `InvalidUsageException`, `InvalidBillingConfigException` — one per
validation domain (product data, usage data, policy configuration). See `ideal/README.md` ⑥.

### ⑦ Primary-Flow Walkthrough — the actual fix for this session's bug

Source at
[`diagrams/session08-ideal-sequence-diagram.mmd`](diagrams/session08-ideal-sequence-diagram.mmd).

![A sequence diagram showing BillingService generating a monthly bill for the same twenty days of JIRA usage in February 2025 and February 2024, dividing by twenty eight days in the first case and twenty nine days in the second because 2024 is a leap year, producing two different correct totals.](diagrams/session08-ideal-sequence-diagram.png)

The same 20 days of JIRA usage bills as `$7.14` in Feb 2025 (28 days) and `$6.90` in Feb 2024
(leap year, 29 days) — direct proof that proration uses the real length of the month, not a
hardcoded constant the practice code's `Product.days = 30` baked in regardless of which month was
being billed. `BillingServiceTest#prorationUsesTheActualNumberOfDaysInThatCalendarMonth` is the
regression test.

### ⑧ Concurrency & Extensibility

**Concurrency:** explicitly out of scope — the problem never mentions concurrent access, unlike
the rate limiter's Stage 4.

**Extensibility:** a new discount rule or trial reading costs one new class, zero changes to
`BillingService` — proven, not just claimed, by
`BillingServiceTest#trialPolicyAndDiscountPolicyPlugInWithNoChangesToBillingService`, which plugs
in `FixedTrialPeriodPolicy`/`BundleDiscountPolicy` on top of the exact same usage data as the
practice-matching scenario.

## Takeaways to drill

1. "For a given year, calculate monthly billing" is a scope statement about the *API*, not just
   the math — a method that only ever handles one month at a time hasn't actually implemented the
   requirement, even if its one-month math is correct.
2. When a problem names two independent follow-ups ("discounts" and "trial periods"), check
   whether they're actually independent before reaching for one interface to cover both — forcing
   them together couples two things that should vary separately.
3. An entity that's never constructed or read anywhere (`User`, `Bill` here) is worth deleting the
   moment you notice it's unused, not leaving in "in case." Two unused entities in one mock is a
   new high for this specific recurring gap.
4. Money needs `BigDecimal` for a *different* reason than a token bucket's "exact division by a
   power of ten" — here it's deliberate rounding to the cent, because real currency is
   discretized and a `double` sum of fractional cents isn't an amount anyone can actually be
   billed.
5. `java.time.YearMonth` (or an equivalent calendar-aware type) beats a bare `int month` + a
   hardcoded days-per-month constant every time the actual day count matters — it's one import,
   not a design decision to defer.

> Rehearse this against [`concepts/answer-framework.md`](../concepts/answer-framework.md) before
> the next mock.

## How to Improve

Before writing any code, restate the problem's scope statement back as a method signature (here:
"for a given year, calculate monthly billing" → a method that takes a year and returns 12 bills)
and check the signature actually matches the sentence — this session's core method only ever took
a single month, and that mismatch was never caught before the mock was called done.
