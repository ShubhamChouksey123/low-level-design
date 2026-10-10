package com.shubham.app.snakegame.ideal.service;

import org.junit.jupiter.api.Test;

import com.shubham.app.snakegame.ideal.entity.GrowthPolicy;
import com.shubham.app.snakegame.ideal.entity.Position;
import com.shubham.app.snakegame.ideal.exception.InvalidGrowthConfigException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EveryNMovesGrowthPolicyTest {

    private static final Position IGNORED = new Position(0, 0);

    @Test
    void constructorRejectsANonPositiveInterval() {
        assertThrows(InvalidGrowthConfigException.class, () -> new EveryNMovesGrowthPolicy(0));
        assertThrows(InvalidGrowthConfigException.class, () -> new EveryNMovesGrowthPolicy(-1));
    }

    @Test
    void growsOnlyOnTheIntervalMoveNotBefore() {
        GrowthPolicy policy = new EveryNMovesGrowthPolicy(3);

        assertFalse(policy.shouldGrow(IGNORED));
        assertFalse(policy.shouldGrow(IGNORED));
        assertTrue(policy.shouldGrow(IGNORED));
        assertFalse(policy.shouldGrow(IGNORED));
        assertFalse(policy.shouldGrow(IGNORED));
        assertTrue(policy.shouldGrow(IGNORED));
    }
}
