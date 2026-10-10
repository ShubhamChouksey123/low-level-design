package com.shubham.app.snakegame.practice;

import com.shubham.app.snakegame.practice.entity.Board;
import com.shubham.app.snakegame.practice.entity.Direction;
import com.shubham.app.snakegame.practice.entity.Position;
import com.shubham.app.snakegame.practice.service.SnakeGame;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        List<Position> foods = new ArrayList<>();
        foods.add(new Position(1, 2));
        foods.add(new Position(0, 1));
        Board board = new Board(2, 3, foods);

        SnakeGame snakeGame = new SnakeGame(board);
        int result = 0;

        result = snakeGame.move(Direction.RIGHT);
        System.out.println("result : " + result);
        result = snakeGame.move(Direction.DOWN);
        System.out.println("result : " + result);
        result = snakeGame.move(Direction.RIGHT);
        System.out.println("result : " + result);

        result = snakeGame.move(Direction.UP);
        System.out.println("result : " + result);
        result = snakeGame.move(Direction.LEFT);
        System.out.println("result : " + result);
        result = snakeGame.move(Direction.UP);
        System.out.println("result : " + result);
    }
}
