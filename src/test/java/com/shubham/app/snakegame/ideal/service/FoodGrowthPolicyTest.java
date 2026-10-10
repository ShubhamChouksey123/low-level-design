package com.shubham.app.snakegame.ideal.service;

import org.junit.jupiter.api.Test;

import com.shubham.app.snakegame.ideal.entity.GrowthPolicy;
import com.shubham.app.snakegame.ideal.entity.Position;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FoodGrowthPolicyTest {

    @Test
    void growsOnlyWhenTheNextHeadMatchesTheNextFoodInOrder() {
        GrowthPolicy policy = new FoodGrowthPolicy(List.of(new Position(0, 1), new Position(1, 1)));

        assertFalse(policy.shouldGrow(new Position(0, 0)));
        assertTrue(policy.shouldGrow(new Position(0, 1)));
        // The first food is already consumed - landing on it again doesn't grow a
        // second time.
        assertFalse(policy.shouldGrow(new Position(0, 1)));
        assertTrue(policy.shouldGrow(new Position(1, 1)));
    }

    @Test
    void aNullFoodListNeverGrows() {
        GrowthPolicy policy = new FoodGrowthPolicy(null);
        assertFalse(policy.shouldGrow(new Position(0, 0)));
    }

    @Test
    void neverGrowsOnceAllFoodIsConsumed() {
        GrowthPolicy policy = new FoodGrowthPolicy(List.of(new Position(0, 1)));
        policy.shouldGrow(new Position(0, 1));

        assertFalse(policy.shouldGrow(new Position(0, 1)));
    }
}
