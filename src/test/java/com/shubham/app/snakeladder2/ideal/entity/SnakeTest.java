package com.shubham.app.snakeladder2.ideal.entity;

import org.junit.jupiter.api.Test;

import com.shubham.app.snakeladder2.ideal.exception.InvalidBoardConfigurationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SnakeTest {

    @Test
    void validSnakeMovesPlayerDown() {
        Snake snake = new Snake(50, 14);

        assertEquals(50, snake.getStart());
        assertEquals(14, snake.getEnd());
    }

    @Test
    void snakeGoingUpwardIsRejected() {
        assertThrows(InvalidBoardConfigurationException.class, () -> new Snake(14, 50));
    }

    @Test
    void snakeWithEqualStartAndEndIsRejected() {
        assertThrows(InvalidBoardConfigurationException.class, () -> new Snake(20, 20));
    }
}
