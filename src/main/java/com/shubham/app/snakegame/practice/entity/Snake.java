package com.shubham.app.snakegame.practice.entity;

import java.util.ArrayDeque;
import java.util.Deque;

public class Snake {

    private Deque<Position> snakeNodes;
    private boolean[][] isOccupied;

    public Snake(Board board) {
        this.snakeNodes = new ArrayDeque<>();
        snakeNodes.offerFirst(new Position(0, 0));
        this.isOccupied = new boolean[board.getHeight()][board.getWidth()];
        isOccupied[0][0] = true;
    }

    public Position getHead() {
        return snakeNodes.peekFirst();
    }

    public Position getNextPosition(Direction direction) {
        int x = getHead().x() + direction.getX();
        int y = getHead().y() + direction.getY();

        return new Position(x, y);
    }

    public boolean wouldCollide(Position nextPosition, boolean grow) {
        if (!isOccupied[nextPosition.x()][nextPosition.y()]) {
            return false;
        }
        Position tail = snakeNodes.peekLast();
        if (!grow && nextPosition.equals(tail)) {
            return false;
        }
        return true;
    }

    public void onlyMove(Direction direction) {
        int x = getHead().x() + direction.getX();
        int y = getHead().y() + direction.getY();
        Position newHead = new Position(x, y);
        snakeNodes.offerFirst(newHead);
        isOccupied[x][y] = true;
        Position tail = snakeNodes.pollLast();
        isOccupied[tail.x()][tail.y()] = false;
    }

    public void moveAndIncreaseLength(Direction direction) {
        int x = getHead().x() + direction.getX();
        int y = getHead().y() + direction.getY();
        Position newHead = new Position(x, y);
        snakeNodes.offerFirst(newHead);
        isOccupied[x][y] = true;
    }

    public void print() {
        System.out.println("snakeNodes :  " + snakeNodes);
    }
}
