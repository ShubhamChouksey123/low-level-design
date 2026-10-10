# Design Snake Game

> `[LLD]` — reported as an Atlassian interview variant of
> [LeetCode 353 "Design Snake Game"](https://leetcode.com/problems/design-snake-game/). Moved here
> from the [DSA repo](https://github.com/ShubhamChouksey123/data-structures-java-solution/blob/master/docs/company-specific/atlassian/README.md#design--simulation-multi-part-escalating--closest-match-to-the-recruiter-described-format),
> which cites [leetcode.com/discuss/post/6807250](https://leetcode.com/discuss/post/6807250) as the
> source for the reported variant and the `deque + hash set` data-structure hint. This file only
> states the problem — no solution/design yet.
>
> **Verification note:** direct access to that source post was blocked by a Cloudflare bot
> challenge on this attempt (tried twice, including a reader-proxy fallback). A search-grounding
> fallback query returned a write-up, but it cited unrelated generic LeetCode-353 solution sites
> (not the actual discuss post) and closely echoed the exact phrasing of my own search query — a
> sign it was synthesized, not quoted — so it is **not** treated as a confirmed source here. What
> follows is LeetCode 353's standard, independently well-established ruleset, plus the reported
> variant and data-structure hint exactly as the DSA repo states them, with the variant explicitly
> flagged as reported-but-not-independently-reverified.

## Problem Statement

Design a Snake game that's played on a 2D grid/board.

- The snake starts at a fixed position (e.g. the top-left cell) with an initial length.
- The snake moves one cell at a time in one of four directions: up, down, left, right.
- A `move(direction)` call advances the snake by one step and returns the current score, or a
  sentinel value (e.g. `-1`) if the move ends the game.
- **Game over** if the snake's new head position goes outside the board boundaries, or the new
  head position collides with the snake's own body.
- **Standard LeetCode 353 growth rule:** the board has a list of food positions, given in order;
  the snake grows by one cell only when its head moves onto the next food position in that list
  (and that food item is then consumed — it won't appear again).
- **Reported Atlassian variant (per the DSA repo's bullet — not independently reverified here):**
  growth is time-based instead of food-based — the snake grows by one cell every 5 moves,
  regardless of food.

## Confirmed data-structure hint (per the DSA repo's bullet)

Reported solutions represent the snake's body with a **deque** (double-ended queue) — new head
pushed to the front each move, tail popped from the back unless the snake is growing that move —
plus a **hash set** of the same cells, used purely for O(1) self-collision checks on the new head
position. This combination is also exactly how the standard (non-variant) LeetCode 353 problem is
conventionally solved, which is independent, well-established corroboration of this specific
data-structure choice even though the Atlassian-specific report itself couldn't be re-verified
directly this time.

## Things to clarify before designing

- **Exact growth trigger for the variant.** "Every 5 moves" — does the 5-move counter start at
  move 1 and grow on move 5, 10, 15, ...? Does a game-ending move still count toward the counter?
  Not stated in what's confirmed here.
- **Does the variant keep food at all**, or does removing the food-based trigger mean food (and
  the board's food-position list) drops out of the design entirely? The DSA repo's bullet doesn't
  say whether this is "replace the growth trigger" or "replace the growth trigger and drop food
  as a concept."
- **Self-collision edge case at the tail.** In the standard problem, if the new head moves exactly
  onto the current tail's cell *and* the tail is about to be popped that same move (i.e. the
  snake isn't growing this move), that's **not** a collision — the tail cell is vacated at the
  same instant the head arrives. Worth confirming this edge case still applies under the
  every-5-moves variant, since growth timing changes exactly when the tail is or isn't popped.
- **Board size / coordinate system.** Not stated in the confirmed source material — likely given
  as width/height at construction time, matching the standard problem's own constructor shape.
