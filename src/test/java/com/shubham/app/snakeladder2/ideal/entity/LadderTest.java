package com.shubham.app.snakeladder2.ideal.entity;

import org.junit.jupiter.api.Test;

import com.shubham.app.snakeladder2.ideal.exception.InvalidBoardConfigurationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LadderTest {

    @Test
    void validLadderMovesPlayerUp() {
        Ladder ladder = new Ladder(15, 67);

        assertEquals(15, ladder.getStart());
        assertEquals(67, ladder.getEnd());
    }

    @Test
    void ladderGoingDownwardIsRejected() {
        assertThrows(InvalidBoardConfigurationException.class, () -> new Ladder(67, 15));
    }

    @Test
    void ladderWithEqualStartAndEndIsRejected() {
        assertThrows(InvalidBoardConfigurationException.class, () -> new Ladder(20, 20));
    }
}
