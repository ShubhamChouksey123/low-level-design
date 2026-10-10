package com.shubham.app.snakegame.ideal.service;

import com.shubham.app.snakegame.ideal.entity.GrowthPolicy;
import com.shubham.app.snakegame.ideal.entity.Position;
import com.shubham.app.snakegame.ideal.exception.InvalidGrowthConfigException;

/**
 * The reported Atlassian variant: the snake grows every {@code interval} moves,
 * regardless of food - the 5th, 10th, 15th, ... move each grows it by one.
 */
public class EveryNMovesGrowthPolicy implements GrowthPolicy {

    private final int interval;
    private int moveCount;

    public EveryNMovesGrowthPolicy(int interval) {
        if (interval <= 0) {
            throw new InvalidGrowthConfigException("interval must be positive: " + interval);
        }
        this.interval = interval;
    }

    @Override
    public boolean shouldGrow(Position nextHeadPosition) {
        moveCount++;
        return moveCount % interval == 0;
    }
}
