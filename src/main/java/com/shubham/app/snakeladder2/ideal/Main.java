package com.shubham.app.snakeladder2.ideal;

import com.shubham.app.snakeladder2.ideal.entity.Board;
import com.shubham.app.snakeladder2.ideal.entity.BoardJump;
import com.shubham.app.snakeladder2.ideal.entity.Ladder;
import com.shubham.app.snakeladder2.ideal.entity.Player;
import com.shubham.app.snakeladder2.ideal.entity.Snake;
import com.shubham.app.snakeladder2.ideal.service.GamePlayService;
import com.shubham.app.snakeladder2.ideal.service.PlayerRegistrationService;
import com.shubham.app.snakeladder2.ideal.service.RandomDice;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        // same board config manually exercised in practice/README.md's original mock
        // attempt
        List<BoardJump> jumps = List.of(new Snake(50, 14), new Snake(45, 10), new Snake(23, 4), new Snake(98, 27),
                new Snake(93, 18), new Ladder(15, 67), new Ladder(33, 89));

        Board board = new Board(100, jumps);
        List<Player> players = new PlayerRegistrationService().registerPlayers(List.of("Shubham", "Ram"));
        GamePlayService gamePlayService = new GamePlayService(board, players, new RandomDice());

        boolean gameOver = false;
        while (!gameOver) {
            for (Player player : players) {
                boolean justEnded = gamePlayService.playTurn(player);
                System.out.println(player.getName() + " is now at position " + player.getPosition());

                if (justEnded) {
                    gameOver = true;
                    break;
                }
            }
        }

        gamePlayService.getWinner().ifPresent(winner -> System.out.println(winner.getName() + " wins!"));
    }
}
