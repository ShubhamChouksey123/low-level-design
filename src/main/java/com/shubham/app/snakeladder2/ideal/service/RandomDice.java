package com.shubham.app.snakeladder2.ideal.service;

import com.shubham.app.snakeladder2.ideal.entity.Dice;

import java.util.Random;

public class RandomDice implements Dice {

    private final Random random;

    public RandomDice() {
        this(new Random());
    }

    public RandomDice(Random random) {
        this.random = random;
    }

    @Override
    public int roll() {
        return 1 + random.nextInt(6);
    }
}
