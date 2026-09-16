package com.shubham.app.snakeladder2.practice.entity;

import java.util.List;

public class Game {

    private List<User> users;
    private Board board;

    public Game(List<User> user, Board board) {
        this.users = user;
        this.board = board;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    @Override
    public String toString() {
        return "Game{" + "users=" + users + ", board=" + board + '}';
    }
}
