package com.shubham.app.snakeladder2.ideal.entity;

import com.shubham.app.snakeladder2.ideal.exception.InvalidBoardConfigurationException;

public class Ladder implements BoardJump {

    private final int start;
    private final int end;

    public Ladder(int start, int end) {
        if (start >= end) {
            throw new InvalidBoardConfigurationException(
                    "a ladder must move a player up the board: start=" + start + " must be less than end=" + end);
        }
        this.start = start;
        this.end = end;
    }

    @Override
    public int getStart() {
        return start;
    }

    @Override
    public int getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return "Ladder{" + "start=" + start + ", end=" + end + '}';
    }
}
