package com.shubham.app.snakeladder;

import com.shubham.app.snakeladder.service.GameCreateService;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello world!");
        createGame();
    }

    private static void createGame() {
        GameCreateService gameCreateService = new GameCreateService();

        Scanner in = new Scanner(System.in);

        System.out.println("Please enter the number of numberOfSnakes :");
        int numberOfSnakes = in.nextInt();
        System.out.println("numberOfSnakes : " + numberOfSnakes);

        for (int i = 0; i < numberOfSnakes; i++) {
            System.out.println("Please enter the head of the " + (i + 1) + " th snake");
            int head = in.nextInt();
            System.out.println("Please enter the head of the " + (i + 1) + " th snake");
            int tail = in.nextInt();
            System.out.println("head : " + head + " and tail : " + tail);
            gameCreateService.addSnake(head, tail);
        }

        System.out.println("Please enter the number of numberOfLadders :");
        int numberOfLadders = in.nextInt();
        System.out.println("numberOfLadders : " + numberOfLadders);

        for (int i = 0; i < numberOfLadders; i++) {
            System.out.println("Please enter the start of the " + (i + 1) + " th ladder");
            int start = in.nextInt();
            System.out.println("Please enter the end of the " + (i + 1) + " th ladder");
            int end = in.nextInt();
            System.out.println("start : " + start + " and end : " + end);
            gameCreateService.addLadder(start, end);
        }

        System.out.println("Please enter the number of players :");
        int numberOfPlayers = in.nextInt();
        System.out.println("numberOfPlayers : " + numberOfPlayers);

        for (int i = 0; i < numberOfPlayers; i++) {
            System.out.println("Please enter the name of the " + (i + 1) + " th player");
            String playerName = in.nextLine();
            gameCreateService.addPlayer(playerName);
        }

        System.out.println("gameCreateService : " + gameCreateService);
    }
}
