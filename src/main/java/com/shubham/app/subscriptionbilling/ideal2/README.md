# Subscription / Billing Service — Ideal Design, 40-Minute Scope

A scaled-down version of
[`ideal/`](../ideal/README.md), built to the question: *what would actually fit in a 40-minute
interview round?* Everything structural is identical — same `Product`/`UsageRecord`/`Bill`
entities, same `YearMonth`-based proration, same `BillingService.monthlyBills`/`annualCost`. The
one thing deliberately cut is **concrete Strategy implementations**: `TrialPolicy` and
`DiscountPolicy` still exist as interfaces (the extensibility seam the interviewer is actually
grading), but only `NoTrialPolicy`/`NoDiscountPolicy` ship — the real policies
(`FixedTrialPeriodPolicy`, `BundleDiscountPolicy`) from `ideal/` are not reimplemented here.

## Why this split, not a different cut

Reviewing `practice/`'s mock turned up two kinds of gaps (see
[`practice/session-08-subscription-billing.md`](../../../../../../../practice/session-08-subscription-billing.md)):

1. **Cheap-but-wrong fixes** — `Product.days` hardcoded to 30, and `getMonthlyCost` only ever
   handling one month. Both cost nothing extra to get right: `YearMonth.lengthOfMonth()` is a
   type choice, not more code, and wrapping one month's logic in a 12-iteration loop
   (`monthlyBills`) is a few lines. There's no excuse to skip either under any time constraint.
2. **Genuinely time-expensive extras** — two fully-implemented, fully-tested `DiscountPolicy`
   and `TrialPolicy` concrete classes, each with their own validation and edge cases. This is
   real design work the interviewer is probing for ("how would you handle discounts?" is a
   *follow-up*, asked after the base implementation, often verbally rather than as more code).

So the cut isn't "do less correctness" — the `YearMonth` fix and the year-spanning API are both
still here, fully implemented, because they were never actually expensive. The cut is specifically
the concrete policy implementations, because *those* are the part that's reasonable to describe
rather than fully write under a 40-minute clock: *"`DiscountPolicy` is the seam — a bundle discount
would be a new class here, implementing this interface, with zero changes to `BillingService`."*

## What's the same as `ideal/`

`Product`, `UsageRecord`, `BillLineItem`, `Bill`, `ProductCatalog`, `UsageRepository`,
`BillingService`, and the `TrialPolicy`/`DiscountPolicy` interfaces themselves — all identical in
shape and behavior to [`ideal/`](../ideal). `Main.java` reproduces the same three proof points
(practice-scenario match, Feb leap-year proration difference, full-year `monthlyBills`/`annualCost`)
and prints identical numbers — verified by running both.

## What's different

- `service/` has exactly two classes (`NoTrialPolicy`, `NoDiscountPolicy`) instead of `ideal/`'s
  four — no `FixedTrialPeriodPolicy`, no `BundleDiscountPolicy`.
- One exception class dropped: `InvalidBillingConfigException` only existed to validate the two
  removed policies' constructor arguments (trial length, discount percentage) — with those classes
  gone, keeping the exception would just be unused code, the exact "named-but-unused" gap this
  whole review tracker keeps flagging in `practice/` code. `InvalidProductException` and
  `InvalidUsageException` remain, since `Product`/`UsageRecord` still validate themselves.
- No diagrams and a shorter test suite (4 tests instead of ~17) — proportionate to what an actual
  40-minute attempt would produce, not padded out to match `ideal/`'s documentation depth.
