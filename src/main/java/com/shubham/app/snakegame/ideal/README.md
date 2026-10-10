# Design Snake Game — Ideal Design

Reviewed against [`practice/`](../practice) (see that package's `README.md` for the problem
statement and the review discussion), then written directly against
[`concepts/answer-framework.md`](../../../../../../../concepts/answer-framework.md)'s 8 steps.

## ① Functional Requirements & Scope

**Actor:** Any caller driving the snake one move at a time.

**Operations:**
- `move(direction)` — advances the snake one cell; returns the current score (snake length - 1),
  or `-1` if this move ends the game (out of bounds, or a self-collision).
- Growth is a **swappable policy**: standard LeetCode 353 grows the snake when its head lands on
  the next food item, in order; the reported Atlassian variant grows it every 5 moves regardless
  of food. Both are implemented here (`FoodGrowthPolicy`, `EveryNMovesGrowthPolicy`).

**Explicitly out of scope:** nothing beyond the practice code's own scope — this directly answers
the practice `README.md`'s own open question ("does the variant keep food at all?") by making food
specific to one `GrowthPolicy` implementation rather than a property of the board every variant
shares.

## ② Core Entities & Responsibilities

| Entity | Responsibility |
|---|---|
| `Position` | A cell on the grid (record). |
| `Direction` | One of the four move vectors (enum). |
| `Board` | Just the grid's dimensions — no food. Immutable. |
| `Snake` | The body (deque) + occupied cells (set); decides self-collision correctly, accounting for whether this move grows the snake. |
| `GrowthPolicy` (interface) | The Strategy: decides whether a given move grows the snake. |
| `FoodGrowthPolicy` / `EveryNMovesGrowthPolicy` | The two concrete growth rules. |
| `SnakeGame` | Orchestrates a move: boundary check, growth decision, collision check, mutation; tracks whether the game has ended. |
| `InvalidBoardConfigException` / `InvalidGrowthConfigException` | Thrown for bad board dimensions and bad growth-policy configuration, respectively. |

## ③ Relationships & Class Diagram

- **Uses** — `SnakeGame` uses `Board`, `Snake`, and `GrowthPolicy`.
- **Has-a** — `Snake` has-a `Deque<Position>` and a `Set<Position>` of the same cells.
- **Implements the contract of** — `FoodGrowthPolicy`/`EveryNMovesGrowthPolicy` implement
  `GrowthPolicy`.
- **Is-a** — deliberately absent; no inheritance hierarchy here.

**Ideal class diagram:** source at
[`diagrams/session10-ideal-class-diagram.mmd`](../../../../../../../practice/diagrams/session10-ideal-class-diagram.mmd).

## ④ Pattern Choice & Justification

**Pattern used: Strategy** (`GrowthPolicy`) — the practice code had no pattern at all here; growth
was hardcoded directly into `SnakeGame.move` as a food-position check, with no seam for the
reported Atlassian variant the problem's own source names.

**Why growth is the Strategy, not something else:** everything about *how a move is validated*
(boundary, self-collision) is identical between the two variants — only *whether this move grows
the snake* differs. `GrowthPolicy.shouldGrow` is called exactly once per legal move and that's the
only place the two variants' logic diverges.

**Why food was moved off `Board` and into `FoodGrowthPolicy`:** this directly resolves an
ambiguity the practice `README.md` itself flagged — "does the variant keep food at all?" By making
food a detail of one specific `GrowthPolicy` implementation, `Board` (shared by both variants)
never has to answer that question; `EveryNMovesGrowthPolicy` simply has no food field at all.

## ⑤ Core Classes/Interfaces

Signatures only — full implementations are in
[`src/main/java/com/shubham/app/snakegame/ideal/`](../../../../../../../src/main/java/com/shubham/app/snakegame/ideal),
tests in
[`src/test/java/com/shubham/app/snakegame/ideal/`](../../../../../../../src/test/java/com/shubham/app/snakegame/ideal):

```java
record Position(int x, int y) {}
enum Direction { UP, DOWN, LEFT, RIGHT }   // each with its own (dx, dy)

class Board {
    Board(int height, int width);   // both must be positive
    boolean isOutside(Position position);
}

class Snake {
    Snake(Position start);
    Position getHead();
    int length();
    boolean wouldCollide(Position nextPosition, boolean grow);
    // move(Position, boolean) is package-private - only SnakeGame calls it
}

interface GrowthPolicy { boolean shouldGrow(Position nextHeadPosition); }

class SnakeGame {
    SnakeGame(Board board, GrowthPolicy growthPolicy);
    int move(Direction direction);
}
```

**Why `Snake` uses a `Set<Position>`, not a `boolean[][]` sized from the board:** the practice
code's grid meant `Snake`'s own correctness silently depended on `Board`'s dimensions never
changing after construction (and `Board` had public setters for exactly that). A set has no size
to desync in the first place — removing the coupling removes the bug class entirely, not just the
specific instance of it.

## ⑥ Exception Handling

- `InvalidBoardConfigException` — thrown by `Board`'s constructor for a non-positive
  height/width, and by `SnakeGame`'s constructor for a null `Board`.
- `InvalidGrowthConfigException` — thrown by `EveryNMovesGrowthPolicy`'s constructor for a
  non-positive interval, and by `SnakeGame`'s constructor for a null `GrowthPolicy`.
- Edge case handled **without** an exception: calling `move` again after the game has already
  ended returns `-1` immediately — not an error, just a no-op continuation of "the game is over."

## ⑦ Primary-Flow Walkthrough — the actual fix for this session's bug

Source at
[`diagrams/session10-ideal-sequence-diagram.mmd`](../../../../../../../practice/diagrams/session10-ideal-sequence-diagram.mmd).

`Main.java` runs four scenarios. The first reproduces LeetCode 353's own official example 1
exactly (`[0, 0, 1, 1, 2, -1]`) — the practice code already got this right.

The second is **the actual fix**: a length-2 snake (grown by eating one food) reverses directly
onto its own current tail cell. The practice code's `Snake.isOccupied` checked the grid *before*
the tail would be removed, so it saw the tail cell as still occupied and wrongly ended the game.
Here, `Snake.wouldCollide(nextPosition, grow)` is told whether this move grows the snake *before*
deciding collision — when it doesn't grow, the current tail is specifically excluded from the
collision check, since it vacates in the very same step the head arrives. Verified: the reversal
now correctly continues the game (`1`, not `-1`).
`SnakeGameTest#reversingOntoTheVacatingTailIsLegal` is the regression test.

The third scenario is a second, independently-found bug: the practice code had no `gameOver`
flag, and a losing move never mutated the snake's state — so calling `move` again after a `-1`
would silently resume play from the pre-collision state. `SnakeGame.gameOver` fixes this: once
set, every subsequent `move` call returns `-1` immediately, with no further state inspection at
all. `SnakeGameTest#theGameStaysOverAfterALosingMove` locks this in.

The fourth scenario swaps in `EveryNMovesGrowthPolicy(5)` with zero changes to `SnakeGame` —
proving ④'s extensibility claim, not just asserting it.

## ⑧ Concurrency & Extensibility

**Concurrency:** explicitly out of scope — the problem never raises it.

**Extensibility:**
- A new growth rule (e.g. "grows every time the snake crosses the board's midpoint") → one new
  `GrowthPolicy` implementation, zero changes to `SnakeGame` — proven by ⑦'s fourth scenario.
- A different self-collision rule (e.g. the snake can pass through its own tail segment even
  while growing, in some variant) is **not** a drop-in extension today — `Snake.wouldCollide`
  hardcodes the one tail-exclusion rule. If that needed to vary too, it would become a third
  Strategy, the same shape as `GrowthPolicy`.
