package com.shubham.app.snakeladder2.practice.service;

import com.shubham.app.snakeladder2.practice.entity.Game;
import com.shubham.app.snakeladder2.practice.entity.Ladder;
import com.shubham.app.snakeladder2.practice.entity.Snake;
import com.shubham.app.snakeladder2.practice.entity.User;

import java.util.Random;

public class GameControllerService {

    private Game game;
    private Random random;

    public GameControllerService(Game game) {
        this.game = game;
        this.random = new Random();
    }

    public void rollDice(int userId) {

        if (checkWinner(game)) {
            throw new IllegalArgumentException("Game finished");
        }

        // new dice roll value
        int step = 1 + random.nextInt(6);
        System.out.println("user with id " + userId + " throw a dice and got " + step);
        updatePosition(userId, step);
    }

    private void updatePosition(int userId, int step) {

        User user = null;
        for (User player : game.getUsers()) {
            if (player.getUserId() == userId) {
                user = player;
            }
        }

        if (user == null) {
            throw new IllegalArgumentException("invalid user id");
        }

        int newPosition = user.getPosition() + step;
        if (newPosition > game.getBoard().getEnd())
            return;

        checkWinner(game);

        for (Snake snake : game.getBoard().getSnakes()) {
            if (snake.getStart() == newPosition) {
                System.out.println("user with id " + userId + " is hit a snake at position " + snake.getStart());
                newPosition = snake.getEnd();
            }
        }

        for (Ladder ladder : game.getBoard().getLadders()) {
            if (ladder.getStart() == newPosition) {
                newPosition = ladder.getEnd();
            }
        }

        user.setPosition(newPosition);
        checkWinner(game);
    }

    public boolean checkWinner(Game game) {

        User winnerUser = null;
        for (User player : game.getUsers()) {
            if (player.getPosition() == game.getBoard().getEnd()) {
                winnerUser = player;
                System.out.println("winner of the game is user : " + winnerUser.getName());
                return true;
            }
        }
        return false;
    }
}
