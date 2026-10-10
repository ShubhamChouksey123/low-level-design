package com.shubham.app.snakegame.ideal.entity;

/**
 * The Strategy for "when does the snake grow?" - the one thing that actually
 * varies between the standard LeetCode 353 rule (grow when the head lands on
 * the next food item) and the reported Atlassian variant (grow every 5 moves,
 * regardless of food). Called once per legal move, with the candidate next head
 * position, and returns whether this move grows the snake.
 */
public interface GrowthPolicy {

    boolean shouldGrow(Position nextHeadPosition);
}
