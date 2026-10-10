package com.shubham.app.snakegame.ideal.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SnakeTest {

    @Test
    void aFreshCellIsNeverACollision() {
        Snake snake = new Snake(new Position(0, 0));
        assertFalse(snake.wouldCollide(new Position(5, 5), false));
    }

    @Test
    void anOccupiedBodyCellIsACollisionWhenGrowing() {
        Snake snake = new Snake(new Position(0, 0));
        snake.move(new Position(0, 1), true); // grows: body is now (0,1),(0,0)

        assertTrue(snake.wouldCollide(new Position(0, 0), true));
    }

    // The fix for the practice code's bug: when the move doesn't grow the snake,
    // the current
    // tail cell is vacated in the same step - landing exactly on it is not a
    // collision.
    @Test
    void movingOntoTheCurrentTailIsNotACollisionWhenNotGrowing() {
        Snake snake = new Snake(new Position(0, 0));
        snake.move(new Position(0, 1), true); // grows: body is now (0,1),(0,0)

        assertFalse(snake.wouldCollide(new Position(0, 0), false));
    }

    @Test
    void movingOntoAnInteriorBodyCellIsStillACollisionEvenWhenNotGrowing() {
        Snake snake = new Snake(new Position(0, 0));
        snake.move(new Position(0, 1), true); // (0,1),(0,0)
        snake.move(new Position(1, 1), true); // (1,1),(0,1),(0,0)

        // (0,1) is now an interior cell, not the tail - still a real collision.
        assertTrue(snake.wouldCollide(new Position(0, 1), false));
    }

    @Test
    void moveWithoutGrowingKeepsLengthConstant() {
        Snake snake = new Snake(new Position(0, 0));
        snake.move(new Position(0, 1), false);

        assertEquals(1, snake.length());
    }

    @Test
    void moveWhileGrowingIncreasesLength() {
        Snake snake = new Snake(new Position(0, 0));
        snake.move(new Position(0, 1), true);

        assertEquals(2, snake.length());
    }
}
