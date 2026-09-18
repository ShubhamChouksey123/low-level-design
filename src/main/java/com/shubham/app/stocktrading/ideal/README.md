# Stock Trading & Matching — Ideal Design (implemented)

This package is the working implementation of the **"The ideal design"** section in
[`practice/session-04-stock-trading.md`](../../../../../../../../practice/session-04-stock-trading.md) — the
reference answer written after reviewing [`../practice`](../practice)'s mock-interview attempt against
[`concepts/answer-framework.md`](../../../../../../../../concepts/answer-framework.md).

## What changed vs. the practice attempt (and why)

- **The matching algorithm actually exists.** The practice attempt's `MatchingEngine.match(int stockId)` fetched
  an `OrderBook` and did nothing with it — the literal core requirement of the problem was unwritten.
  `OrderBook.submit(Order)` here matches an incoming order against the opposite side of the book, walking price
  levels best-first (price priority) and orders within a level oldest-first (time priority — FIFO on a `Deque`),
  producing zero or more `Trade`s and resting any unfilled remainder.
- **One unified matching loop instead of duplicated BUY/SELL branches.** The practice attempt's
  `OrderBook.addOrder`/`removeOrder` each had a near-identical `if (SELL) {...} else {...}` branch. The ideal
  design picks the opposite/own side maps once at the top of `submit(...)` and runs one loop — the only thing
  that varies by side is a three-line `crosses(...)` check.
- **`TreeMap` instead of a `Map` + a separately-maintained `PriorityQueue` of prices.** The practice attempt kept
  a `Map<Double, List<Order>>` *and* a `PriorityQueue<Double>` of prices that has to be manually kept in sync —
  emptying a price bucket in the map never removed that price from the heap, so the two structures could drift
  apart. A single `NavigableMap<Double, Deque<Order>>` (ascending for asks, descending for bids via
  `Collections.reverseOrder()`) gives sorted-by-price access without a second structure to desynchronize.
- **`Order` gained a `userId` field.** The practice attempt's `Trade` javadoc documented `sellerId`/`buyerId`
  fields, but `Order` never recorded *who* placed it — there was no way to ever populate those fields from a real
  order. `Order` now takes the placing user's id at construction.
- **`Order` validates itself and is otherwise immutable** (`price`, `quantity` checked in the constructor; only
  `remainingQuantity` mutates, via `fill(...)`) instead of a no-arg constructor plus unchecked setters for every
  field.
- **The exception package is lowercase** (`exception`, not `Exception`) to match every other design in this repo,
  and `InvalidOrderException` drops the practice attempt's typo'd extra "I" (`InvalidIOrderException`).
- **`OrderType` is renamed `Side`.** "Side" (of the book) is the standard term in real order-book design — an
  order is on the buy side or the sell side of a specific stock's book.
- **A `TradePricingStrategy` — the Strategy pattern.** Which price a trade executes at, when a buy and a sell
  cross, is itself a real, configurable policy in actual exchanges (resting order's price vs. the aggressor's
  price vs. midpoint). `RestingOrderPriceStrategy` — trade at the price of whichever order was already resting in
  the book — is the one implementation here, matching the requirement's own wording ("best available price").

## The gap this design fixes

The practice attempt had no reachable code path for its own core feature: `addOrder`/`removeOrder` were `private`
with no public caller, and `match(...)` never actually matched anything. There was nothing to walk — no
`Main.java`, no test, no way to exercise the design at all. `OrderBook.submit(...)` here is the single public
entry point that both rests and matches orders, and `Main.java`'s demo plus
`OrderBookTest#aCrossingOrderConsumesTheBestPriceLevelFirstThenPartiallyFillsTheNextLevel` walk the flow the
practice attempt's own requirement asked for: an incoming order matching across multiple price levels in
price-time order, with a partial fill on the second level.
