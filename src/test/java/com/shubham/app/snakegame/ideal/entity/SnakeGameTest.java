package com.shubham.app.snakegame.ideal.entity;

import org.junit.jupiter.api.Test;

import com.shubham.app.snakegame.ideal.exception.InvalidBoardConfigException;
import com.shubham.app.snakegame.ideal.exception.InvalidGrowthConfigException;
import com.shubham.app.snakegame.ideal.service.EveryNMovesGrowthPolicy;
import com.shubham.app.snakegame.ideal.service.FoodGrowthPolicy;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SnakeGameTest {

    @Test
    void constructorRejectsANullBoardOrGrowthPolicy() {
        assertThrows(InvalidBoardConfigException.class, () -> new SnakeGame(null, new FoodGrowthPolicy(List.of())));
        assertThrows(InvalidGrowthConfigException.class, () -> new SnakeGame(new Board(3, 3), null));
    }

    // LeetCode 353's own official example 1.
    @Test
    void matchesTheOfficialLeetCode353Example() {
        SnakeGame game = new SnakeGame(new Board(2, 3),
                new FoodGrowthPolicy(List.of(new Position(1, 2), new Position(0, 1))));

        assertEquals(0, game.move(Direction.RIGHT));
        assertEquals(0, game.move(Direction.DOWN));
        assertEquals(1, game.move(Direction.RIGHT));
        assertEquals(1, game.move(Direction.UP));
        assertEquals(2, game.move(Direction.LEFT));
        assertEquals(-1, game.move(Direction.UP));
    }

    // The practice code's bug: a length-2 snake reversing onto its own (vacating)
    // tail was
    // wrongly treated as a collision.
    @Test
    void reversingOntoTheVacatingTailIsLegal() {
        SnakeGame game = new SnakeGame(new Board(1, 3), new FoodGrowthPolicy(List.of(new Position(0, 1))));

        assertEquals(1, game.move(Direction.RIGHT));
        assertEquals(1, game.move(Direction.LEFT));
    }

    // The practice code's other bug: a losing move never mutated the snake, so a
    // second move()
    // call after -1 would silently resume play.
    @Test
    void theGameStaysOverAfterALosingMove() {
        SnakeGame game = new SnakeGame(new Board(3, 3), new FoodGrowthPolicy(List.of()));

        assertEquals(-1, game.move(Direction.UP));
        assertEquals(-1, game.move(Direction.RIGHT));
        assertEquals(-1, game.move(Direction.DOWN));
    }

    // Proves the ④ extensibility claim: the reported Atlassian variant plugs in
    // through the same
    // GrowthPolicy seam, with zero changes to SnakeGame.
    @Test
    void everyNMovesGrowthPolicyGrowsOnTheFifthMoveNotBefore() {
        SnakeGame game = new SnakeGame(new Board(10, 10), new EveryNMovesGrowthPolicy(5));

        assertEquals(0, game.move(Direction.RIGHT));
        assertEquals(0, game.move(Direction.RIGHT));
        assertEquals(0, game.move(Direction.RIGHT));
        assertEquals(0, game.move(Direction.RIGHT));
        assertEquals(1, game.move(Direction.RIGHT));
        assertEquals(1, game.move(Direction.DOWN));
    }
}
