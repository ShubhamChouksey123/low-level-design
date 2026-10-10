# Subscription / Billing Service — Ideal Design

Reviewed against [`practice/`](../practice) (see that package's `README.md` for the problem
statement and the review discussion), then written directly against
[`concepts/answer-framework.md`](../../../../../../../concepts/answer-framework.md)'s 8 steps.

## ① Functional Requirements & Scope

**Actor:** Any caller billing a user for products used in a given month/year.

**Operations:**
- Register products, each with its own monthly price.
- Record how many days a user used a product in a given calendar month.
- `generateMonthlyBill(userId, period)` — an itemized `Bill` for one month.
- `monthlyBills(userId, year)` — a `Bill` for every month of a year (the problem's own
  `monthlyCostList()`-shaped API — inferred, not quoted; see `practice/README.md`'s source note).
- `annualCost(userId, year)` — the sum of a year's monthly bills (`annualCost()`).

**Explicitly out of scope:**
- Persisting products/usage across process restarts — everything lives in memory, same convention
  as every other `ideal/` design in this repo.
- Mid-month plan changes / a `User` entity with its own identity — the problem only ever hands
  over `(product, period, daysUsed)` tuples keyed by an opaque `userId`; nothing in the data shape
  needs a `User` object (the practice code's `User` class was created but never actually used for
  anything, including a typo'd field name — see the review for details).
- Which exact reading of "trial period" is correct — the problem statement doesn't say whether a
  trial is "first N days per month, every month" or "first N days ever, then never again." This
  design implements the first reading in `FixedTrialPeriodPolicy`; see ④ for why, and why the
  Strategy split means switching readings later costs one class, not a redesign.

## ② Core Entities & Responsibilities

| Entity | Responsibility |
|---|---|
| `Product` | Immutable id/name/monthly price. |
| `UsageRecord` | How many days one product was used by one user in one `YearMonth`; validates the day count against that month's actual length. |
| `ProductCatalog` | O(1) product lookup by id. |
| `UsageRepository` | Usage indexed by `userId` then `YearMonth`; upserts rather than accumulates per `(userId, productId, period)`. |
| `BillLineItem` | One product's billable days + prorated amount within a `Bill`. |
| `Bill` | One user's itemized invoice for one month — line items, subtotal, discount, total. |
| `TrialPolicy` (interface) | The Strategy for "how would you handle trial periods?" — reduces a product's raw usage days to billable days. |
| `DiscountPolicy` (interface) | The Strategy for "how would you handle discounts?" — reduces a month's subtotal. |
| `BillingService` | Orchestrates the above into `generateMonthlyBill`/`monthlyBills`/`annualCost`. |
| `InvalidProductException` / `InvalidUsageException` / `InvalidBillingConfigException` | Thrown for bad product data, bad usage data, and bad policy configuration respectively. |

## ③ Relationships & Class Diagram

- **Has-a** — `BillingService` has-a `ProductCatalog`, has-a `UsageRepository`, has-a `TrialPolicy`,
  has-a `DiscountPolicy`. `Bill` has-a `List<BillLineItem>`.
- **Uses** — `BillingService` uses `ProductCatalog`/`UsageRepository` to look up data, and uses
  `TrialPolicy`/`DiscountPolicy` to decide billable days and the discount.
- **Implements the contract of** — `NoTrialPolicy`/`FixedTrialPeriodPolicy` implement
  `TrialPolicy`; `NoDiscountPolicy`/`BundleDiscountPolicy` implement `DiscountPolicy`.
- **Is-a** — deliberately absent; there's no inheritance hierarchy here.

**Ideal class diagram:** source at
[`diagrams/session08-ideal-class-diagram.mmd`](../../../../../../../practice/diagrams/session08-ideal-class-diagram.mmd).

## ④ Pattern Choice & Justification

**Pattern used: Strategy**, applied twice — `TrialPolicy` and `DiscountPolicy` — because the
problem names *two independent* follow-ups ("how would you handle discounts?" and "how would you
handle trial periods?"), and nothing says they vary together. Keeping them as two separate
interfaces means a new discount rule never touches trial logic and vice versa — one combination
isn't privileged over another the way a single `BillingPolicy` god-interface would force.

This directly fixes the practice code's biggest gap: `BillingService.getMonthlyCost` had **no**
extensibility seam for either follow-up — adding either would have meant editing the method body
directly, which is exactly what the interviewer said they grade against ("how many lines you'd
need to change to support new requirements").

- **Why `TrialPolicy` operates per-product, not per-bill:** a trial is naturally a per-product
  concept (you don't get a trial on a product you've never tried), so `billableDays(Product, int)`
  is called once per usage record, before proration — not once per bill.
- **Why `DiscountPolicy` operates on the whole bill, not per-product:** a bundle discount is
  inherently about *how many products* were used together, which only the aggregate view has.

## ⑤ Core Classes/Interfaces

Signatures only — full implementations are in
[`src/main/java/com/shubham/app/subscriptionbilling/ideal/`](../../../../../../../src/main/java/com/shubham/app/subscriptionbilling/ideal),
tests in
[`src/test/java/com/shubham/app/subscriptionbilling/ideal/`](../../../../../../../src/test/java/com/shubham/app/subscriptionbilling/ideal):

```java
class Product {
    Product(int id, String name, BigDecimal monthlyPrice);   // requires a positive price
}
class UsageRecord {
    UsageRecord(int productId, YearMonth period, int daysUsed);   // 0 <= daysUsed <= period.lengthOfMonth()
}
class ProductCatalog {
    void register(Product product);
    Product getProduct(int productId);
}
class UsageRepository {
    void recordUsage(int userId, UsageRecord record);   // upserts per (userId, productId, period)
    List<UsageRecord> getUsage(int userId, YearMonth period);
}

interface TrialPolicy { int billableDays(Product product, int rawDaysUsed); }
interface DiscountPolicy { BigDecimal discountFor(BigDecimal subtotal, List<BillLineItem> lineItems); }

class BillingService {
    BillingService(ProductCatalog catalog, UsageRepository usageRepository,
                    TrialPolicy trialPolicy, DiscountPolicy discountPolicy);
    Bill generateMonthlyBill(int userId, YearMonth period);
    List<Bill> monthlyBills(int userId, int year);
    BigDecimal annualCost(int userId, int year);
}
```

**Money is `BigDecimal`, not `double`** (the practice code used `double`) — for a *different*
reason than the rate limiter's `TokenBucketRateLimitingAlgorithm` used it. There, `BigDecimal` gave
*exact* arithmetic (dividing nanoseconds by a power of ten always terminates). Here, dividing a
price by an arbitrary days-in-month (28/29/30/31) usually does **not** terminate exactly — the
point of `BigDecimal` here is deliberate, controlled *rounding to the cent* (`RoundingMode.HALF_UP`,
scale 2), because real money is discretized to cents and a `double` sum of fractional cents
(the practice code's `14.033333...`) isn't an amount anyone can actually be billed.

**Billing period is `java.time.YearMonth`, not a bare `int month`** (the practice code's
`ProductUsage.month`) — it already knows how many days are actually in that month
(`lengthOfMonth()`), which is exactly the hardcoded-`30` bug found in the practice review.

## ⑥ Exception Handling

- `InvalidProductException` — thrown by `Product`'s constructor for a blank name or non-positive
  price, and by `ProductCatalog.getProduct` for an unregistered id.
- `InvalidUsageException` — thrown by `UsageRecord`'s constructor for a null period or a day count
  outside `[0, period.lengthOfMonth()]`.
- `InvalidBillingConfigException` — thrown by `FixedTrialPeriodPolicy`/`BundleDiscountPolicy`'s
  constructors for an invalid trial length or discount percentage.
- Edge case handled **without** an exception: a product with zero usage in a month simply produces
  no line item — not an error.
- Edge case handled **without** an exception: recording usage twice for the same `(userId,
  productId, period)` replaces the earlier record rather than throwing or double-counting — see
  `UsageRepositoryTest#recordingUsageTwiceForTheSameProductAndPeriodReplacesRatherThanAccumulates`.

## ⑦ Primary-Flow Walkthrough

`Main.java` runs four scenarios. The first reproduces the practice code's own demo exactly (JIRA 14
days, CONFLUENCE 30 days, BITBUCKET 7 days) in a 30-day month, so the raw math matches before
rounding — then rounds each line item to the cent (`$4.67 + $7.50 + $1.87 = $14.04`), unlike the
practice code's un-roundable `14.033333...`.

The second scenario is **the actual fix for this session's bug**: the same 20 days of JIRA usage
billed in Feb 2024 (leap year, 29 days) versus Feb 2025 (28 days) produces *different* amounts
(`$6.90` vs. `$7.14`) — proof that proration uses the real length of the month, not the practice
code's hardcoded `30`. `BillingServiceTest#prorationUsesTheActualNumberOfDaysInThatCalendarMonth`
locks this in.

The third scenario calls `monthlyBills`/`annualCost` across a full year — the dimension the
practice code never implemented at all.

The fourth scenario plugs in `FixedTrialPeriodPolicy(5)` and `BundleDiscountPolicy(10%, 2)` on top
of the exact same usage data as the first scenario, with zero changes to `BillingService` itself —
proving ④'s extensibility claim. `BillingServiceTest#trialPolicyAndDiscountPolicyPlugInWithNoChangesToBillingService`
is the regression test.

## ⑧ Concurrency & Extensibility

**Concurrency:** explicitly out of scope — the problem never mentions concurrent access (unlike
the rate limiter's Stage 4), and `ProductCatalog`/`UsageRepository` are plain `HashMap`-backed, not
thread-safe. If this were extended to concurrent use, the same lesson from the rate limiter's
`RateLimiter` applies: index per-key (per `userId`) rather than taking one lock around every call.

**Extensibility:**
- A new discount rule or trial reading → one new `DiscountPolicy`/`TrialPolicy` implementation,
  zero changes to `BillingService` — proven by ⑦'s fourth scenario, not just claimed.
- A new product → one `catalog.register(...)` call; nothing else changes.
- A *combination* of discount rules (e.g. bundle discount **and** a loyalty discount) is **not** a
  drop-in extension of this design today — `BillingService` takes exactly one `DiscountPolicy`.
  Composing multiple policies would need a `CompositeDiscountPolicy` (itself a `DiscountPolicy`
  that delegates to a list of others) — a reasonable next step, not built here since the problem
  only ever asks about one discount at a time.
