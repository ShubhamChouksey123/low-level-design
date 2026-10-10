package com.shubham.app.snakegame.practice.service;

import com.shubham.app.snakegame.practice.entity.*;
import com.shubham.app.snakegame.practice.entity.Board;

public class SnakeGame {

    private Snake snake;
    private Board board;
    private int score;
    private int indexToEat;

    public SnakeGame(Board board) {
        this.board = board;
        this.snake = new Snake(board);
        this.score = 0;
        this.indexToEat = 0;
    }

    private boolean liesOutSideBoundry(Position position) {

        if (position.x() < 0 || position.x() >= board.getHeight() || position.y() < 0
                || position.y() >= board.getWidth())
            return true;
        return false;
    }

    private boolean isFoodPosition(Position positionHead) {

        if (board.getFoods().size() <= indexToEat)
            return false;

        Position foodPosition = board.getFoods().get(indexToEat);

        if (foodPosition.x() == positionHead.x() && foodPosition.y() == positionHead.y())
            return true;
        return false;
    }

    public int move(Direction direction) {
        Position nextPosition = snake.getNextPosition(direction);

        if (liesOutSideBoundry(nextPosition))
            return -1;

        boolean grow = isFoodPosition(nextPosition);
        if (snake.wouldCollide(nextPosition, grow))
            return -1;

        if (grow) {
            snake.moveAndIncreaseLength(direction);
            score++;
            indexToEat++;
        } else {
            snake.onlyMove(direction);
        }
        snake.print();
        return score;
    }
}
