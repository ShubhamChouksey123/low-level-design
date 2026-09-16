package com.shubham.app.snakeladder2.ideal.service;

import com.shubham.app.snakeladder2.ideal.entity.Board;
import com.shubham.app.snakeladder2.ideal.entity.BoardJump;
import com.shubham.app.snakeladder2.ideal.entity.Dice;
import com.shubham.app.snakeladder2.ideal.entity.Player;

import java.util.List;
import java.util.Optional;

public class GamePlayService {

    private final Board board;
    private final List<Player> players;
    private final Dice dice;

    private Player winner;

    public GamePlayService(Board board, List<Player> players, Dice dice) {
        this.board = board;
        this.players = players;
        this.dice = dice;
    }

    // caller must check this after every single turn and stop the round immediately
    // — not just
    // between rounds
    public boolean playTurn(Player player) {
        if (isGameOver()) {
            return true;
        }

        int roll = dice.roll();
        int newPosition = player.getPosition() + roll;

        if (newPosition > board.getLength()) {
            return false;
        }

        Optional<BoardJump> jump = board.getJumpFrom(newPosition);
        if (jump.isPresent()) {
            newPosition = jump.get().getEnd();
        }

        player.moveTo(newPosition);

        if (newPosition == board.getLength()) {
            winner = player;
        }

        return isGameOver();
    }

    public boolean isGameOver() {
        return winner != null;
    }

    public Optional<Player> getWinner() {
        return Optional.ofNullable(winner);
    }

    public Board getBoard() {
        return board;
    }

    public List<Player> getPlayers() {
        return List.copyOf(players);
    }
}
