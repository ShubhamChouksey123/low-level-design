# Snake and Ladder — Ideal Design (implemented)

This package is the working implementation of the **"The ideal design"** section in
[`practice/session-01-snake-and-ladder.md`](../../../../../../../../practice/session-01-snake-and-ladder.md) — the
reference answer written after reviewing [`../practice`](../practice)'s mock-interview attempt against
[`concepts/answer-framework.md`](../../../../../../../../concepts/answer-framework.md).

## What changed vs. the doc (and why)

Two small refinements surfaced while actually writing the code — both in the direction the design doc's own
reasoning already pointed:

- **No separate "setup service" for the board.** The doc described `GameSetupService` as "validates and builds a
  `Board`." Once `Board`'s constructor does that validation itself (bounds, duplicate jump-starts), a wrapper
  service that just calls `new Board(...)` doesn't add anything — so it was dropped. `Main` constructs the `Board`
  directly.
- **`Snake`/`Ladder` each validate their own direction.** The doc's "What lost points" table flagged *that*
  validation was missing, without saying *where* it should live. Giving each class its own directional check
  (`Snake` requires `start > end`, `Ladder` requires `start < end`) — rather than putting that check in `Board`,
  which only knows about the generic `BoardJump` contract — is the same encapsulation argument from
  [`concepts/basic-oop-concepts.md`](../../../../../../../../concepts/basic-oop-concepts.md) §1: each class
  protects its own invariant. `Board` only validates the cross-cutting concerns no single jump could check by
  itself (bounds relative to *this* board, no two jumps starting on the same square).

Only `PlayerRegistrationService` survives from the doc's `GameSetupService` — registering players is a real,
standalone responsibility with nothing to merge it into.

## The bug this design fixes

The original mock's `rollDice()` threw once *any* player won, but the turn loop finished the whole round first —
so a second player's turn in the same round crashed the program. `GamePlayService.playTurn(Player)` here returns
`boolean` instead of throwing, and `Main`'s loop checks it after **every individual turn**, `break`-ing out of the
round immediately — see `Main.java` and the regression test in
`GamePlayServiceTest#secondPlayersTurnInTheSameRoundDoesNotCrashOnceFirstPlayerWins`.
