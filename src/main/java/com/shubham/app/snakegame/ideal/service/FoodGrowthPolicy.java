package com.shubham.app.snakegame.ideal.service;

import com.shubham.app.snakegame.ideal.entity.GrowthPolicy;
import com.shubham.app.snakegame.ideal.entity.Position;

import java.util.List;

/**
 * Standard LeetCode 353: the snake grows when its head lands on the next food
 * item, in the order the food list was given. Consumed food is never offered
 * again.
 */
public class FoodGrowthPolicy implements GrowthPolicy {

    private final List<Position> foods;
    private int nextFoodIndex;

    public FoodGrowthPolicy(List<Position> foods) {
        this.foods = foods == null ? List.of() : List.copyOf(foods);
    }

    @Override
    public boolean shouldGrow(Position nextHeadPosition) {
        if (nextFoodIndex >= foods.size()) {
            return false;
        }
        if (!foods.get(nextFoodIndex).equals(nextHeadPosition)) {
            return false;
        }
        nextFoodIndex++;
        return true;
    }
}
