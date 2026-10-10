package com.shubham.app.snakegame.ideal.entity;

import com.shubham.app.snakegame.ideal.exception.InvalidBoardConfigException;
import com.shubham.app.snakegame.ideal.exception.InvalidGrowthConfigException;

/**
 * The snake always starts at (0, 0). {@code move} returns the current score
 * (snake length - 1), or -1 if this move ends the game.
 */
public class SnakeGame {

    private final Board board;
    private final Snake snake;
    private final GrowthPolicy growthPolicy;
    private boolean gameOver;

    public SnakeGame(Board board, GrowthPolicy growthPolicy) {
        if (board == null) {
            throw new InvalidBoardConfigException("board must not be null");
        }
        if (growthPolicy == null) {
            throw new InvalidGrowthConfigException("growthPolicy must not be null");
        }
        this.board = board;
        this.snake = new Snake(new Position(0, 0));
        this.growthPolicy = growthPolicy;
    }

    public int move(Direction direction) {
        // The practice code had no equivalent flag: a losing move never mutated the
        // snake, so a
        // second move() call after -1 would silently resume play from the pre-collision
        // state as
        // if the game had never ended.
        if (gameOver) {
            return -1;
        }

        Position head = snake.getHead();
        Position nextHead = new Position(head.x() + direction.getDx(), head.y() + direction.getDy());

        if (board.isOutside(nextHead)) {
            gameOver = true;
            return -1;
        }

        // Growth has to be decided before the collision check, not after - whether the
        // tail is
        // about to vacate its cell changes whether landing on it is a collision at all.
        boolean grow = growthPolicy.shouldGrow(nextHead);

        if (snake.wouldCollide(nextHead, grow)) {
            gameOver = true;
            return -1;
        }

        snake.move(nextHead, grow);
        return snake.length() - 1;
    }
}
