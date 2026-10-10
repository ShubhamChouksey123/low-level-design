package com.shubham.app.snakegame.ideal.entity;

import org.junit.jupiter.api.Test;

import com.shubham.app.snakegame.ideal.exception.InvalidBoardConfigException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoardTest {

    @Test
    void constructorRejectsANonPositiveHeightOrWidth() {
        assertThrows(InvalidBoardConfigException.class, () -> new Board(0, 3));
        assertThrows(InvalidBoardConfigException.class, () -> new Board(3, 0));
        assertThrows(InvalidBoardConfigException.class, () -> new Board(-1, 3));
    }

    @Test
    void isOutsideReflectsBothDimensions() {
        Board board = new Board(2, 3);

        assertFalse(board.isOutside(new Position(0, 0)));
        assertFalse(board.isOutside(new Position(1, 2)));
        assertTrue(board.isOutside(new Position(-1, 0)));
        assertTrue(board.isOutside(new Position(2, 0)));
        assertTrue(board.isOutside(new Position(0, 3)));
    }
}
