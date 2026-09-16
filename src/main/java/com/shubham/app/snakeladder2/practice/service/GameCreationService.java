package com.shubham.app.snakeladder2.practice.service;

import com.shubham.app.snakeladder2.practice.entity.*;

import java.util.ArrayList;
import java.util.List;

public class GameCreationService {

    public void registerUser(int id, String name, Game game) {
        User user = new User(id, name);
        game.getUsers().add(user);
    }

    public Game boardCreate(int length, List<Snake> snakes, List<Ladder> ladders) {
        Board board = new Board(length, snakes, ladders);
        // add validations for snake and ladder
        Game game = new Game(new ArrayList<>(), board);
        return game;
    }
}
