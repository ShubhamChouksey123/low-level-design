package com.shubham.app.snakeladder2.practice.entity;

import java.util.List;

public class Board {

    private int start;
    private int end;
    private List<Snake> snakes;
    private List<Ladder> ladders;

    public Board(int end, List<Snake> snakes, List<Ladder> ladders) {
        this.start = 0;
        this.end = end;
        this.snakes = snakes;
        this.ladders = ladders;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
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
        return "Board{" + "start=" + start + ", end=" + end + ", snakes=" + snakes + ", ladders=" + ladders + '}';
    }
}
