package com.shubham.app.snakeladder2.ideal.entity;

import com.shubham.app.snakeladder2.ideal.exception.InvalidBoardConfigurationException;

public class Snake implements BoardJump {

    private final int start;
    private final int end;

    public Snake(int start, int end) {
        if (start <= end) {
            throw new InvalidBoardConfigurationException(
                    "a snake must move a player down the board: start=" + start + " must be greater than end=" + end);
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
        return "Snake{" + "start=" + start + ", end=" + end + '}';
    }
}
