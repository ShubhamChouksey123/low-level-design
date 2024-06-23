package com.shubham.app.snakeladder.game.create;

import java.util.ArrayList;
import java.util.List;

public class Board {

    private List<Snake> snakes;
    private List<Ladder> ladders;

    public Board() {
        snakes = new ArrayList<>();
        ladders = new ArrayList<>();
    }

    public Board(List<Snake> snakes, List<Ladder> ladders) {
        this.snakes = snakes;
        this.ladders = ladders;
    }

    public List<Snake> getSnakes() {
        return snakes;
    }

    public void setSnakes(List<Snake> snakes) {
        this.snakes = snakes;
    }

    public List<Ladder> getLadders() {
        return ladders;
    }

    public void setLadders(List<Ladder> ladders) {
        this.ladders = ladders;
    }

    @Override
    public String toString() {
        return "Board{" + "snakes=" + snakes + ", ladders=" + ladders + '}';
    }
}
