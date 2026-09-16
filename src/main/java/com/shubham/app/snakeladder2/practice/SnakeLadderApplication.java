package com.shubham.app.snakeladder2.practice;

import com.shubham.app.snakeladder2.practice.entity.*;
import com.shubham.app.snakeladder2.practice.service.GameControllerService;
import com.shubham.app.snakeladder2.practice.service.GameCreationService;

import java.util.*;
import java.util.Scanner;

public class SnakeLadderApplication {

    public static void main(String[] args) {

        GameCreationService gameServiceCreation = new GameCreationService();
        System.out.println("Welcome to Snake Ladder Game");
        System.out.println("Type the length of the board from 100 to 1000");
        Scanner scanner = new Scanner(System.in);
        int length = scanner.nextInt();

        System.out.println("Type the number of snakes on the board");
        int snakesCount = scanner.nextInt();
        List<Snake> snakes = new ArrayList<>();

        for (int i = 0; i < snakesCount; i++) {
            System.out.println("Type the start and end of the " + (i + 1) + "th snake");
            int snakeStart = scanner.nextInt();
            int snakeEnd = scanner.nextInt();
            snakes.add(new Snake(snakeStart, snakeEnd));
        }

        System.out.println("Type the number of ladders on the board");
        int laddersCount = scanner.nextInt();
        List<Ladder> ladders = new ArrayList<>();

        for (int i = 0; i < laddersCount; i++) {
            System.out.println("Type the start and end of the " + (i + 1) + "th ladder");
            int ladderStart = scanner.nextInt();
            int ladderEnd = scanner.nextInt();
            ladders.add(new Ladder(ladderStart, ladderEnd));
        }
        scanner.nextLine();

        Game game = gameServiceCreation.boardCreate(length, snakes, ladders);

        System.out.println("Type the number of Players playing the game");
        int playersCount = Integer.parseInt(scanner.nextLine());

        for (int i = 0; i < playersCount; i++) {
            System.out.println("Type the name of the " + (i + 1) + "th player");
            String name = scanner.nextLine();
            gameServiceCreation.registerUser(i, name, game);
        }
        printBoard(game);

        GameControllerService controller = new GameControllerService(game);
        while (!controller.checkWinner(game)) {
            for (int j = 0; j < game.getUsers().size(); j++) {
                controller.rollDice(j);
            }
            printPosition(game);
        }

        scanner.close();
    }

    private static void printPosition(Game game) {

        for (User player : game.getUsers()) {
            System.out.println("user with id : " + player.getUserId() + " and name : " + player.getName()
                    + " is at position : " + player.getPosition());
        }
    }

    private static void printBoard(Game game) {

        for (Snake snake : game.getBoard().getSnakes()) {
            System.out.println(snake);
        }
        for (Ladder ladder : game.getBoard().getLadders()) {
            System.out.println(ladder);
        }
        for (User user : game.getUsers()) {
            System.out.println(user);
        }
    }
}
