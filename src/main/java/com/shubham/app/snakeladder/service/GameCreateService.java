package com.shubham.app.snakeladder.service;

import com.shubham.app.snakeladder.game.create.Board;
import com.shubham.app.snakeladder.game.create.Ladder;
import com.shubham.app.snakeladder.game.create.Snake;
import com.shubham.app.snakeladder.user.Player;

import java.util.ArrayList;
import java.util.List;

public class GameCreateService {

    private Board board;
    private List<Player> players;

    public GameCreateService() {
        board = new Board();
        players = new ArrayList<>();
    }

    public GameCreateService(Board board, List<Player> players) {
        this.board = board;
        this.players = players;
    }

    public void addSnake(Integer start, Integer end) {
        Snake snake = new Snake(start, end);
        board.getSnakes().add(snake);
    }

    public void addLadder(Integer start, Integer end) {
        Ladder ladder = new Ladder(start, end);
        board.getLadders().add(ladder);
    }

    public void addPlayer(String name) {
        Player player = new Player(name);
        players.add(player);
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    @Override
    public String toString() {
        return "GameCreateService{" + "board=" + board + ", players=" + players + '}';
    }
}
