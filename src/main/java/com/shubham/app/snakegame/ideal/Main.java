package com.shubham.app.snakegame.ideal;

import com.shubham.app.snakegame.ideal.entity.Board;
import com.shubham.app.snakegame.ideal.entity.Direction;
import com.shubham.app.snakegame.ideal.entity.Position;
import com.shubham.app.snakegame.ideal.entity.SnakeGame;
import com.shubham.app.snakegame.ideal.service.EveryNMovesGrowthPolicy;
import com.shubham.app.snakegame.ideal.service.FoodGrowthPolicy;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("Official LeetCode 353 example 1 (width=3, height=2, food=[[1,2],[0,1]]):");
        SnakeGame leetCodeGame = new SnakeGame(new Board(2, 3),
                new FoodGrowthPolicy(List.of(new Position(1, 2), new Position(0, 1))));
        System.out.println("  R: " + leetCodeGame.move(Direction.RIGHT));
        System.out.println("  D: " + leetCodeGame.move(Direction.DOWN));
        System.out.println("  R: " + leetCodeGame.move(Direction.RIGHT));
        System.out.println("  U: " + leetCodeGame.move(Direction.UP));
        System.out.println("  L: " + leetCodeGame.move(Direction.LEFT));
        System.out.println("  U: " + leetCodeGame.move(Direction.UP));

        System.out.println("\nTail-vacate fix: eat food to grow to length 2, then reverse onto the vacated tail cell:");
        SnakeGame reversalGame = new SnakeGame(new Board(1, 3), new FoodGrowthPolicy(List.of(new Position(0, 1))));
        System.out.println("  RIGHT (eats food, grows to length 2): " + reversalGame.move(Direction.RIGHT));
        System.out.println(
                "  LEFT (back onto the now-vacating tail, should be legal): " + reversalGame.move(Direction.LEFT));

        System.out.println("\nGame-over-stays-over fix: move out of bounds, then try to move again:");
        SnakeGame overGame = new SnakeGame(new Board(3, 3), new FoodGrowthPolicy(List.of()));
        System.out.println("  UP (out of bounds, ends the game): " + overGame.move(Direction.UP));
        System.out.println("  RIGHT (game must stay over): " + overGame.move(Direction.RIGHT));

        System.out.println("\nReported Atlassian variant (grows every 5 moves, no food) - same SnakeGame, only the "
                + "GrowthPolicy changes:");
        SnakeGame everyFiveGame = new SnakeGame(new Board(10, 10), new EveryNMovesGrowthPolicy(5));
        Direction[] moves = {Direction.RIGHT, Direction.RIGHT, Direction.RIGHT, Direction.RIGHT, Direction.RIGHT,
                Direction.DOWN};
        for (int i = 0; i < moves.length; i++) {
            System.out.println("  move " + (i + 1) + " (" + moves[i] + "): " + everyFiveGame.move(moves[i]));
        }
    }
}
