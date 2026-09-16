package com.shubham.app.snakeladder2.ideal.entity;

import org.junit.jupiter.api.Test;

import com.shubham.app.snakeladder2.ideal.exception.InvalidBoardConfigurationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoardTest {

    @Test
    void validBoardExposesJumpsByStartSquare() {
        Board board = new Board(100, List.of(new Snake(50, 14), new Ladder(15, 67)));

        assertEquals(14, board.getJumpFrom(50).orElseThrow().getEnd());
        assertEquals(67, board.getJumpFrom(15).orElseThrow().getEnd());
        assertTrue(board.getJumpFrom(99).isEmpty());
    }

    @Test
    void nonPositiveLengthIsRejected() {
        assertThrows(InvalidBoardConfigurationException.class, () -> new Board(0, List.of()));
    }

    @Test
    void jumpOutsideBoardRangeIsRejected() {
        assertThrows(InvalidBoardConfigurationException.class, () -> new Board(10, List.of(new Snake(50, 4))));
    }

    @Test
    void twoJumpsStartingOnTheSameSquareAreRejected() {
        assertThrows(InvalidBoardConfigurationException.class,
                () -> new Board(100, List.of(new Snake(50, 14), new Ladder(50, 89))));
    }
}
