package com.shubham.app.snakeladder2.ideal.service;

import com.shubham.app.snakeladder2.ideal.entity.Dice;

public class FixedDice implements Dice {

    private final int[] rolls;
    private int index;

    public FixedDice(int... rolls) {
        this.rolls = rolls;
    }

    @Override
    public int roll() {
        int value = rolls[index % rolls.length];
        index++;
        return value;
    }
}
