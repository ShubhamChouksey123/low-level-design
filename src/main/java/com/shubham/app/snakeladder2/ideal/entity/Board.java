package com.shubham.app.snakeladder2.ideal.entity;

import com.shubham.app.snakeladder2.ideal.exception.InvalidBoardConfigurationException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Board {

    private final int length;
    private final Map<Integer, BoardJump> jumpsByStart;

    public Board(int length, List<BoardJump> jumps) {
        if (length <= 0) {
            throw new InvalidBoardConfigurationException("board length must be positive, got " + length);
        }
        this.length = length;
        this.jumpsByStart = new HashMap<>();

        for (BoardJump jump : jumps) {
            validateWithinBounds(jump);
            if (jumpsByStart.containsKey(jump.getStart())) {
                throw new InvalidBoardConfigurationException(
                        "two jumps cannot start at the same square: " + jump.getStart());
            }
            jumpsByStart.put(jump.getStart(), jump);
        }
    }

    private void validateWithinBounds(BoardJump jump) {
        if (jump.getStart() < 1 || jump.getStart() > length) {
            throw new InvalidBoardConfigurationException(
                    "jump start " + jump.getStart() + " is outside board range [1, " + length + "]");
        }
        if (jump.getEnd() < 1 || jump.getEnd() > length) {
            throw new InvalidBoardConfigurationException(
                    "jump end " + jump.getEnd() + " is outside board range [1, " + length + "]");
        }
    }

    public int getLength() {
        return length;
    }

    public Optional<BoardJump> getJumpFrom(int square) {
        return Optional.ofNullable(jumpsByStart.get(square));
    }
}
